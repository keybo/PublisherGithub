package com.example.metaservice;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.SystemClock;
import android.text.Html;
import android.util.Log;
import android.util.Pair;

import com.example.metaactivity.MainActivity;
import com.example.metanews.Constants;
import com.example.metanews.MainApplication;
import com.example.metanews.R;
import com.example.metaprovider.FeedData;
import com.example.metaprovider.FeedData.EntryColumns;
import com.example.metaprovider.FeedData.FeedColumns;
import com.example.metaprovider.FeedData.TaskColumns;
import com.example.metaprovider.FeedDataContentProvider;
import com.example.metautils.NetworkUtils;
import com.example.metautils.PrefUtils;
import com.meta.pubfeed.JSONFunctions;

public class FetcherService extends IntentService {

	public static final String ACTION_REFRESH_FEEDS = "com.example.metaprovider.REFRESH";
	public static final String ACTION_MOBILIZE_FEEDS = "com.example.metaprovider.MOBILIZE_FEEDS";

	private static final int THREAD_NUMBER = 3;
	private static final int MAX_TASK_ATTEMPT = 3;

	private static final String MOBILIZER_URL = "http://ftr.fivefilters.org/makefulltextfeed.php?url=";
	private static final String[][] TIMEZONES_REPLACE = { { "MEST", "+0200" },
			{ "EST", "-0500" }, { "PST", "-0800" } };
	private static final DateFormat[] PUBDATE_DATE_FORMATS = {
			new SimpleDateFormat("d' 'MMM' 'yyyy' 'HH:mm:ss", Locale.US),
			new SimpleDateFormat("d' 'MMM' 'yyyy' 'HH:mm:ss' 'Z", Locale.US),
			new SimpleDateFormat("d' 'MMM' 'yyyy' 'HH:mm:ss' 'z", Locale.US) };

	private static final DateFormat[] UPDATE_DATE_FORMATS = {
			new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss", Locale.US),
			new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ssZ", Locale.US),
			new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss.SSSz", Locale.US),
			new SimpleDateFormat("yyyy-MM-dd", Locale.US) };
	private long now = System.currentTimeMillis();
	
	
	
	private static final String COUNT = "COUNT(*)";



	private static final String SERVICENAME = "RssFetcherService";
	private static final String URL_SPACE = "%20";
	JSONArray jsonarray;
	private Uri updateUriForFeed;
	private final ArrayList<ContentProviderOperation> inserts = new ArrayList<ContentProviderOperation>();
	/*
	 * Allow different positions of the "rel" attribute w.r.t. the "href"
	 * attribute
	 */
	

	// middle() is group 1; s* is important for non-whitespaces; ' also usable
	private static final Pattern IMG_PATTERN = Pattern.compile(
			"<img\\s+[^>]*src=\\s*['\"]([^'\"]+)['\"][^>]*>",
			Pattern.CASE_INSENSITIVE);

	private static final Pattern DIV_TAG = Pattern
			.compile("<div([^>]*>.*)</div>");

	private NotificationManager mNotifMgr;

	public FetcherService() {
		super(SERVICENAME);
		HttpURLConnection.setFollowRedirects(true);
	}

