package com.meta.pubfavorite;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.meta.pubimage.ImageLoader;
import com.meta.pubui.R;
import com.meta.pubui.UiForPub;

public class FavFragment extends Fragment {
	private int fPos;

	ImageLoader imageLoader;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		UiForPub.setPreferenceTheme(getActivity());
		super.onCreate(savedInstanceState);

	}

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		String strtext = getArguments().getString("keyid");
		String strtextone = getArguments().getString("keyTitle");
		Log.e("Error from fragment" + strtext, strtextone);
		View view = inflater.inflate(R.layout.detail_fav_fragment, container,
				false);
		TextView title = (TextView) view.findViewById(R.id.title);
		TextView authorName = (TextView) view.findViewById(R.id.author);
		title.setText(strtext);
		authorName.setText(strtextone);

		authorName.setText("rajesh");
		return view;
	}

	@Override
	public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);

	}

}
