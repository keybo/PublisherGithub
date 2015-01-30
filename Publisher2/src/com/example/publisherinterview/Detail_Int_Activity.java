package com.example.publisherinterview;


import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ShareActionProvider;

import com.example.pubfeed.JSONFeed;
import com.example.publisherarticle.R;


@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class Detail_Int_Activity extends FragmentActivity 
{
	JSONFeed feed;
	int pos;
	private Desc_Int_Adapter adapter;
	private ViewPager pager;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.intdetail);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayShowHomeEnabled(true);
		getActionBar().setIcon(R.drawable.home);
		feed = (JSONFeed) getIntent().getExtras().get("feed");
		pos = getIntent().getExtras().getInt("pos");
		
		adapter = new Desc_Int_Adapter(getSupportFragmentManager());
		pager = (ViewPager) findViewById(R.id.pager);

		
		pager.setAdapter(adapter);
		pager.setCurrentItem(pos);

	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		switch(item.getItemId()) 
		{
		case android.R.id.home:
			startActivity(new Intent(Detail_Int_Activity.this,com.example.publisherinterview.Splash_Int_Activity.class));
			break;
		
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
	  getMenuInflater().inflate(R.menu.detail_int_menu, menu);
	  ShareActionProvider provider =(ShareActionProvider) menu.findItem(R.id.socialshare).getActionProvider();
	  if (provider != null)
	  {
	        Intent shareIntent = new Intent();
	        shareIntent.setAction(Intent.ACTION_SEND);
	        shareIntent.putExtra(Intent.EXTRA_STREAM, shareIntent.describeContents());
	        shareIntent.setType("text/plain");
	        provider.setShareIntent(shareIntent);
	    }
    return true;
     
	}

	public class Desc_Int_Adapter extends FragmentStatePagerAdapter
	{
		public Desc_Int_Adapter(FragmentManager fm)
		{
			super(fm);
		}

		@Override
		public int getCount() 
		{
			return feed.getItemCount();
		}

		@Override
		public Fragment getItem(int position) 
		{
			Detail_Int_Fragment frag = new Detail_Int_Fragment();
			Bundle bundle = new Bundle();
			bundle.putSerializable("feed", feed);
			bundle.putInt("pos", position);
			frag.setArguments(bundle);
			return frag;

		}

	}

}
