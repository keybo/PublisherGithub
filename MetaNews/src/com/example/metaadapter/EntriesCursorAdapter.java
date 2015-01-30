package com.example.metaadapter;


import java.util.Date;
import java.util.Vector;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

import com.example.metanews.Constants;
import com.example.metanews.MainApplication;
import com.example.metanews.R;
import com.example.metaprovider.FeedData;
import com.example.metaprovider.FeedData.EntryColumns;
import com.example.metaprovider.FeedData.FeedColumns;
import com.example.metaprovider.FeedDataContentProvider;
import com.example.metautils.UiUtils;

public class EntriesCursorAdapter extends ResourceCursorAdapter {
    private int titleColumnPosition;

    private int dateColumn;
    private int isReadColumn;
    private int favoriteColumn;
    private int idColumn;
    private int feedIconColumn;
    private int feedNameColumn;
    private int linkColumn;
    long currentTime=System.currentTimeMillis();
    private final Uri uri;
    private final boolean showFeedInfo;

    private final Vector<Long> markedAsRead = new Vector<Long>();
    private final Vector<Long> markedAsUnread = new Vector<Long>();
    private final Vector<Long> favorited = new Vector<Long>();
    private final Vector<Long> unfavorited = new Vector<Long>();

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public EntriesCursorAdapter(Context context, Uri uri, Cursor cursor, boolean showFeedInfo) 
    {
        super(context, R.layout.item_entry_list, cursor, 0);
        this.uri = uri;
        this.showFeedInfo = showFeedInfo;

        reinit(cursor);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor)
    {
        final TextView textView = (TextView) view.findViewById(android.R.id.text1);
        textView.setText(cursor.getString(titleColumnPosition));

        final TextView dateTextView = (TextView) view.findViewById(android.R.id.text2);
        final ImageView starImgView = (ImageView) view.findViewById(android.R.id.icon);
        final long id = cursor.getLong(idColumn);
        view.setTag(cursor.getString(linkColumn));
        final boolean favorite = !unfavorited.contains(id) && (cursor.getInt(favoriteColumn) == 1 || favorited.contains(id));
        final CheckBox viewCheckBox = (CheckBox) view.findViewById(android.R.id.checkbox);

        starImgView.setImageResource(favorite ? R.drawable.dimmed_rating_important : R.drawable.dimmed_rating_not_important);
        starImgView.setTag(favorite ? Constants.TRUE : Constants.FALSE);
        starImgView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean newFavorite = !Constants.TRUE.equals(view.getTag());

                if (newFavorite)
                {
                    view.setTag(Constants.TRUE);
                    starImgView.setImageResource(R.drawable.dimmed_rating_important);
                    favorited.add(id);
                    unfavorited.remove(id);
                } 
                else {
                    view.setTag(Constants.FALSE);
                    starImgView.setImageResource(R.drawable.dimmed_rating_not_important);
                    unfavorited.add(id);
                    favorited.remove(id);
                }

                ContentValues values = new ContentValues();
                values.put(EntryColumns.IS_FAVORITE, newFavorite ? 1 : 0);

                ContentResolver cr = MainApplication.getContext().getContentResolver();
                Uri entryUri = ContentUris.withAppendedId(uri, id);
                if (cr.update(entryUri, values, null, null) > 0) {
                    FeedDataContentProvider.notifyAllFromEntryUri(entryUri, false); //Receive New Favorite on MainActivity
                }
            }
        });

        Date date = new Date(cursor.getLong(dateColumn));
        long getTime=date.getTime();
        long nowtime=currentTime-getTime;
        Date to =new Date(nowtime);
        Log.e("To time "+to, "now time"+nowtime);
        Date nowitme=new Date(nowtime);
        Log.e("Exaxct time"+nowitme, "Exact time"+nowitme);
        if (showFeedInfo && feedIconColumn > -1) {
            byte[] iconBytes = cursor.getBlob(feedIconColumn);

            if (iconBytes != null && iconBytes.length > 0) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(iconBytes, 0, iconBytes.length);

                if (bitmap != null) {
                    int bitmapSizeInDip = UiUtils.dpToPixel(18);
                    if (bitmap.getHeight() != bitmapSizeInDip) {
                        bitmap = Bitmap.createScaledBitmap(bitmap, bitmapSizeInDip, bitmapSizeInDip, false);
                    }
                }
                dateTextView.setCompoundDrawablesWithIntrinsicBounds(new BitmapDrawable(context.getResources(), bitmap), null, null, null);
            } else {
                dateTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            }
        }

        if (showFeedInfo && feedNameColumn > -1) {
            String feedName = cursor.getString(feedNameColumn);
            if (feedName != null) {
                dateTextView.setText(new StringBuilder(Constants.DATE_FORMAT.format(date)).append(' ').append(Constants.TIME_FORMAT.format(date)).append(Constants.COMMA_SPACE).append(feedName));
            } else {
                dateTextView.setText(new StringBuilder(Constants.DATE_FORMAT.format(date)).append(' ').append(Constants.TIME_FORMAT.format(date)));
            }
        } else {
            dateTextView.setText(new StringBuilder(Constants.DATE_FORMAT.format(date)).append(' ').append(Constants.TIME_FORMAT.format(date)));
        }

        viewCheckBox.setOnCheckedChangeListener(null);
        if (markedAsUnread.contains(id) || (cursor.isNull(isReadColumn) && !markedAsRead.contains(id))) {
            textView.setEnabled(true);
            dateTextView.setEnabled(true);
            viewCheckBox.setChecked(false);
        } else {
            textView.setEnabled(false);
            dateTextView.setEnabled(false);
            viewCheckBox.setChecked(true);
        }

        viewCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    markAsRead(id);
                    textView.setEnabled(false);
                    dateTextView.setEnabled(false);
                } else {
                    markAsUnread(id);
                    textView.setEnabled(true);
                    dateTextView.setEnabled(true);
                }
            }
        });
    }

    public void markAllAsRead() {
        markedAsRead.clear();
        markedAsUnread.clear();

        new Thread() {
            @Override
            public void run() {
                ContentResolver cr = MainApplication.getContext().getContentResolver();

                if (cr.update(uri, FeedData.getReadContentValues(), EntryColumns.WHERE_UNREAD, null) > 0) {
                    if (!uri.toString().startsWith(EntryColumns.CONTENT_URI.toString())) {
                        cr.notifyChange(EntryColumns.CONTENT_URI, null);
                    }
                    cr.notifyChange(FeedColumns.CONTENT_URI, null);
                    cr.notifyChange(FeedColumns.GROUPS_CONTENT_URI, null);
                    cr.notifyChange(FeedColumns.GROUPED_FEEDS_CONTENT_URI, null);
                    cr.notifyChange(EntryColumns.FAVORITES_CONTENT_URI, null);
                }
            }
        }.start();
    }

    private void markAsRead(final long id) {
        markedAsRead.add(id);
        markedAsUnread.remove(id);

        new Thread() {
            @Override
            public void run() {
                ContentResolver cr = MainApplication.getContext().getContentResolver();
                Uri entryUri = ContentUris.withAppendedId(uri, id);
                if (cr.update(entryUri, FeedData.getReadContentValues(), null, null) > 0) {
                    FeedDataContentProvider.notifyAllFromEntryUri(entryUri, false);
                }
            }
        }.start();
    }

    private void markAsUnread(final long id) {
        markedAsUnread.add(id);
        markedAsRead.remove(id);

        new Thread() {
            @Override
            public void run() {
                ContentResolver cr = MainApplication.getContext().getContentResolver();
                Uri entryUri = ContentUris.withAppendedId(uri, id);
                if (cr.update(entryUri, FeedData.getUnreadContentValues(), null, null) > 0) {
                    FeedDataContentProvider.notifyAllFromEntryUri(entryUri, false);
                }
            }
        }.start();
    }

    @Override
    public void changeCursor(Cursor cursor) {
        reinit(cursor);
        super.changeCursor(cursor);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
    public Cursor swapCursor(Cursor newCursor) {
        reinit(newCursor);
        return super.swapCursor(newCursor);
    }

    @Override
    public void notifyDataSetChanged() {
        reinit(null);
        super.notifyDataSetChanged();
    }

    @Override
    public void notifyDataSetInvalidated() {
        reinit(null);
        super.notifyDataSetInvalidated();
    }

    private void reinit(Cursor cursor) {
        markedAsRead.clear();
        markedAsUnread.clear();
        favorited.clear();
        unfavorited.clear();

        if (cursor != null) {
            titleColumnPosition = cursor.getColumnIndex(EntryColumns.TITLE);
            dateColumn = cursor.getColumnIndex(EntryColumns.DATE);
            Log.e("Date for dote"+dateColumn,  "Date for dote"+dateColumn);
            isReadColumn = cursor.getColumnIndex(EntryColumns.IS_READ);
            favoriteColumn = cursor.getColumnIndex(EntryColumns.IS_FAVORITE);
            idColumn = cursor.getColumnIndex(EntryColumns._ID);
            linkColumn = cursor.getColumnIndex(EntryColumns.LINK);
            if (showFeedInfo) 
            {
                feedIconColumn = cursor.getColumnIndex(FeedColumns.ICON);
                feedNameColumn = cursor.getColumnIndex(FeedColumns.NAME);
            }
        }
    }
}
