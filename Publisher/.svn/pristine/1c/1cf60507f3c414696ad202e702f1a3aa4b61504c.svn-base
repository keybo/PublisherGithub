package com.example.publisherarticle;

import static com.example.publisherarticle.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static com.example.publisherarticle.CommonUtilities.EXTRA_MESSAGE;
import static com.example.publisherarticle.CommonUtilities.SENDER_ID;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pubfavorite.FavoriteList;
import com.example.pubfeed.JSONFeed;
import com.example.pubimage.ImageLoader;
import com.example.pubsqlite.DatabaseHelper;
import com.example.pubui.R;
import com.example.pubui.UiActivity;
import com.example.pubui.UiForPub;
import com.google.android.gcm.GCMRegistrar;

@SuppressLint("NewApi")
public class ListActivityOne extends Activity {

	JSONFeed feed = new JSONFeed();
	ListView lv;
	static ExpandableListView expandList;
	CustomListAdapter adapter;
	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;

	AsyncTask<Void, Void, Void> mRegisterTask;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		UiForPub.setPreferenceTheme(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.json_list);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		getActionBar().setIcon(R.drawable.collections_view_as_list);

		feed = (JSONFeed) getIntent().getExtras().get("feed");
		GCMRegistrar.checkDevice(this);
		GCMRegistrar.checkManifest(this);
		registerReceiver(mHandleMessageReceiver, new IntentFilter(DISPLAY_MESSAGE_ACTION));

		final String regId = GCMRegistrar.getRegistrationId(this);
		Log.d("registrationID", "::" + regId);
		if (regId.equals("")) 
		{
			// Automatically registers application on startup.
			GCMRegistrar.register(this, SENDER_ID);
		} 
		else 
		{
			// Device is already registered on GCM, needs to check if it is
			// registered on our server as well.
			if (GCMRegistrar.isRegisteredOnServer(this))
			{
				// Skips registration.
				Toast.makeText(getApplicationContext(),
						getString(R.string.already_registered),
						Toast.LENGTH_LONG).show();
			} 
			else 
			{

				final Context context = this;
				mRegisterTask = new AsyncTask<Void, Void, Void>() {

					@Override
					protected Void doInBackground(Void... params) {
						boolean registered = ServerUtilities.register(context,
								regId);

						if (!registered) {
							GCMRegistrar.unregister(context);
						}
						return null;
					}

					@Override
					protected void onPostExecute(Void result) {
						mRegisterTask = null;
					}

				};
				mRegisterTask.execute(null, null, null);
			}
		}

		// FirstLaunch
		boolean firstrun = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
				.getBoolean("firstrun", true);
		if (firstrun) {
			new AlertDialog.Builder(this)
					.setTitle("Welcome to Publisher!")
					.setIcon(R.drawable.ic_launcher)
					.setMessage(
							"To navigate categories Click TopLeft corner icon")
					.setNeutralButton("OK", null).show(); // Sets the button
															// type
		}
		getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
				.putBoolean("firstrun", false).commit();

