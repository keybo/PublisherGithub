package com.example.publisherinterview;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
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

import com.example.pubfeed.JSONFeed;
import com.example.pubimage.ImageLoader;
import com.example.publisherarticle.R;

@SuppressLint("NewApi")
public class List_Int_Activity extends Activity
{
	
	JSONFeed feed=new JSONFeed();
	ListView lv;
	static ExpandableListView expandList;
	CustomListAdapter adapter;
	private DrawerLayout mDrawerLayout;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.json_int_list);

		 getActionBar().setDisplayHomeAsUpEnabled(true);
	     getActionBar().setHomeButtonEnabled(true);
	     getActionBar().setIcon(R.drawable.collections_view_as_list);
		feed = (JSONFeed) getIntent().getExtras().get("feed");

		
		lv = (ListView) findViewById(R.id.listView);
		mDrawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout);
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		expandList=(ExpandableListView)findViewById(R.id.left_drawer);
		expandList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
       
        
       

        adapter = new CustomListAdapter(this);
		lv.setAdapter(adapter);

		lv.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3)
			{
				// actions to be performed when a list item clicked
				int pos = arg2;

				Bundle bundle = new Bundle();
				bundle.putSerializable("feed", feed);
				Intent intent = new Intent(List_Int_Activity.this,Detail_Int_Activity.class);
				intent.putExtras(bundle);
				intent.putExtra("pos", pos);
				startActivity(intent);

			}
		});

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		switch(item.getItemId()) 
		{
		case android.R.id.home:
			startActivity(new Intent(List_Int_Activity.this,com.example.publisherarticle.SplashActivity.class));
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		adapter.imageLoader.clearCache();
		adapter.notifyDataSetChanged();
	}

	class CustomListAdapter extends BaseAdapter
	{

		private LayoutInflater layoutInflater;
		public ImageLoader imageLoader;

		public CustomListAdapter(List_Int_Activity activity)
		{

			layoutInflater = (LayoutInflater) activity
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			imageLoader = new ImageLoader(activity.getApplicationContext());
		}

		@Override
		public int getCount()
		{
		  return feed.getItemCount();
		}

		@Override
		public Object getItem(int position) 
		{
			return position;
		}

		@Override
		public long getItemId(int position)
		{
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{

			// Inflate the item layout and set the views
			View listItem = convertView;
			int pos = position;
			if (listItem == null)
			{
				listItem = layoutInflater.inflate(R.layout.list_int_item, null);
			}
			ImageView iv = (ImageView) listItem.findViewById(R.id.thumb);
			
			TextView tvTitle = (TextView) listItem.findViewById(R.id.title);
			TextView tvDesc = (TextView) listItem.findViewById(R.id.desc);
			TextView tvDate = (TextView) listItem.findViewById(R.id.date);

			imageLoader.DisplayImage(feed.getItem(pos).getImage(), iv);
			String splitText=feed.getItem(pos).getDescription();
			if(splitText.length()>31)
			{
				splitText=splitText.substring(0,30)+"...";
			}
			tvDesc.setText(splitText);
			tvTitle.setText(feed.getItem(pos).getTitle());
			String a=feed.getItem(pos).getDate();
			SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
			Date date1=null;
			try
			{
			 date1=format.parse(a);
			}
			catch(Exception e)
			{
				System.out.println(""+e);
			}
			SimpleDateFormat again=new SimpleDateFormat("MMM dd yyyy, hh:mm a");
			String nes=again.format(date1);
			tvDate.setText(nes.toString());

			return listItem;
		}
	}
	
	 
	

	
}
