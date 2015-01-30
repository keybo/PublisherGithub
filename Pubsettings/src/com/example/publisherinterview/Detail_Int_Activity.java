package com.example.publisherinterview;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
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
import com.example.publisherarticle.R;


@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class Detail_Int_Activity extends SherlockFragmentActivity 
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

			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			getSupportActionBar().setDisplayShowHomeEnabled(true);
			getSupportActionBar().setIcon(R.drawable.home);
		
			feed = (JSONFeed) getIntent().getExtras().get("feed");
			pos = getIntent().getExtras().getInt("pos");
		
			adapter = new Desc_Int_Adapter(getSupportFragmentManager());
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
			startActivity(new Intent(Detail_Int_Activity.this,com.example.publisherinterview.Splash_Int_Activity.class));
			break;
		case R.id.usercomment:
			Intent usercomments=new Intent(Detail_Int_Activity.this,InterviewComments.class);
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
