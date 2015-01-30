package com.meta.publisherarticle;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ShareActionProvider;

import com.meta.pubconstants.Constants;
import com.meta.pubfeed.Favorite;
import com.meta.pubfeed.JSONFeed;
import com.meta.pubsqlite.DatabaseHelper;
import com.meta.pubui.R;
import com.meta.pubui.UiForPub;

@SuppressLint("NewApi")
public class DetailActivity extends FragmentActivity implements
		LoaderManager.LoaderCallbacks<Cursor>, OnTouchListener {
	JSONFeed feed;
	int pos;
	private DescAdapter adapter;
	private ViewPager pager;
	DatabaseHelper db;
	boolean mFullScreen;
	int mPreviousStackCount;
	ImageView cancelFullscreen;
	View mMainView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		UiForPub.setPreferenceTheme(this);
		super.onCreate(savedInstanceState);

		mMainView = getLayoutInflater().inflate(R.layout.detail, null);
		setContentView(mMainView);

		cancelFullscreen = (ImageView) findViewById(R.id.cancelFullscreenBtn);
		db = new DatabaseHelper(this);
		db.openToWrite();

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayShowHomeEnabled(true);
		getActionBar().setIcon(R.drawable.home);
		feed = (JSONFeed) getIntent().getExtras().get("feed");
		pos = getIntent().getExtras().getInt("pos");

		adapter = new DescAdapter(getSupportFragmentManager());
		pager = (ViewPager) findViewById(R.id.pager);
		pager.setAdapter(adapter);

		pager.setPageTransformer(true, new ViewPager.PageTransformer() {

			@Override
			public void transformPage(View view, float position) {

				view.setRotationX(position * -100);

			}
		});
		pager.setPageTransformer(true, new ViewPager.PageTransformer() {

			@Override
			public void transformPage(View view, float position) {

				view.setRotationY(position * -120);

			}
		});

		pager.setCurrentItem(pos);

		if (savedInstanceState != null) {
			mFullScreen = savedInstanceState
					.getBoolean(Constants.EXTENDED_FULLSCREEN);

			// Sets the fullscreen flag to its previous state
			setFullScreen(mFullScreen);
		}
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		Favorite fav = new Favorite();

		switch (item.getItemId()) {
		case android.R.id.home:
			Intent home = new Intent(this, SplashActivity.class);
			home.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(home);
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
			Intent intent = new Intent(DetailActivity.this,
					ArticleComments.class);
			intent.putExtras(bundle);
			intent.putExtra("pos", pos);
			startActivity(intent);

			break;
		case R.id.fullscreen:
			setFullScreen(true);

			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		menu.clear();
		getMenuInflater().inflate(R.menu.detailmenu, menu);

		List<Favorite> c = new ArrayList<Favorite>();

		c = db.getAllFavorite();
		db.openToRead();

		for (Favorite f : c) {
			int r = f.getFav();
			Log.e("This  is eroor Cute" + r, "This is rokro" + r);
			if (r == pos) {
				MenuItem item = menu.findItem(R.id.rate);

				item.setTitle(R.string.app_name).setIcon(
						R.drawable.rating_important);
			} else {
				MenuItem item = menu.findItem(R.id.unrate);
				item.setTitle(R.string.app_name).setIcon(
						R.drawable.rating_not_important);
			}
		}
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

	public void setFullScreen(boolean fullscreen) {

		getWindow().setFlags(
				fullscreen ? WindowManager.LayoutParams.FLAG_FULLSCREEN : 0,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		mFullScreen = fullscreen;

		if (Build.VERSION.SDK_INT >= 11) {

			int flag = fullscreen ? View.SYSTEM_UI_FLAG_LOW_PROFILE : 0;

			if (Build.VERSION.SDK_INT >= 14 && fullscreen) {

				flag |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
			}

			mMainView.setSystemUiVisibility(flag);

			if (fullscreen) {
				this.getActionBar().hide();
				cancelFullscreen.setVisibility(View.VISIBLE);
			} else {
				this.getActionBar().show();
				cancelFullscreen.setVisibility(View.GONE);
			}
		}
	}

	public void onClickCancelFullscreenBtn(View view) {
		setFullScreen(false);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putBoolean(Constants.EXTENDED_FULLSCREEN, mFullScreen);
		super.onSaveInstanceState(outState);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onTouchEvent(android.view.MotionEvent ev) {
		if (super.onTouchEvent(ev)) {
			return false;
		}
		return false;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {

		return null;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub

	}

}
