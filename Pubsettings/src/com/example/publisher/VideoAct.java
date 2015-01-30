package com.example.publisher;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

import com.example.pubfeed.JSONFeed;
import com.example.publisherarticle.R;

public class VideoAct extends FragmentActivity 
{
	 int fPos;
	
	JSONFeed feed;
	
	MediaController media;
	
	Uri uri;
    String vurl;
    VideoView video;
	
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.video_fragment);
	
		feed = (JSONFeed) getIntent().getExtras().get("feed");
		fPos = getIntent().getExtras().getInt("pos");
		video =(VideoView)findViewById(R.id.youtubewebView);
	    vurl=feed.getItem(fPos).getVideo();
	    new myTask().execute(vurl);
	}
	@Override
	protected void onPostCreate(Bundle savedInstanceState) 
	{
		super.onPostCreate(savedInstanceState);
		
	}

	class myTask extends AsyncTask<String, Uri, Void> 
			{
			   
			  ProgressDialog dialog;
	        protected void onPreExecute()
		        {
		            dialog = new ProgressDialog(VideoAct.this);
		            dialog.setMessage("Video Loading, Please Wait...");
		            dialog.setCancelable(true);
		            dialog.show();
		        }
		 
		        protected void onProgressUpdate(final Uri... uri) {
		 
		            try {
		 
		                media=new MediaController(VideoAct.this);
		                video.setMediaController(media);
		                media.setPrevNextListeners(new View.OnClickListener() 
		                {
		                    @Override
		                    public void onClick(View v)
		                    {
		                      
		 
		                    }
		                }, new View.OnClickListener() {
		                    @Override
		                    public void onClick(View v) {
		 
		                        finish();
		                    }
		                });
		                media.show(10000);
		 
		                video.setVideoURI(uri[0]);
		                video.requestFocus();
		                video.setOnPreparedListener(new OnPreparedListener()
		                {
		                    public void onPrepared(MediaPlayer arg0) {
		                        video.start();
		                        dialog.dismiss();
		                    }
		                });
		                 
		 
		            } catch (IllegalArgumentException e) {
		                e.printStackTrace();
		            } catch (IllegalStateException e) {
		                e.printStackTrace();
		            } catch (SecurityException e) {
		                // TODO Auto-generated catch block
		                e.printStackTrace();
		            }
		             
		 
		        }
		 
		        @Override
		        protected Void doInBackground(String... params)
		        {
		            try
		            {
		              uri = Uri.parse(params[0]);
		                 
		                publishProgress(uri);
		            } 
		            catch (Exception e) {
		                e.printStackTrace();
		 
		            }
		 
		            return null;
		        }
		}
}
