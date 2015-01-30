package com.example.publisherarticle;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.pubfeed.JSONFeed;
import com.example.pubimage.ImageLoader;



public class DetailFragment extends Fragment 
{
	private int fPos;
	JSONFeed jFeed;
	ImageLoader imageLoader;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		jFeed = (JSONFeed) getArguments().getSerializable("feed");
		fPos = getArguments().getInt("pos");
	//	imageLoader = new ImageLoader(getActivity().getApplicationContext());
	}

	@SuppressLint("SetJavaScriptEnabled")
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.detail_fragment, container, false);
		TextView title = (TextView) view.findViewById(R.id.title);
		
		TextView authorName=(TextView)view.findViewById(R.id.author);
		WebView comments=(WebView)view.findViewById(R.id.comments);
		/*ImageView image=(ImageView)view.findViewById(R.id.detailimage);
		TextView  time= (TextView) view.findViewById(R.id.date);
		TextView desc = (TextView) view.findViewById(R.id.desc);
		
		imageLoader.DisplayImage(jFeed.getItem(fPos).getImage(), image);
		title.setText(jFeed.getItem(fPos).getTitle());
		time.setText(jFeed.getItem(fPos).getDate());
		desc.setText(jFeed.getItem(fPos).getDescription());
		
		
		//imageLoader.DisplayImage("http://10.0.2.2:80/images/sales.png", title1);
		//imageLoader.DisplayImage(jFeed.getItem(fPos).getImage(), title1);*/
		WebView desc = (WebView) view.findViewById(R.id.desc);
		WebSettings ws = desc.getSettings();
		ws.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		ws.setLightTouchEnabled(false);
		ws.setPluginState(PluginState.ON);
		
		ws.setBuiltInZoomControls(true);
		ws.setJavaScriptEnabled(true);
		
		
		WebView image = (WebView) view.findViewById(R.id.image);
		WebSettings imageWeb = image.getSettings();
		imageWeb.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		imageWeb.setLightTouchEnabled(false);
		imageWeb.setPluginState(PluginState.ON);
		imageWeb.setJavaScriptEnabled(true);
		imageWeb.setBuiltInZoomControls(true);
		
		//String htmlComments = getHtmlComment("baburaj","babu001");
		WebSettings webSettings2 = comments.getSettings();
		webSettings2.setJavaScriptEnabled(true);
		webSettings2.setBuiltInZoomControls(true);
		comments.requestFocusFromTouch();
		comments.setWebViewClient(new WebViewClient());
		comments.setWebChromeClient(new WebChromeClient());
		//comments.loadData(htmlComments, "text/html", null);
		comments.loadUrl("http://baburajannamalai.webs.com/Publisher/disqus.html");
		ScrollView sv = (ScrollView) view.findViewById(R.id.sv);
		sv.setVerticalFadingEdgeEnabled(true);
		
	
		String data="<html><head><title>Example</title><meta name=\"viewport\"\"content=\"width="+100+"%"+", initial-scale=0.65 \" /></head>";
		data=data+"<body><center><img width="+100+"%"+"\" src=\""+jFeed.getItem(fPos).getImage()+"\" /></center></body></html>";
	//	image.loadDataWithBaseUrl("",data, "text/html", null);
		image.loadDataWithBaseURL(null,data,"text/html", "utf-8", null);
		title.setText(jFeed.getItem(fPos).getTitle());
		authorName.setText("Authored by:"+jFeed.getItem(fPos).getAuthorName());
		String text="<html><body style=\"text-align:justify\"> %s </body></Html>";
		 
		String webtext=jFeed.getItem(fPos).getDescription();
		desc.loadDataWithBaseURL(null,String.format(text,webtext),"text/html"," charset=UTF-8",null);
	//	image.loadUrl(jFeed.getItem(fPos).getImage());
		//desc.loadDataWithBaseURL(" ",jFeed.getItem(fPos).getDescription(), "text/html", "UTF-8", null);
		//desc.loadDataWithBaseURL("http://10.0.2.2:80/images/fetchdata.php",jFeed.getItem(fPos).getDescription(),"image/png", "UTF-8",null);

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
