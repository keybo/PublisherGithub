package com.meta.pubfavorite;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ShareActionProvider;

import com.meta.pubfeed.Favorite;
import com.meta.pubfeed.JSONFeed;
import com.meta.publisherarticle.ArticleComments;
import com.meta.publisherarticle.DetailFragment;
import com.meta.pubsqlite.DatabaseHelper;
import com.meta.pubui.R;
import com.meta.pubui.UiForPub;

@SuppressLint("NewApi")
public class FavoriteAct extends FragmentActivity {

	JSONFeed feed;
	int pos;
	private DescAdapter adapter;
	private ViewPager pager;
	DatabaseHelper db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		UiForPub.setPreferenceTheme(this);
		super.onCreate(savedInstanceState);

		setContentView(R.layout.detail);

		db = new DatabaseHelper(this);
		db.openToWrite();
		Bundle extras = getIntent().getExtras();

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayShowHomeEnabled(true);
		getActionBar().setIcon(R.drawable.home);
		// Intent b = getIntent();
		if (extras != null) {
			String id = extras.getString("id");
			String keyTitle = extras.getString("title");
			String keyid = extras.getString("desc");
			String timed = extras.getString("time");
			String imageurl = extras.getString("image");

			Log.e("Key.............Title" + keyTitle, "position" + keyid + ""
					+ timed + "" + imageurl);
		}
		pager.setCurrentItem(pos);
		adapter = new DescAdapter(getSupportFragmentManager());
		pager = (ViewPager) findViewById(R.id.pager);
		pager.setAdapter(adapter);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Favorite fav = new Favorite();
		switch (item.getItemId()) {
		case R.id.rate:
			fav.setFav(pos);
			fav.setFavTitle(feed.getItem(pos).getTitle());
			fav.setImage(feed.getItem(pos).getImage());
			fav.setFavDesc(feed.getItem(pos).getDescription());
			fav.setDate(feed.getItem(pos).getDate());
			db.createFav(fav);
			break;
		case R.id.usercomment:
			Bundle bundle = new Bundle();
			bundle.putSerializable("feed", feed);
			Intent intent = new Intent(FavoriteAct.this, ArticleComments.class);
			intent.putExtras(bundle);
			intent.putExtra("pos", pos);
			startActivity(intent);
			break;

		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.detailmenu, menu);
		ShareActionProvider provider = (ShareActionProvider) menu.findItem(
				R.id.socialshare).getActionProvider();
		if (provider != null) {
			Intent shareIntent = new Intent();
			shareIntent.setAction(Intent.ACTION_SEND);
			shareIntent.putExtra(Intent.EXTRA_TEXT, feed.getItem(pos)
					.getTitle());
			shareIntent.putExtra(Intent.EXTRA_SUBJECT, feed.getItem(pos)
					.getDescription());
			shareIntent.setType("text/plain");
			provider.setShareIntent(shareIntent);
		}
		return true;
	}

	public class DescAdapter extends FragmentStatePagerAdapter {
		public DescAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public int getCount() {
			return feed.getItemCount();
		}

		@Override
		public Fragment getItem(int position) {
			DetailFragment frag = new DetailFragment();
			Bundle bundle = new Bundle();
			bundle.putSerializable("feed", feed);
			bundle.putInt("pos", position);
			frag.setArguments(bundle);
			return frag;

		}

	}

}
