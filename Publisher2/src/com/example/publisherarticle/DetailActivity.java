package com.example.publisherarticle;

import android.annotation.SuppressLint;
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


@SuppressLint("NewApi")
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class DetailActivity extends FragmentActivity 
{

	JSONFeed feed;
	int pos;
	private DescAdapter adapter;
	private ViewPager pager;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail);

		try{
			
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayShowHomeEnabled(true);
		getActionBar().setIcon(R.drawable.home);
		feed = (JSONFeed) getIntent().getExtras().get("feed");
		pos = getIntent().getExtras().getInt("pos");

		
		adapter = new DescAdapter(getSupportFragmentManager());
		pager = (ViewPager) findViewById(R.id.pager);

		
		pager.setAdapter(adapter);
		pager.setCurrentItem(pos);
		}
		catch(Exception e)
		{
			
		}

	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		switch(item.getItemId()) 
		{
		case android.R.id.home:
			try
			{
			startActivity(new Intent(DetailActivity.this,SplashActivity.class));
			}
			catch(Exception e)
			{
				
			}
			break;
		
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
	  getMenuInflater().inflate(R.menu.detailmenu, menu);
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

	public class DescAdapter extends FragmentStatePagerAdapter
	{
		public DescAdapter(FragmentManager fm)
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
			DetailFragment frag = new DetailFragment();
			Bundle bundle = new Bundle();
			bundle.putSerializable("feed", feed);
			bundle.putInt("pos", position);
			frag.setArguments(bundle);
		    return frag;

		}

	}

}
