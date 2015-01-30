package com.example.publisherarticle;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
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

import com.example.pubfeed.JSONFeed;
import com.example.pubimage.ImageLoader;

@SuppressLint("NewApi")
public class ListActivityOne extends Activity
{
	
	JSONFeed feed=new JSONFeed();
	ListView lv;
	static ExpandableListView expandList;
	CustomListAdapter adapter;
	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.json_list);

		 getActionBar().setDisplayHomeAsUpEnabled(true);
	     getActionBar().setHomeButtonEnabled(true);
	     getActionBar().setIcon(R.drawable.collections_view_as_list);
		feed = (JSONFeed) getIntent().getExtras().get("feed");

		mTitle = mDrawerTitle = getTitle();
		lv = (ListView) findViewById(R.id.listView);
		mDrawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout);
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		expandList=(ExpandableListView)findViewById(R.id.left_drawer);
		expandList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        expandList.setAdapter(new ExpandAdapter(this));
        expandList.setOnChildClickListener(new OnChildClickListener()
        {        	
			@Override
			public boolean onChildClick(ExpandableListView arg0, View arg1,int arg2, int arg3, long arg4) 
			{
				String itemPosition=ExpandAdapter.listSection[arg2][arg3];
				
				Toast.makeText(getApplicationContext(), ""+itemPosition, Toast.LENGTH_SHORT).show();
				
				if(itemPosition.equals("Arts"))
				{
					startActivity(new Intent(ListActivityOne.this,com.example.publisherinterview.Splash_Int_Activity.class));
				}
				/*else if(itemPosition.equals("Engineer"))
				{
					startActivity(new Intent(MainActivity.this,RegisterAct.class));
				}*/
				return false;
			}
        	
        });
        
        mDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout,R.drawable.ic_drawer,R.string.drawer_open,R.string.drawer_close)
        {
            public void onDrawerClosed(View view)
            {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); 
            }

            public void onDrawerOpened(View drawerView) 
            {
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); 
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

	
	

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
				Intent intent = new Intent(ListActivityOne.this,DetailActivity.class);
				intent.putExtras(bundle);
				intent.putExtra("pos", pos);
				startActivity(intent);

			}
		});

	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		adapter.imageLoader.clearCache();
		adapter.notifyDataSetChanged();
	}

	@SuppressLint("SimpleDateFormat")
	class CustomListAdapter extends BaseAdapter
	{

		private LayoutInflater layoutInflater;
		public ImageLoader imageLoader;

		public CustomListAdapter(ListActivityOne activity)
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
				listItem = layoutInflater.inflate(R.layout.list_item, null);
			}
			ImageView iv = (ImageView) listItem.findViewById(R.id.thumb);
			
			TextView tvTitle = (TextView) listItem.findViewById(R.id.title);
			TextView tvDesc = (TextView) listItem.findViewById(R.id.desc);
			TextView tvDate = (TextView) listItem.findViewById(R.id.date);

			imageLoader.DisplayImage(feed.getItem(pos).getImage(), iv);
			
			String splitDesc=feed.getItem(pos).getDescription();
			if(splitDesc.length()>31)
			{
				splitDesc=splitDesc.substring(0,30)+"...";
			}
			tvDesc.setText(splitDesc);
			String splitTitle=feed.getItem(pos).getTitle();
			if(splitTitle.length()>30)
			{
				splitTitle=splitTitle.substring(0,29)+"...";
			}
			tvTitle.setText(splitTitle);
	
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
	public static class ExpandAdapter implements ExpandableListAdapter
	{
		Context context;
		static String listHeaders[]={"All","Interviews","Article","Settings"};
		static String listSection[][]={{"Arts","Engineer","Science","Technology"},{"News","Reviews"},{"Buyer's Guide","Vedios"},{"Podcast","Shopping"}};
		int lastExpandedListview;
		public ExpandAdapter(Context con)
		{
			this.context=con;
		}

		
		@Override
		public boolean areAllItemsEnabled()
		{
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) 
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition)
		{
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) 
		{
			TextView sectionText=new TextView(context);
			sectionText.setText(listSection[groupPosition][childPosition]);
			sectionText.setPadding(50, 0, 0, 0);
			sectionText.setTextColor(Color.parseColor("#E69316"));
			sectionText.setTextSize(20);
			
			return sectionText;
		}

		@Override
		public int getChildrenCount(int groupPosition)
		{
			return listSection[groupPosition].length;
		}

		@Override
		public long getCombinedChildId(long groupId, long childId)
		{
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public long getCombinedGroupId(long groupId)
		{
			// TODO Auto-generated method stub
			return groupId;
		}

		@Override
		public Object getGroup(int groupPosition)
		{
			// TODO Auto-generated method stub
			return groupPosition;
		}

		@Override
		public int getGroupCount()
		{
			// TODO Auto-generated method stub
			return listHeaders.length;
		}

		@Override
		public long getGroupId(int groupPosition)
		{
			// TODO Auto-generated method stub
			return groupPosition;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,View convertView, ViewGroup parent)
		{
			TextView headerText=new TextView(context);
			headerText.setText(listHeaders[groupPosition]);
			headerText.setPadding(30, 0, 0, 0);
			headerText.setTextSize(25);
			return headerText;
			
		}

		@Override
		public boolean hasStableIds()
		{
		
			return false;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) 
		{
			
			return true;
		}

		@Override
		public boolean isEmpty()
		{
			
			return false;
		}

		@Override
		public void onGroupCollapsed(int groupPosition)
		{
			
			
		}

		@Override
		public void onGroupExpanded(int groupPosition)
		{
			
			 if(groupPosition != lastExpandedListview)
			 {
				 expandList.collapseGroup(lastExpandedListview);
		     }
			 lastExpandedListview= groupPosition;
			
		}

		@Override
		public void registerDataSetObserver(DataSetObserver observer)
		{
			
			
		}

		@Override
		public void unregisterDataSetObserver(DataSetObserver observer) 
		{
			
			
		}
		
	}
	@Override
    public boolean onOptionsItemSelected(MenuItem item)
	 {
         
        if (mDrawerToggle.onOptionsItemSelected(item))
        {
            return true;
        }
		return false;
        
       
      
    }
	 
	
	 @Override
    public void setTitle(CharSequence title) 
	 {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }
	 
	 @Override
    protected void onPostCreate(Bundle savedInstanceState)
	 {
        super.onPostCreate(savedInstanceState);
      
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
      
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

	
}