	@Override
	public void onHandleIntent(Intent intent) {
		if (intent == null) { // No intent, we quit
			return;
		}

		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		final NetworkInfo networkInfo = connectivityManager
				.getActiveNetworkInfo();
		// Connectivity issue, we quit
		if (networkInfo == null
				|| networkInfo.getState() != NetworkInfo.State.CONNECTED) {
			return;
		}

		boolean isFromAutoRefresh = intent.getBooleanExtra(
				Constants.FROM_AUTO_REFRESH, false);
		boolean skipFetch = isFromAutoRefresh
				&& PrefUtils.getBoolean(PrefUtils.REFRESH_WIFI_ONLY, false)
				&& networkInfo.getType() != ConnectivityManager.TYPE_WIFI;
		// We need to skip the fetching process, so we quit
		if (skipFetch) {
			return;
		}

		if (ACTION_MOBILIZE_FEEDS.equals(intent.getAction())) {
			mobilizeAllEntries();
			downloadAllImages();

		} else { // == Constants.ACTION_REFRESH_FEEDS
			PrefUtils.putBoolean(PrefUtils.IS_REFRESHING, true);

			if (isFromAutoRefresh) {
				PrefUtils.putLong(PrefUtils.LAST_SCHEDULED_REFRESH,
						SystemClock.elapsedRealtime());
			}

			String feedId = intent.getStringExtra(Constants.FEED_ID);
			Log.e("Feed id" + feedId, "Feed id" + feedId);
			int newCount = (feedId == null ? refreshFeeds()
					: refreshFeed(feedId));

			if (newCount > 0) {
				if (PrefUtils.getBoolean(PrefUtils.NOTIFICATIONS_ENABLED, true)) {
					Cursor cursor = getContentResolver().query(
							EntryColumns.CONTENT_URI, new String[] { COUNT },
							EntryColumns.WHERE_UNREAD, null, null);

					cursor.moveToFirst();
					newCount = cursor.getInt(0); // The number has possibly
													// changed
					cursor.close();

					if (newCount > 0) {
						String text = String.valueOf(newCount) + ' '
								+ getString(R.string.new_entries);

						Intent notificationIntent = new Intent(
								FetcherService.this, MainActivity.class);
						PendingIntent contentIntent = PendingIntent
								.getActivity(FetcherService.this, 0,
										notificationIntent,
										PendingIntent.FLAG_UPDATE_CURRENT);

						Notification.Builder notifBuilder = new Notification.Builder(
								MainApplication.getContext()) //
								.setContentIntent(contentIntent) //
								.setSmallIcon(R.drawable.ic_statusbar_rss)
								//
								.setLargeIcon(
										BitmapFactory.decodeResource(
												getResources(),
												R.drawable.ic_launcher)) //
								.setTicker(text) //
								.setWhen(System.currentTimeMillis()) //
								.setAutoCancel(true) //
								.setContentTitle(
										getString(R.string.feedex_feeds)) //
								.setContentText(text) //
								.setLights(0xffffffff, 300, 1000);

						if (PrefUtils.getBoolean(
								PrefUtils.NOTIFICATIONS_VIBRATE, false)) {
							notifBuilder.setVibrate(new long[] { 0, 1000 });
						}

						String ringtone = PrefUtils.getString(
								PrefUtils.NOTIFICATIONS_RINGTONE, null);
						if (ringtone != null && ringtone.length() > 0) {
							notifBuilder.setSound(Uri.parse(ringtone));
						}

						mNotifMgr.notify(0, notifBuilder.getNotification());
					}
				} else {
					mNotifMgr.cancel(0);
				}
			}

			mobilizeAllEntries();
			downloadAllImages();

			PrefUtils.putBoolean(PrefUtils.IS_REFRESHING, false);
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mNotifMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
	}

	public static long getMobilizingTaskId(long entryId) {
		long result = -1;

		Cursor cursor = MainApplication
				.getContext()
				.getContentResolver()
				.query(TaskColumns.CONTENT_URI,
						TaskColumns.PROJECTION_ID,
						TaskColumns.ENTRY_ID + '=' + entryId + Constants.DB_AND
								+ TaskColumns.IMG_URL_TO_DL
								+ Constants.DB_IS_NULL, null, null);
		if (cursor.moveToFirst()) {
			result = cursor.getLong(0);
		}
		cursor.close();

		return result;
	}

	public static void addImagesToDownload(String entryId, Vector<String> images) {
		if (images != null) {
			ContentValues[] values = new ContentValues[images.size()];
			for (int i = 0; i < images.size(); i++) {
				values[i] = new ContentValues();
				values[i].put(TaskColumns.ENTRY_ID, entryId);
				values[i].put(TaskColumns.IMG_URL_TO_DL, images.get(i));
			}

			MainApplication.getContext().getContentResolver()
					.bulkInsert(TaskColumns.CONTENT_URI, values);
		}
	}

	public static void addEntriesToMobilize(long[] entriesId) {
		ContentValues[] values = new ContentValues[entriesId.length];
		for (int i = 0; i < entriesId.length; i++) {
			values[i] = new ContentValues();
			values[i].put(TaskColumns.ENTRY_ID, entriesId[i]);
		}

		MainApplication.getContext().getContentResolver()
				.bulkInsert(TaskColumns.CONTENT_URI, values);
	}

	private void mobilizeAllEntries() {
		ContentResolver cr = getContentResolver();
		Cursor cursor = cr.query(TaskColumns.CONTENT_URI, new String[] {
				TaskColumns._ID, TaskColumns.ENTRY_ID,
				TaskColumns.NUMBER_ATTEMPT }, TaskColumns.IMG_URL_TO_DL
				+ Constants.DB_IS_NULL, null, null);

		ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();

		while (cursor.moveToNext()) {
			long taskId = cursor.getLong(0);
			long entryId = cursor.getLong(1);
			int nbAttempt = 0;
			if (!cursor.isNull(2)) {
				nbAttempt = cursor.getInt(2);
			}

			boolean success = false;

			Uri entryUri = EntryColumns.CONTENT_URI(entryId);
			Cursor entryCursor = cr.query(entryUri, null, null, null, null);

			if (entryCursor.moveToFirst()) {
				if (entryCursor.isNull(entryCursor
						.getColumnIndex(EntryColumns.MOBILIZED_HTML))) { // If
																			// we
																			// didn't
																			// already
																			// mobilized
																			// it
					int linkPosition = entryCursor
							.getColumnIndex(EntryColumns.LINK);
					HttpURLConnection connection = null;

					try {
						String link = entryCursor.getString(linkPosition);
						connection = NetworkUtils.setupConnection(MOBILIZER_URL
								+ link);
						BufferedReader reader = new BufferedReader(
								new InputStreamReader(NetworkUtils
										.getConnectionInputStream(connection)));

						StringBuilder sb = new StringBuilder();
						String line;
						while ((line = reader.readLine()) != null) {
							sb.append(line);
						}

						String mobilizedHtml = null;
						Pattern p = Pattern
								.compile("<description>[^<]*</description>.*<description>(.*)&lt;p&gt;&lt;em&gt;This entry passed through the");
						Matcher m = p.matcher(sb.toString());
						if (m.find()) {
							mobilizedHtml = m.toMatchResult().group(1);
						} else {
							p = Pattern
									.compile("<description>[^<]*</description>.*<description>(.*)</description>");
							m = p.matcher(sb.toString());
							if (m.find()) {
								mobilizedHtml = m.toMatchResult().group(1);
							}
						}

						if (mobilizedHtml != null) {
							String realHtml = Html.fromHtml(mobilizedHtml,
									null, null).toString();
							Pair<String, Vector<String>> improvedContent = improveHtmlContent(
									realHtml, PrefUtils.getBoolean(
											PrefUtils.FETCH_PICTURES, false));
							if (improvedContent.first != null) {
								ContentValues values = new ContentValues();
								values.put(EntryColumns.MOBILIZED_HTML,
										improvedContent.first);
								if (cr.update(entryUri, values, null, null) > 0) {
									success = true;
									operations
											.add(ContentProviderOperation
													.newDelete(
															TaskColumns
																	.CONTENT_URI(taskId))
													.build());
									addImagesToDownload(
											String.valueOf(entryId),
											improvedContent.second);
								}
							}
						}
					} catch (Throwable ignored) {
					} finally {
						if (connection != null) {
							connection.disconnect();
						}
					}
				} else { // We already mobilized it
					success = true;
					operations.add(ContentProviderOperation.newDelete(
							TaskColumns.CONTENT_URI(taskId)).build());
				}
			}
			entryCursor.close();

			if (!success) {
				if (nbAttempt + 1 > MAX_TASK_ATTEMPT) {
					operations.add(ContentProviderOperation.newDelete(
							TaskColumns.CONTENT_URI(taskId)).build());
				} else {
					ContentValues values = new ContentValues();
					values.put(TaskColumns.NUMBER_ATTEMPT, nbAttempt + 1);
					operations.add(ContentProviderOperation
							.newUpdate(TaskColumns.CONTENT_URI(taskId))
							.withValues(values).build());
				}
			}
		}

		cursor.close();

		if (!operations.isEmpty()) {
			try {
				cr.applyBatch(FeedData.AUTHORITY, operations);
			} catch (Throwable ignored) {
			}
		}
	}

	private void downloadAllImages() {
		ContentResolver cr = MainApplication.getContext().getContentResolver();
		Cursor cursor = cr.query(FeedData.TaskColumns.CONTENT_URI,
				new String[] { FeedData.TaskColumns._ID,
						FeedData.TaskColumns.ENTRY_ID,
						FeedData.TaskColumns.IMG_URL_TO_DL,
						FeedData.TaskColumns.NUMBER_ATTEMPT },
				FeedData.TaskColumns.IMG_URL_TO_DL + Constants.DB_IS_NOT_NULL,
				null, null);

		ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();

		while (cursor.moveToNext()) {
			long taskId = cursor.getLong(0);
			long entryId = cursor.getLong(1);
			String imgPath = cursor.getString(2);
			int nbAttempt = 0;
			if (!cursor.isNull(3)) {
				nbAttempt = cursor.getInt(3);
			}

			try {
				NetworkUtils.downloadImage(entryId, imgPath);

				// If we are here, everything is OK
				operations.add(ContentProviderOperation.newDelete(
						FeedData.TaskColumns.CONTENT_URI(taskId)).build());
			} catch (Exception e) {
				if (nbAttempt + 1 > MAX_TASK_ATTEMPT) {
					operations.add(ContentProviderOperation.newDelete(
							FeedData.TaskColumns.CONTENT_URI(taskId)).build());
				} else {
					ContentValues values = new ContentValues();
					values.put(FeedData.TaskColumns.NUMBER_ATTEMPT,
							nbAttempt + 1);
					operations
							.add(ContentProviderOperation
									.newUpdate(
											FeedData.TaskColumns
													.CONTENT_URI(taskId))
									.withValues(values).build());
				}
			}
		}

		cursor.close();

		if (!operations.isEmpty()) {
			try {
				cr.applyBatch(FeedData.AUTHORITY, operations);
			} catch (Throwable ignored) {
			}
		}
	}

	private int refreshFeeds() {
		ContentResolver cr = getContentResolver();
		final Cursor cursor = cr.query(FeedColumns.CONTENT_URI,
				FeedColumns.PROJECTION_ID, null, null, null);
		int nbFeed = cursor.getCount();

		ExecutorService executor = Executors.newFixedThreadPool(THREAD_NUMBER,
				new ThreadFactory() {
					@Override
					public Thread newThread(Runnable r) {
						Thread t = new Thread(r);
						t.setPriority(Thread.MIN_PRIORITY);
						return t;
					}
				});

		CompletionService<Integer> completionService = new ExecutorCompletionService<Integer>(
				executor);
		while (cursor.moveToNext()) {
			final String feedId = cursor.getString(0);
			completionService.submit(new Callable<Integer>() {
				@Override
				public Integer call() {
					int result = 0;
					try {
						result = refreshFeed(feedId);
					} catch (Exception ignored) {

					}
					return result;
				}
			});
		}
		cursor.close();

		int globalResult = 0;
		for (int i = 0; i < nbFeed; i++) {
			try {
				Future<Integer> f = completionService.take();
				globalResult += f.get();
			} catch (Exception ignored) {
			}
		}

		executor.shutdownNow(); // To purge all threads

		return globalResult;
	}

	private int refreshFeed(String feedId) {
	
		ContentResolver cvr = MainApplication.getContext().getContentResolver();
		ContentResolver cr = getContentResolver();
		Cursor cursor = cr.query(FeedColumns.CONTENT_URI(feedId), null, null,
				null, null);
		
	

		if (cursor.moveToFirst()) {
			int urlPosition = cursor.getColumnIndex(FeedColumns.URL);
			int idPosition = cursor.getColumnIndex(FeedColumns._ID);
		//	int titlePosition = cursor.getColumnIndex(FeedColumns.NAME);
			//int fetchmodePosition = cursor.getColumnIndex(FeedColumns.FETCH_MODE);
			//int realLastUpdatePosition = cursor.getColumnIndex(FeedColumns.REAL_LAST_UPDATE);
			//int iconPosition = cursor.getColumnIndex(FeedColumns.ICON);
			//int retrieveFullscreenPosition = cursor.getColumnIndex(FeedColumns.RETRIEVE_FULLTEXT);
			String id = cursor.getString(idPosition);
			

			JSONObject jo;
			//HttpURLConnection connection = null;
			
			try {
				String feedUrl = cursor.getString(urlPosition);
				Log.e("Feed url" + feedUrl, "Feed Url" + feedUrl);
				ContentValues val = new ContentValues();
				updateUriForFeed = EntryColumns
						.ENTRIES_FOR_FEED_CONTENT_URI(id);
				jsonarray = JSONFunctions.getJSONfromURL(feedUrl);
				for (int i = 0; i < jsonarray.length(); i++) {
					jo = jsonarray.getJSONObject(i);

					if (jo.has("articletitle")) {
						val.put(EntryColumns.TITLE,
								jo.getString("articletitle"));
					} else {
						val.put(EntryColumns.TITLE,
								jo.getString("interviewtitle"));
					}

					if (jo.has("articledescription")) {
						val.put(EntryColumns.ABSTRACT,
								jo.getString("articledescription"));
					} else {
						val.put(EntryColumns.ABSTRACT,
								jo.getString("interviewdescription"));
					}
					val.put(EntryColumns.AUTHOR, jo.getString("authorname"));
					val.put(EntryColumns.DATE, now--);
					Log.e("Tocomo url" + now--, "Date" + now);
					/*
					 * String dtStart = jo.getString("updatedtime");
					 * dateStringBuilder.append(dtStart);
					 * 
					 * entryUpdateDate = parsePubdateDate(dateStringBuilder
					 * .toString());
					 * 
					 * SimpleDateFormat dateformat = new
					 * SimpleDateFormat("yyyy-MM-dd  HH:mm:ss"); try {
					 * 
					 * String datetime = dateformat.format(entryUpdateDate);
					 * System.out.println("Current Date Time : " + datetime);
					 * 
					 * 
					 * 
					 * } catch (Exception e) { // TODO Auto-generated catch
					 * block e.printStackTrace(); }
					 */

					inserts.add(ContentProviderOperation
							.newInsert(updateUriForFeed).withValues(val)
							.build());
					
				}
				ContentProviderResult[] results = cvr.applyBatch(
						FeedData.AUTHORITY, inserts);
				FeedDataContentProvider.notifyGroupFromFeedId(id);
				cvr.notifyChange(EntryColumns.CONTENT_URI, null);
			
			//	connection = NetworkUtils.setupConnection(feedUrl);

				

			
				/*
				 * if (fetchMode == 0) { if (contentType != null &&
				 * contentType.startsWith(CONTENT_TYPE_TEXT_HTML)) {
				 * BufferedReader reader = new BufferedReader( new
				 * InputStreamReader(NetworkUtils
				 * .getConnectionInputStream(connection)));
				 * 
				 * String line; int posStart = -1;
				 * 
				 * while ((line = reader.readLine()) != null) { if
				 * (line.contains(HTML_BODY)) { break; } else { Matcher matcher
				 * = FEED_LINK_PATTERN .matcher(line);
				 * 
				 * if (matcher.find()) { // not "while" as only one // link is
				 * needed line = matcher.group(); posStart = line.indexOf(HREF);
				 * 
				 * if (posStart > -1) { String url = line .substring( posStart +
				 * 6, line.indexOf('"', posStart + 10))
				 * .replace(Constants.AMP_SG, Constants.AMP);
				 * 
				 * ContentValues values = new ContentValues();
				 * 
				 * if (url.startsWith(Constants.SLASH)) { int index =
				 * feedUrl.indexOf('/', 8);
				 * 
				 * if (index > -1) { url = feedUrl.substring(0, index) + url; }
				 * else { url = feedUrl + url; } } else if (!url
				 * .startsWith(Constants.HTTP) &&
				 * !url.startsWith(Constants.HTTPS)) { url = feedUrl + '/' +
				 * url; } values.put(FeedColumns.URL, url);
				 * cr.update(FeedColumns.CONTENT_URI(id), values, null, null);
				 * connection.disconnect(); connection = NetworkUtils
				 * .setupConnection(url); contentType = connection
				 * .getContentType(); break; } } } } // this indicates a badly
				 * configured feed if (posStart == -1) {
				 * connection.disconnect(); connection =
				 * NetworkUtils.setupConnection(feedUrl); contentType =
				 * connection.getContentType(); } } if (contentType != null) {
				 * int index = contentType.indexOf(CHARSET);
				 * 
				 * if (index > -1) { int index2 = contentType.indexOf(';',
				 * index);
				 * 
				 * try { Xml.findEncodingByName(index2 > -1 ? contentType
				 * .substring(index + 8, index2) : contentType.substring(index +
				 * 8)); fetchMode = FETCHMODE_DIRECT; } catch
				 * (UnsupportedEncodingException usee) { fetchMode =
				 * FETCHMODE_REENCODE; } } else { fetchMode =
				 * FETCHMODE_REENCODE; }
				 * 
				 * } else { BufferedReader bufferedReader = new BufferedReader(
				 * new InputStreamReader(NetworkUtils
				 * .getConnectionInputStream(connection)));
				 * 
				 * char[] chars = new char[20];
				 * 
				 * int length = bufferedReader.read(chars);
				 * 
				 * String jsonDescription = new String(chars, 0, length);
				 * 
				 * connection.disconnect(); connection =
				 * NetworkUtils.setupConnection(connection .getURL());
				 * 
				 * int start = jsonDescription != null ? jsonDescription
				 * .indexOf(ENCODING) : -1;
				 * 
				 * if (start > -1) { try {
				 * Xml.findEncodingByName(jsonDescription .substring(start + 10,
				 * jsonDescription .indexOf('"', start + 11)));
				 * 
				 * fetchMode = FETCHMODE_DIRECT; } catch
				 * (UnsupportedEncodingException usee) { fetchMode =
				 * FETCHMODE_REENCODE; } } else { // absolutely no encoding
				 * information found fetchMode = FETCHMODE_DIRECT; } }
				 * 
				 * ContentValues values = new ContentValues();
				 * values.put(FeedColumns.FETCH_MODE, fetchMode);
				 * cr.update(FeedColumns.CONTENT_URI(id), values, null, null); }
				 */

			
				cursor.close();
			}
			catch(Exception e)
			{
				
			}
		}
	

		

		return 1;
	}

	public static Pair<String, Vector<String>> improveHtmlContent(
			String content, boolean fetchImages) {
		if (content != null) {
			// remove trashes
			content = content.trim()
					.replaceAll("(?i)<[/]?[ ]?span(.|\n)*?>", "")
					.replaceAll("(?i)<blockquote", "<div")
					.replaceAll("(?i)</blockquote", "</div");
			// remove ads
			content = content
					.replaceAll(
							"(?i)<div class=('|\")mf-viral('|\")><table border=('|\")0('|\")>.*",
							"");
			// remove lazy loading images stuff
			content = content.replaceAll(
					"(?i)\\s+src=[^>]+\\s+original[-]*src=(\"|')", " src=$1");
			// remove bad image paths
			content = content.replaceAll("(?i)\\s+(href|src)=(\"|')//",
					" $1=$2http://");

			// Remove unmatched div tags
			content = content.replaceAll("(?i)<div", "<div").replaceAll(
					"(?i)</div>", "</div>");
			boolean found;
			do {
				found = false;
				Matcher matcher = DIV_TAG.matcher(content);
				while (matcher.find()) {
					String matchedStr = matcher.group(1);
					if (!matchedStr.contains("<div")) {
						content = content.replace(matcher.group(0), "<__DIV__"
								+ matchedStr + "</__DIV__>");
						found = true;
					}
				}
			} while (found);
			content = content.replaceAll("<div[^>]*>", "").replaceAll("</div>",
					"");
			content = content.replaceAll("<__DIV__", "<div").replaceAll(
					"</__DIV__>", "</div>");

			if (content.length() > 0) {
				Vector<String> images = null;
				if (fetchImages) {
					images = new Vector<String>(4);

					Matcher matcher = IMG_PATTERN.matcher(content);

					while (matcher.find()) {
						String match = matcher.group(1).replace(" ", URL_SPACE);

						images.add(match);
						try {
							// replace the '%' that may occur while urlencode
							// such that the img-src url (in the abstract text)
							// does reinterpret the
							// parameters
							content = content
									.replace(
											match,
											(Constants.FILE_URL
													+ NetworkUtils.IMAGE_FOLDER
													+ Constants.IMAGEID_REPLACEMENT + URLEncoder.encode(
													match.substring(match
															.lastIndexOf('/') + 1),
													Constants.UTF8))
													.replace(
															NetworkUtils.PERCENT,
															NetworkUtils.PERCENT_REPLACE));
						} catch (UnsupportedEncodingException e) {
							// UTF-8 should be supported
						}
					}
				}

				return new Pair<String, Vector<String>>(content, images);
			}
		}

		return new Pair<String, Vector<String>>(null, null);
	}

	private Date parsePubdateDate(String dateStr) {
		dateStr = improveDateString(dateStr);
		return parsePubdateDate(dateStr, true);
	}

	private String improveDateString(String dateStr) {
		// We remove the first part if necessary (the day display)
		int coma = dateStr.indexOf(", ");
		if (coma != -1) {
			dateStr = dateStr.substring(coma + 2);
		}

		dateStr = dateStr.replace("T", " ").replace("Z", "")
				.replaceAll("  ", " ").trim(); // fix useless char

		// Replace bad timezones
		for (String[] timezoneReplace : TIMEZONES_REPLACE) {
			dateStr = dateStr.replace(timezoneReplace[0], timezoneReplace[1]);
		}

		return dateStr;
	}

	private Date parsePubdateDate(String dateStr, boolean tryAllFormat) {
		for (DateFormat format : PUBDATE_DATE_FORMATS) {
			try {
				Date result = format.parse(dateStr);
				return (result.getTime() > now ? new Date(now) : result);
			} catch (ParseException ignored) {
			} // just do nothing
		}

		if (tryAllFormat)
			return parseUpdateDate(dateStr, false);
		else
			return null;
	}

	private Date parseUpdateDate(String dateStr, boolean tryAllFormat) {
		for (DateFormat format : UPDATE_DATE_FORMATS) {
			try {
				Date result = format.parse(dateStr);
				return (result.getTime() > now ? new Date(now) : result);
			} catch (ParseException ignored) {
			} // just do nothing
		}

		if (tryAllFormat)
			return parsePubdateDate(dateStr, false);
		else
			return null;
	}
}