		mTitle = mDrawerTitle = getTitle();
		lv = (ListView) findViewById(R.id.listView);
		lv.setOnItemClickListener(new ListView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// selectDrawerItem(position);
				mDrawerLayout.closeDrawer(lv);
			}
		});
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		// mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);
		expandList = (ExpandableListView) findViewById(R.id.left_drawer);
		expandList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		expandList.setAdapter(new ExpandAdapter(this));

		expandList.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView arg0, View arg1,
					int arg2, int arg3, long arg4) {
				String itemPosition = ExpandAdapter.listSection[arg2][arg3];

				Toast.makeText(getApplicationContext(), "" + itemPosition,
						Toast.LENGTH_SHORT).show();

				if (itemPosition.equals("Arts")) {
					startActivity(new Intent(
							ListActivityOne.this,
							com.example.publisherinterview.Splash_Int_Activity.class));
				} else if (itemPosition.equals("Favorites")) {
					startActivity(new Intent(ListActivityOne.this,
							FavoriteList.class));
				} 
				

				return false;
			}

		});

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, R.string.drawer_open,
				R.string.drawer_close) {
			public void onDrawerClosed(View view) 
			{
				getActionBar().setTitle(mTitle);
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView)
			{
				getActionBar().setTitle(R.string.app_name);
				invalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		adapter = new CustomListAdapter(this);
		lv.setAdapter(adapter);

		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// actions to be performed when a list item clicked
				int pos = arg2;

				Bundle bundle = new Bundle();
				bundle.putSerializable("feed", feed);
				Intent intent = new Intent(ListActivityOne.this,
						DetailActivity.class);
				intent.putExtras(bundle);
				intent.putExtra("pos", pos);

				startActivity(intent);

			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.preferences, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		adapter.imageLoader.clearCache();
		adapter.notifyDataSetChanged();
		if (mRegisterTask != null)
		{
			mRegisterTask.cancel(true);
		}
		unregisterReceiver(mHandleMessageReceiver);
		GCMRegistrar.onDestroy(this);
	}

	class CustomListAdapter extends BaseAdapter {

		private LayoutInflater layoutInflater;
		public ImageLoader imageLoader;

		public CustomListAdapter(ListActivityOne activity) {

			layoutInflater = (LayoutInflater) activity
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			imageLoader = new ImageLoader(activity.getApplicationContext());
		}

		@Override
		public int getCount() {
			return feed.getItemCount();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			// Inflate the item layout and set the views
			View listItem = convertView;
			int pos = position;
			if (listItem == null) {
				listItem = layoutInflater.inflate(R.layout.list_item, null);
			}
			ImageView iv = (ImageView) listItem.findViewById(R.id.thumb);

			TextView tvTitle = (TextView) listItem.findViewById(R.id.title);
			TextView tvDate = (TextView) listItem.findViewById(R.id.date);
			TextView tvDesc = (TextView) listItem.findViewById(R.id.desc);

			imageLoader.DisplayImage(feed.getItem(pos).getImage(), iv);
			String a = feed.getItem(pos).getDate();
			String splitdate[] = a.split(" ");

			String splitmonth[] = splitdate[0].split("-");
			String DateTime = splitdate[1];
			String splitYearOf = splitmonth[0];
			String splitDateOf = splitmonth[2];
			Log.d("Year is=" + splitYearOf, "Date is " + splitDateOf);

			Map<String, String> monthMap = new HashMap<String, String>();
			monthMap.put("01", "Jan");
			monthMap.put("02", "Feb");
			monthMap.put("03", "Mar");
			monthMap.put("04", "Apr");
			monthMap.put("05", "May");
			monthMap.put("06", "Jun");
			monthMap.put("07", "Jul");
			monthMap.put("08", "Aug");
			monthMap.put("09", "Sep");
			monthMap.put("10", "Oct");
			monthMap.put("11", "Nov");
			monthMap.put("12", "Dec");
			String month = String.valueOf(monthMap.get(splitmonth[1]));
			Log.d("Moth" + month, "Moth" + month);
			tvDate.setText(month + " " + splitDateOf + " " + splitYearOf + " "
					+ DateTime);
			String splitTitle = feed.getItem(pos).getTitle();
			if (splitTitle.length() > 40) {
				splitTitle = splitTitle.substring(0, 40) + "...";
			}
			tvTitle.setText(splitTitle);
			String splitDesc = feed.getItem(pos).getDescription();
			if (splitDesc.length() > 45) {
				splitDesc = splitDesc.substring(0, 44) + "...";
			}
			tvDesc.setText(splitDesc);

			return listItem;
		}

	}

	public static class ExpandAdapter implements ExpandableListAdapter {

		DatabaseHelper bdh;

		Context context;
		static String listHeaders[] = { "All", "Interviews", "Article",
				"Settings" };
		static String listSection[][] = {
				{ "Arts", "Favorites", "Science", "Technology" },
				{ "News", "Reviews" }, { "Buyer's Guide", "Videos" },
				{ "Podcast", "Shopping" } };
		int lastExpandedListview;

		public ExpandAdapter(Context con)
		{
			this.context = con;
			bdh = new DatabaseHelper(con);
		}

		@Override
		public boolean areAllItemsEnabled() {

			return false;
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {

			return null;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return 0;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			TextView sectionText = new TextView(context);
			sectionText.setText(listSection[groupPosition][childPosition]);
			sectionText.setPadding(50, 0, 0, 0);
			sectionText.setTextColor(Color.parseColor("#E69316"));
			sectionText.setTextSize(20);
			bdh.openToRead();
			int a = bdh.getAllCount();

			if (listSection[groupPosition][childPosition] == "Favorites") {
				sectionText.setText("Favorites          " + a);
			}

			return sectionText;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return listSection[groupPosition].length;
		}

		@Override
		public long getCombinedChildId(long groupId, long childId) {

			return 0;
		}

		@Override
		public long getCombinedGroupId(long groupId) {

			return groupId;
		}

		@Override
		public Object getGroup(int groupPosition) {

			return groupPosition;
		}

		@Override
		public int getGroupCount() {

			return listHeaders.length;
		}

		@Override
		public long getGroupId(int groupPosition) {

			return groupPosition;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			TextView headerText = new TextView(context);
			headerText.setText(listHeaders[groupPosition]);
			headerText.setPadding(30, 0, 0, 0);
			headerText.setTextSize(25);
			return headerText;

		}

		@Override
		public boolean hasStableIds() {

			return false;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {

			return true;
		}

		@Override
		public boolean isEmpty() {

			return false;
		}

		@Override
		public void onGroupCollapsed(int groupPosition) {

		}

		@Override
		public void onGroupExpanded(int groupPosition) {

			if (groupPosition != lastExpandedListview) {
				expandList.collapseGroup(lastExpandedListview);
			}
			lastExpandedListview = groupPosition;

		}

		@Override
		public void registerDataSetObserver(DataSetObserver observer) {

		}

		@Override
		public void unregisterDataSetObserver(DataSetObserver observer)
		{

		}

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{

		switch (item.getItemId())
		{
		case R.id.prefernces:
			Intent a = new Intent(this, UiActivity.class);
			startActivity(a);
			break;

		}
		if (mDrawerToggle.onOptionsItemSelected(item))
		{
			return true;
		}
		return super.onOptionsItemSelected(item);

	}
	

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	private void checkNotNull(Object reference, String name) {
		if (reference == null) {
			throw new NullPointerException(getString(R.string.error_config,
					name));
		}
	}

	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
			Toast.makeText(getApplicationContext(), newMessage,
					Toast.LENGTH_LONG).show();
		}
	};

}
