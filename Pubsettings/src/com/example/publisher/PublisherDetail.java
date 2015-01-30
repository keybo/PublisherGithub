package com.example.publisher;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.ShareActionProvider;
import com.example.pubfeed.JSONFeed;
import com.example.publisherarticle.R;
import com.example.publisherarticle.SplashActivity;

public class PublisherDetail extends SherlockFragmentActivity 
{
	JSONFeed feed;
	int pos;
	private DescAdapter adapter;
	private ViewPager pager;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detailvedio);

		 getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		 getSupportActionBar().setDisplayShowHomeEnabled(true);
		 getSupportActionBar().setIcon(R.drawable.home);
		feed = (JSONFeed) getIntent().getExtras().get("feed");
		pos = getIntent().getExtras().getInt("pos");

		adapter = new DescAdapter(getSupportFragmentManager());
		pager = (ViewPager) findViewById(R.id.pager);
		pager.setPageTransformer(true, new ViewPager.PageTransformer()
		{	
			@Override
			public void transformPage(View view, float position) 
			{
				view.setRotationX(position * -100);
			}
		});
		pager.setPageTransformer(true, new ViewPager.PageTransformer()
		{
			 
			@Override
			public void transformPage(View view, float position) 
			{
		        view.setRotationY(position * -120);
			}
		});
		pager.setAdapter(adapter);
		pager.setCurrentItem(pos);

			}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		switch(item.getItemId()) 
		{
		case android.R.id.home:
			startActivity(new Intent(PublisherDetail.this,SplashActivity.class));
			break;
		case R.id.usercomment:
			startActivity(new Intent(PublisherDetail.this,PublishComments.class));
			break;
		
		}
		return super.onOptionsItemSelected(item);
	}
	@SuppressLint("NewApi")
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater=getSupportMenuInflater();
		inflater.inflate(R.menu.detail_int_menu, menu);
		 MenuItem actionItem = menu.findItem(R.id.socialshare);
		 ShareActionProvider provider =(ShareActionProvider) actionItem.getActionProvider();
	  if (provider != null)
	  {
	        Intent shareIntent = new Intent();
	        shareIntent.setAction(Intent.ACTION_SEND);
	        shareIntent.putExtra(Intent.EXTRA_TEXT, feed.getItem(pos).getTitle());
	        shareIntent.putExtra(Intent.EXTRA_SUBJECT, feed.getItem(pos).getDescription());
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
			PublisherFragment frag = new PublisherFragment();
			Bundle bundle = new Bundle();
			bundle.putSerializable("feed", feed);
			bundle.putInt("pos", position);
			frag.setArguments(bundle);
			return frag;

		}

	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{		
		return false;
	}

	
}
