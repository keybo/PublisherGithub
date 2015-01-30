package com.example.publisherarticle;

import static com.example.publisherarticle.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static com.example.publisherarticle.CommonUtilities.EXTRA_MESSAGE;
import static com.example.publisherarticle.CommonUtilities.SENDER_ID;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.ActionProvider;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.SubMenu;
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

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.example.pubfeed.JSONFeed;
import com.example.pubimage.ImageLoader;
import com.google.android.gcm.GCMRegistrar;

@SuppressLint("NewApi")
public class ListActivityOne extends SherlockActivity
{	
	
	AsyncTask<Void, Void, Void> mRegisterTask;
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

		 getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	     getSupportActionBar().setHomeButtonEnabled(true);
	     getSupportActionBar().setIcon(R.drawable.collections_view_as_list);
	     
	 	feed = (JSONFeed) getIntent().getExtras().get("feed");
	  
	 	GCMRegistrar.checkDevice(this);
        GCMRegistrar.checkManifest(this);
        registerReceiver(mHandleMessageReceiver,new IntentFilter(DISPLAY_MESSAGE_ACTION));
        final String regId = GCMRegistrar.getRegistrationId(this);
        if (regId.equals(""))
        {
            // Automatically registers application on startup.
            GCMRegistrar.register(this, SENDER_ID);
        } 
        else
        {
            // Device is already registered on GCM, check server.
            if (GCMRegistrar.isRegisteredOnServer(this)) 
            {
                // Skips registration.
               Toast.makeText(getApplicationContext(), "Already registered on Server", Toast.LENGTH_LONG).show();
            } 
            else
            {
                // Try to register again, but not in the UI thread.
                // It's also necessary to cancel the thread onDestroy(),
                // hence the use of AsyncTask instead of a raw thread.
                final Context context = this;
                mRegisterTask = new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... params) {
                        boolean registered =ServerUtilities.register(context, regId);
                        // At this point all attempts to register with the app
                        // server failed, so we need to unregister the device
                        // from GCM - the app will try to register again when
                        // it is restarted. Note that GCM will send an
                        // unregistered callback upon completion, but
                        // GCMIntentService.onUnregistered() will ignore it.
                        if (!registered)
                        {
                            GCMRegistrar.unregister(context);
                        }
                        return null;
                    }
                    @Override
                    protected void onPostExecute(Void result)
                    {
                        mRegisterTask = null;
                    }
                };
                mRegisterTask.execute(null, null, null);
            }
        }
    
       
	   //FirstLaunch
	     boolean firstrun = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("firstrun", true);
	 	    if (firstrun)
	 	    {
	 	    	new AlertDialog.Builder(this)
	 	    	.setTitle("Welcome to Publisher!") 
	 	    	.setIcon(R.drawable.meta)
	 	    	.setMessage("To navigate categories Click TopLeft corner icon")
	 	    	.setNeutralButton("OK", null).show(); //Sets the button type
	 	    }
	 	   getSharedPreferences("PREFERENCE", MODE_PRIVATE)
	        .edit()
	        .putBoolean("firstrun", false)
	        .commit();
	 	   
	 	   
	     
		mTitle = mDrawerTitle = getTitle();
		lv = (ListView) findViewById(R.id.listView);
		lv.setTextFilterEnabled(true);
		mDrawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout);
		//mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
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
				if(itemPosition.equals("Videos"))
				{
					startActivity(new Intent(ListActivityOne.this,com.example.publisher.PublisherSplash.class));
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
                getSupportActionBar().setTitle(mTitle);
                supportInvalidateOptionsMenu(); 
            }

            public void onDrawerOpened(View drawerView) 
            {
                getSupportActionBar().setTitle(mDrawerTitle);
                supportInvalidateOptionsMenu(); 
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);


		adapter = new CustomListAdapter(this);
		adapter.notifyDataSetChanged();
		
		lv.setAdapter(adapter);
		lv.invalidateViews();
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
	
	private final BroadcastReceiver mHandleMessageReceiver =new BroadcastReceiver()
	{
	    @Override
	    public void onReceive(Context context, Intent intent) 
	    {
	        String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
	        //String getMessage=intent.getExtras().getString("message from server");
	        Toast.makeText(getApplicationContext(), newMessage,Toast.LENGTH_LONG).show();
	       
	    }
	};
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
				listItem = layoutInflater.inflate(R.layout.list_v_item, null);
			}
			ImageView iv = (ImageView) listItem.findViewById(R.id.thumb);
			
			TextView tvTitle = (TextView) listItem.findViewById(R.id.title);
			TextView tvDate = (TextView) listItem.findViewById(R.id.date);
			TextView tvDesc = (TextView) listItem.findViewById(R.id.desc);
			
    		imageLoader.DisplayImage(feed.getItem(pos).getImage(), iv);
			String a=feed.getItem(pos).getDate();
			String splitdate[]=a.split(" ");
			
			String splitmonth[]=splitdate[0].split("-");
			String DateTime=splitdate[1];
			String splitYearOf=splitmonth[0];
			String  splitDateOf=splitmonth[2];
			Log.d("Year is="+splitYearOf,"Date is "+splitDateOf);
			
			Map<String,String> monthMap=new HashMap<String,String>();
			monthMap.put("01","Jan");
			monthMap.put("02","Feb");
			monthMap.put("03","Mar");
			monthMap.put("04", "Apr");
			monthMap.put("05", "May");
			monthMap.put("06", "Jun");
			monthMap.put("07","Jul");
			monthMap.put("08","Aug");
			monthMap.put("09", "Sep");
			monthMap.put("10","Oct");
			monthMap.put("11", "Nov");
			monthMap.put("12","Dec");
			String month=String.valueOf(monthMap.get(splitmonth[1]));
			Log.d("Moth"+month, "Moth"+month);
			tvDate.setText(month+" "+splitDateOf+" "+splitYearOf+" "+DateTime);
			String splitTitle=feed.getItem(pos).getTitle();
			if(splitTitle.length()>23)
			{
				splitTitle=splitTitle.substring(0,22)+"...";
			}
			tvTitle.setText(splitTitle);
			String splitDesc=feed.getItem(pos).getDescription();
			if(splitDesc.length()>45)
			{
				splitDesc=splitDesc.substring(0,44)+"...";
			}
			tvDesc.setText(splitDesc);

			return listItem;
		}

	}
	public static class ExpandAdapter implements ExpandableListAdapter
	{
		Context context;
		static String listHeaders[]={"All","Interviews","Article","Settings"};
		static String listSection[][]={{"Arts","Engineer","Science","Technology"},{"News","Reviews"},{"Buyer's Guide","Videos"},{"Podcast","Shopping"}};
		int lastExpandedListview;
		public ExpandAdapter(Context con)
		{
			this.context=con;
		}

		
		@Override
		public boolean areAllItemsEnabled()
		{
			
			return false;
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) 
		{
		
			return null;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition)
		{
		
			return 1;
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
			
			return 0;
		}

		@Override
		public long getCombinedGroupId(long groupId)
		{
			
			return groupId;
		}

		@Override
		public Object getGroup(int groupPosition)
		{
			
			return groupPosition;
		}

		@Override
		public int getGroupCount()
		{
			
			return listHeaders.length;
		}

		@Override
		public long getGroupId(int groupPosition)
		{
			
			return groupPosition;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,View convertView, ViewGroup parent)
		{
			TextView headerText=new TextView(context);
	    	//headerText.setCompoundDrawablesWithIntrinsicBounds(drawable.zoom_plate, drawable.sym_call_missed,drawable.presence_video_away,drawable.ic_popup_reminder);
			headerText.setText(listHeaders[groupPosition]);
			headerText.setPadding(30, 0, 0, 0);
			headerText.setTextSize(25);
			headerText.setTextColor(Color.parseColor("#FFFFFF"));
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
	public boolean onCreateOptionsMenu(Menu menu)
	{ 
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.splash, menu);
		return super.onCreateOptionsMenu(menu);
	}

	 @Override
	   public boolean onPrepareOptionsMenu(Menu menu) 
	  {
	      // If the nav drawer is open, hide action items related to the content
	      // view
	     // boolean drawerOpen = mDrawerLayout.isDrawerOpen(expandList);
	
		 return super.onPrepareOptionsMenu(menu);
	   }
	@Override
    public boolean onOptionsItemSelected(final MenuItem item)
	 {
         
        if (mDrawerToggle.onOptionsItemSelected(getMenuItem(item)))
        {
            return true;
        }
		return false;
        
       
      
    }

	   private android.view.MenuItem getMenuItem(final MenuItem item) {
	      return new android.view.MenuItem() 
	      {
	         @Override
	         public int getItemId() {
	            return item.getItemId();
	         }

	         public boolean isEnabled() {
	            return true;
	         }

	         @Override
	         public boolean collapseActionView() {
	         
	            return false;
	         }

	         @Override
	         public boolean expandActionView() {
	        
	            return false;
	         }

	        
	         @Override
	         public View getActionView()
	         {
	      
	        	 return null;
	         }

	         @Override
	         public char getAlphabeticShortcut() {
	           
	            return 0;
	         }

	         @Override
	         public int getGroupId() {
	           
	            return 0;
	         }

	         @Override
	         public Drawable getIcon() 
	         {
	            
	            return null;
	         }

	         @Override
	         public Intent getIntent() {
	            
	            return null;
	         }

	         @Override
	         public ContextMenuInfo getMenuInfo() {
	          
	            return null;
	         }

	         @Override
	         public char getNumericShortcut() {
	        
	            return 0;
	         }

	         @Override
	         public int getOrder() {
	          
	            return 0;
	         }

	         @Override
	         public SubMenu getSubMenu() {
	          
	            return null;
	         }

	         @Override
	         public CharSequence getTitle() {
	         
	            return null;
	         }

	         @Override
	         public CharSequence getTitleCondensed() {
	         
	            return null;
	         }

	         @Override
	         public boolean hasSubMenu() {
	         
	            return false;
	         }

	         @Override
	         public boolean isActionViewExpanded() {
	            
	            return false;
	         }

	         @Override
	         public boolean isCheckable() {
	         
	            return false;
	         }

	         @Override
	         public boolean isChecked() {
	          
	            return false;
	         }

	         @Override
	         public boolean isVisible()
	         {
	       
	            return false;
	         }

	         @Override
	         public android.view.MenuItem setActionProvider(ActionProvider actionProvider) {
	            
	            return null;
	         }
	         @Override
	         public android.view.MenuItem setActionView(View view) {
	           
	            return null;
	         }

	         @Override
	         public android.view.MenuItem setActionView(int resId) {
	           
	            return null;
	         }

	         @Override
	         public android.view.MenuItem setAlphabeticShortcut(char alphaChar) {
	            
	            return null;
	         }

	         @Override
	         public android.view.MenuItem setCheckable(boolean checkable) {
	           
	            return null;
	         }

	         @Override
	         public android.view.MenuItem setChecked(boolean checked) {
	         
	            return null;
	         }

	         @Override
	         public android.view.MenuItem setEnabled(boolean enabled) {
	            
	            return null;
	         }

	         @Override
	         public android.view.MenuItem setIcon(Drawable icon) {
	         
	            return null;
	         }

	         @Override
	         public android.view.MenuItem setIcon(int iconRes) {
	         
	            return null;
	         }

	         @Override
	         public android.view.MenuItem setIntent(Intent intent) {
	           
	            return null;
	         }

	         @Override
	         public android.view.MenuItem setNumericShortcut(char numericChar) {
	           
	            return null;
	         }

	         @Override
	         public android.view.MenuItem setOnActionExpandListener(OnActionExpandListener listener) {
	           
	            return null;
	         }

	         @Override
	         public android.view.MenuItem setOnMenuItemClickListener(OnMenuItemClickListener menuItemClickListener) {
	            
	            return null;
	         }

	         @Override
	         public android.view.MenuItem setShortcut(char numericChar, char alphaChar) {
	        
	            return null;
	         }

	         @Override
	         public void setShowAsAction(int actionEnum) {
	           

	         }

	         @Override
	         public android.view.MenuItem setShowAsActionFlags(int actionEnum) {
	            
	            return null;
	         }

	         @Override
	         public android.view.MenuItem setTitle(CharSequence title) {
	           
	            return null;
	         }

	         @Override
	         public android.view.MenuItem setTitle(int title) {
	           
	            return null;
	         }

	         @Override
	         public android.view.MenuItem setTitleCondensed(CharSequence title) {
	          
	            return null;
	         }

	         @Override
	         public android.view.MenuItem setVisible(boolean visible) {
	           
	            return null;
	         }

			@Override
			public ActionProvider getActionProvider() {
			
				return null;
			}
	      };
	   }
	
	 @Override
    public void setTitle(CharSequence title) 
	 {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
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
