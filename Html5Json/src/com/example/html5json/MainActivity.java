package com.example.html5json;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;

public class MainActivity extends Activity 
{
	WebView wvVideo;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		wvVideo=(WebView)findViewById(R.id.webView1);
		wvVideo.loadUrl("file:///android_asset/index.html");
	/*String ytVideo= "<html><body><iframe width='350' height='160' src='www.youtube.com/embed/InjL1JqiKhw' frameborder='0' allowfullscreen></iframe></body></html>";
		
	    wvVideo.getSettings().setAllowFileAccess(true);
	    wvVideo.getSettings().setPluginState(PluginState.ON);
	    wvVideo.getSettings().setBuiltInZoomControls(true);
	    wvVideo.getSettings().setJavaScriptEnabled(true);
	    wvVideo.loadData(ytVideo, "text/html", "utf-8");*/
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
