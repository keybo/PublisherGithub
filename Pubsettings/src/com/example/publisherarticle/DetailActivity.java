package com.example.publisherarticle;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.ShareActionProvider;
import com.example.pubfeed.JSONFeed;


@SuppressLint("NewApi")
public class DetailActivity extends SherlockFragmentActivity 
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
	public boolean onOptionsItemSelected(final MenuItem item) 
	{
		switch(item.getItemId()) 
		{
		case android.R.id.home:
			startActivity(new Intent(DetailActivity.this,com.example.publisherarticle.SplashActivity.class));
			break;
		case R.id.usercomment:
			Intent usercomments=new Intent(DetailActivity.this,ArticleComments.class);
			startActivity(usercomments);
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
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
	        shareIntent.setType("text/plain");
	        shareIntent.putExtra(Intent.EXTRA_TEXT, feed.getItem(pos).getTitle());
	        shareIntent.putExtra(Intent.EXTRA_SUBJECT, feed.getItem(pos).getDescription());
	        //startActivity(Intent.createChooser(shareIntent, "Share Via"));
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
