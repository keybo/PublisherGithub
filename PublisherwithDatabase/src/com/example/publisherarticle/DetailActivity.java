package com.example.publisherarticle;

import java.util.ArrayList;
import java.util.List;

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
import android.view.View;
import android.widget.ShareActionProvider;

import com.example.pubfeed.Favorite;
import com.example.pubfeed.JSONFeed;
import com.example.pubsqlite.DatabaseHelper;
import com.example.pubui.R;
import com.example.pubui.UiForPub;
import com.viewpagerindicator.CirclePageIndicator;

@SuppressLint("NewApi")
public class DetailActivity extends FragmentActivity {

	JSONFeed feed;
	int pos;
	private DescAdapter adapter;
	private ViewPager pager;
	DatabaseHelper db;
	CirclePageIndicator titleIndicator;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		UiForPub.setPreferenceTheme(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail);

		db = new DatabaseHelper(this);
		db.openToWrite();
		titleIndicator = (CirclePageIndicator)findViewById(R.id.titles);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayShowHomeEnabled(true);
		getActionBar().setIcon(R.drawable.home);
		feed = (JSONFeed) getIntent().getExtras().get("feed");
		pos = getIntent().getExtras().getInt("pos");

		adapter = new DescAdapter(getSupportFragmentManager());
		pager = (ViewPager) findViewById(R.id.pager);
		pager.setAdapter(adapter);
		titleIndicator.setViewPager(pager);
		pager.setPageTransformer(true, new ViewPager.PageTransformer() {
			// private final static float MIN_SCALE = 0.85f;
			// private static final float MIN_ALPHA = 0.5f;
			// To Zoomout operation
			@Override
			public void transformPage(View view, float position) {

				/*
				 * int pageWidth = view.getWidth(); int pageHeight =
				 * view.getHeight();
				 */
				view.setRotationX(position * -100);
				/*
				 * if (position < -1) { // [-Infinity,-1) // This page is way
				 * off-screen to the left. view.setAlpha(0);
				 * 
				 * } else if (position <= 1) { // [-1,1] // Modify the default
				 * slide transition to shrink the page as well float scaleFactor
				 * = Math.max(MIN_SCALE, 1 - Math.abs(position)); float
				 * vertMargin = pageHeight * (1 - scaleFactor) / 2; float
				 * horzMargin = pageWidth * (1 - scaleFactor) / 2; if (position
				 * < 0) { view.setTranslationX(horzMargin - vertMargin / 2); }
				 * else { view.setTranslationX(-horzMargin + vertMargin / 2); }
				 * 
				 * // Scale the page down (between MIN_SCALE and 1)
				 * view.setScaleX(scaleFactor); view.setScaleY(scaleFactor);
				 * 
				 * // Fade the page relative to its size.
				 * view.setAlpha(MIN_ALPHA + (scaleFactor - MIN_SCALE) / (1 -
				 * MIN_SCALE) * (1 - MIN_ALPHA));
				 * 
				 * } else { // (1,+Infinity] // This page is way off-screen to
				 * the right. view.setAlpha(0); }
				 */
			}
		});
		pager.setPageTransformer(true, new ViewPager.PageTransformer() {
			// private final static float MIN_SCALE = 0.85f;
			// To Depth
			@Override
			public void transformPage(View view, float position) {
				// int pageWidth = view.getWidth();
				view.setRotationY(position * -120);
				/*
				 * if (position < -1) { // [-Infinity,-1) // This page is way
				 * off-screen to the left. view.setAlpha(0);
				 * 
				 * } else if (position <= 0) { // [-1,0] // Use the default
				 * slide transition when moving to the left page
				 * view.setAlpha(1); view.setTranslationX(0); view.setScaleX(1);
				 * view.setScaleY(1);
				 * 
				 * } else if (position <= 1) { // (0,1] // Fade the page out.
				 * view.setAlpha(1 - position);
				 * 
				 * // Counteract the default slide transition
				 * view.setTranslationX(pageWidth * -position);
				 * 
				 * // Scale the page down (between MIN_SCALE and 1) float
				 * scaleFactor = MIN_SCALE + (1 - MIN_SCALE) * (1 -
				 * Math.abs(position)); view.setScaleX(scaleFactor);
				 * view.setScaleY(scaleFactor);
				 * 
				 * } else { // (1,+Infinity] // This page is way off-screen to
				 * the right. view.setAlpha(0); }
				 */
			}
		});

		
		pager.setCurrentItem(pos);

	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
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
			Intent intent = new Intent(DetailActivity.this,
					ArticleComments.class);
			intent.putExtras(bundle);
			intent.putExtra("pos", pos);
			startActivity(intent);
			break;

		}
		return super.onOptionsItemSelected(item);
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
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

		/*
		 * @Override public void destroyItem(ViewGroup container, int position,
		 * Object object) { super.destroyItem(container, position, object);
		 * FragmentManager manager = ((Fragment)object).getFragmentManager();
		 * android.support.v4.app.FragmentTransaction trans =
		 * manager.beginTransaction(); trans.remove((Fragment)object);
		 * trans.commit(); }
		 */

	}

}
