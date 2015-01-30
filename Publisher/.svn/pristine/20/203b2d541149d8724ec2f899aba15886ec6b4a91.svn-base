package com.meta.publisherinterview;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.meta.pubfeed.JSONFeed;
import com.meta.pubimage.ImageLoader;
import com.meta.pubui.R;
import com.meta.pubui.UiForPub;

@SuppressLint("NewApi")
public class List_Int_Activity extends Activity {

	JSONFeed feed = new JSONFeed();
	ListView lv;
	static ExpandableListView expandList;
	CustomListAdapter adapter;
	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	private long duration = 0;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		UiForPub.setPreferenceTheme(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.json_int_list);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		getActionBar().setIcon(R.drawable.collections_view_as_list);
		feed = (JSONFeed) getIntent().getExtras().get("feed");

		mTitle = mDrawerTitle = getTitle();
		lv = (ListView) findViewById(R.id.listView);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, R.string.drawer_open,
				R.string.drawer_close) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
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
				Intent intent = new Intent(List_Int_Activity.this,
						Detail_Int_Activity.class);
				intent.putExtras(bundle);
				intent.putExtra("pos", pos);
				startActivity(intent);

			}
		});

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		adapter.imageLoader.clearCache();
		adapter.notifyDataSetChanged();
	}

	class CustomListAdapter extends BaseAdapter {

		private LayoutInflater layoutInflater;
		public ImageLoader imageLoader;

		public CustomListAdapter(List_Int_Activity activity) {

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
			View listItem = convertView;
			int pos = position;
			if (listItem == null) {
				listItem = layoutInflater.inflate(R.layout.list_int_item, null);
			}
			ImageView iv = (ImageView) listItem.findViewById(R.id.thumb);

			TextView tvTitle = (TextView) listItem.findViewById(R.id.title);
			TextView tvDate = (TextView) listItem.findViewById(R.id.date);
			TextView tvDesc = (TextView) listItem.findViewById(R.id.desc);

			imageLoader.DisplayImage(feed.getItem(pos).getImage(), iv);

			String splitTitle = feed.getItem(pos).getTitle();
			if (splitTitle.length() > 23) {
				splitTitle = splitTitle.substring(0, 22) + "...";
			}
			tvTitle.setText(splitTitle);
			String splitDesc = feed.getItem(pos).getDescription();
			if (splitDesc.length() > 45) {
				splitDesc = splitDesc.substring(0, 44) + "...";
			}
			tvDesc.setText(splitDesc);
			String a = feed.getItem(pos).getDate();
			String splitdate[] = a.split(" ");
			String splitmonth[] = splitdate[0].split("-");
			String DateTime = splitdate[1];
			Log.d("time now=" + DateTime, "Time now " + DateTime);

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

			return listItem;
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			startActivity(new Intent(List_Int_Activity.this,
					com.meta.publisherarticle.SplashActivity.class));
			return true;
		}
		return false;
	}
}
