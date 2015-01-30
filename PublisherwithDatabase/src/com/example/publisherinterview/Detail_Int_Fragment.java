package com.example.publisherinterview;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.pubfeed.JSONFeed;
import com.example.pubimage.ImageLoader;
import com.example.pubui.R;

public class Detail_Int_Fragment extends Fragment {
	private int fPos;
	JSONFeed jFeed;
	ImageLoader imageLoader;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		jFeed = (JSONFeed) getArguments().getSerializable("feed");
		fPos = getArguments().getInt("pos");
		// imageLoader = new ImageLoader(getActivity().getApplicationContext());

	}

	@SuppressLint("SetJavaScriptEnabled")
	@SuppressWarnings("deprecation")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.detail_int_fragment, container,
				false);
		TextView title = (TextView) view.findViewById(R.id.title);
		TextView authorName = (TextView) view.findViewById(R.id.author);
		/*
		 * ImageView image=(ImageView)view.findViewById(R.id.detailimage);
		 * TextView time= (TextView) view.findViewById(R.id.date); TextView desc
		 * = (TextView) view.findViewById(R.id.desc);
		 * 
		 * imageLoader.DisplayImage(jFeed.getItem(fPos).getImage(), image);
		 * title.setText(jFeed.getItem(fPos).getTitle());
		 * time.setText(jFeed.getItem(fPos).getDate());
		 * desc.setText(jFeed.getItem(fPos).getDescription());
		 * 
		 * 
		 * //imageLoader.DisplayImage("http://10.0.2.2:80/images/sales.png",
		 * title1); //imageLoader.DisplayImage(jFeed.getItem(fPos).getImage(),
		 * title1);
		 */
		WebView desc = (WebView) view.findViewById(R.id.desc);
		WebView image = (WebView) view.findViewById(R.id.image);

		ScrollView sv = (ScrollView) view.findViewById(R.id.sv);
		sv.setVerticalFadingEdgeEnabled(true);
		Display display = getActivity().getWindowManager().getDefaultDisplay();
		int width = display.getWidth();
		// String htmlComments = getHtmlComment("yourId", "yourShortName");
		String data = "<html><head><title>Example</title><meta name=\"viewport\"\"content=\"width="
				+100+"%"+", initial-scale=0.65 \" /></head>";
		data = data + "<body><center><img width="+100+"%"+"\" src=\""
				+ jFeed.getItem(fPos).getImage()
				+ "\" /></center></body></html>";
		image.loadData(data, "text/html", null);
		authorName
				.setText("Authored by:" + jFeed.getItem(fPos).getAuthorName());

		title.setText(jFeed.getItem(fPos).getTitle());
		String text = "<html><body style=\"text-align:justify\"> %s </body></Html>";

		String webtext = jFeed.getItem(fPos).getDescription();
		desc.loadData(String.format(text, webtext), "text/html", null);
		// image.loadUrl(jFeed.getItem(fPos).getImage());
		// desc.loadDataWithBaseURL("http://10.0.2.2:80/article/fetchinterview.php",jFeed.getItem(fPos).getDescription(),
		// "text/html", "UTF-8", null);
		// desc.loadDataWithBaseURL("http://10.0.2.2:80/images/fetchdata.php",jFeed.getItem(fPos).getDescription(),"image/png",
		// "UTF-8",null);

		return view;
	}

	public String getHtmlComment(String idPost, String shortName)
	{
		return "<div id='disqus_thread'></div>"
				+ "<script type='text/javascript'>"
				+ "var disqus_identifier = '"
				+ idPost
				+ "';"
				+ "var disqus_shortname = '"
				+ shortName
				+ "';"
				+ " (function() { var dsq = document.createElement('script'); dsq.type = 'text/javascript'; dsq.async = true;"
				+ "dsq.src = 'http://' + disqus_shortname + '.disqus.com/embed.js';"
				+ "(document.getElementsByTagName('head')[0] || document.getElementsByTagName('body')[0]).appendChild(dsq); })();"
				+ "</script>";
	}
}
