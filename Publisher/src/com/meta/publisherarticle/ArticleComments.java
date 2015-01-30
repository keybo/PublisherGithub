package com.meta.publisherarticle;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.meta.pubfeed.JSONFeed;
import com.meta.pubui.R;
import com.meta.pubui.UiForPub;

public class ArticleComments extends Activity
{

	WebView webDisqus;
	JSONFeed feed;
	String htmlComments;
	int pos;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		UiForPub.setPreferenceTheme(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webcomments);

		webDisqus = (WebView) findViewById(R.id.disqus);
		feed = (JSONFeed) getIntent().getExtras().get("feed");
		pos = getIntent().getExtras().getInt("pos");

		htmlComments = getHtmlComment(feed.getItem(pos).getTitle(), "baburaj");

		webDisqus = (WebView) findViewById(R.id.disqus);

		// set up disqus
		WebSettings webSettings2 = webDisqus.getSettings();
		webSettings2.setJavaScriptEnabled(true);
		webSettings2.setBuiltInZoomControls(true);

		webDisqus.setWebViewClient(new WebViewClient());
		webDisqus.setWebChromeClient(new WebChromeClient());

		webSettings2.setDefaultTextEncodingName("utf-8");

		webDisqus
				.loadUrl("http://baburajannamalai.webs.com/Publisher/disqus.html");

		// webDisqus.loadData(htmlComments,"text/html","ISO-8859-1");
		webDisqus.requestFocusFromTouch();
		// webDisqus.loadDataWithBaseURL("",htmlComments, "text/html", "utf-8",
		// "");
		webDisqus.clearHistory();

	}

	public String getHtmlComment(String idPost, String shortName) {

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