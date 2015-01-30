package com.example.publisherinterview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.publisherarticle.DetailActivity;
import com.example.publisherarticle.R;

public class InterviewComments extends Activity
{
	WebView webDisqus;

	@SuppressLint({ "NewApi", "SetJavaScriptEnabled" })
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webcomments);
		String htmlComments = getHtmlComment("babu001", "baburaj");
		//getActionBar().setDisplayHomeAsUpEnabled(true);
		//getActionBar().setDisplayShowHomeEnabled(true);
		webDisqus = (WebView) findViewById(R.id.disqus);
		// set up disqus
		WebSettings webSettings2 = webDisqus.getSettings();
		webSettings2.setJavaScriptEnabled(true);
		webSettings2.setBuiltInZoomControls(true);
		webDisqus.requestFocusFromTouch();
		webDisqus.setWebViewClient(new WebViewClient());
		webDisqus.setWebChromeClient(new WebChromeClient());
		webDisqus.loadData(htmlComments, "text/html", null);
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

	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		switch(item.getItemId()) 
		{
		case android.R.id.home:
				Intent homeIntent = new Intent(this,DetailActivity.class);
		      homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		      startActivity(homeIntent);
			break;
		
		
		}
		return super.onOptionsItemSelected(item);
	}
}