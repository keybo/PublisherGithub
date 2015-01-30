package com.example.publisher;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.VideoView;

import com.actionbarsherlock.app.SherlockFragment;
import com.example.pubfeed.JSONFeed;
import com.example.pubimage.ImageLoader;
import com.example.publisherarticle.R;

public class PublisherFragment extends SherlockFragment
{

	private int fPos;
	VideoView video;
	JSONFeed jFeed;
	ImageLoader imageLoader;
	MediaController media;
	ImageButton play;
	
	Uri uri;
    String vurl=null;
	
	
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		jFeed = (JSONFeed) getArguments().getSerializable("feed");
		fPos = getArguments().getInt("pos");
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
	     .detectAll()
	     .penaltyLog()
	     .build();
		
		StrictMode.setThreadPolicy(policy);
		
		
	//	imageLoader = new ImageLoader(getActivity().getApplicationContext());
	}

	@SuppressLint({ "SetJavaScriptEnabled", "NewApi" })
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.detail_v_fragment, container, false);
		TextView title = (TextView) view.findViewById(R.id.title);
		 play=(ImageButton)view.findViewById(R.id.home);
         play.setOnClickListener(new View.OnClickListener() 
         {
			
			@Override
			public void onClick(View v)
			{
				vurl=jFeed.getItem(fPos).getVideo();
				new myTask().execute(vurl);
				
			}
		});
		//ImageView authorImage=(ImageView)view.findViewById(R.id.authorimage);
		//TextView authorName=(TextView)view.findViewById(R.id.author);
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
		ws.setJavaScriptEnabled(false);

		video = (VideoView) view.findViewById(R.id.image);
		
			
		ScrollView sv = (ScrollView) view.findViewById(R.id.sv);
		sv.setVerticalFadingEdgeEnabled(true);
		
	
		//String htmlComments = getHtmlComment("yourId", "yourShortName");
		String data="<html><head><title>Example</title><meta name=\"viewport\"\"content=\"width=100%"+100+", initial-scale=0.65 \" /></head>";
		data=data+"<body><center><img width=100%"+100+"\" src=\""+jFeed.getItem(fPos).getImage()+"\" /></center></body></html>";
		
		title.setText(jFeed.getItem(fPos).getTitle());
		//authorName.setText("Authored by:"+jFeed.getItem(fPos).getAuthorName());
		String text="<html><body style=\"text-align:justify\"> %s </body></Html>";
		 
		String webtext=jFeed.getItem(fPos).getDescription();
		desc.loadDataWithBaseURL("",String.format(text,webtext),"text/html","utf-8",null);
	//	image.loadUrl(jFeed.getItem(fPos).getImage());
		//desc.loadDataWithBaseURL(" ",jFeed.getItem(fPos).getDescription(), "text/html", "UTF-8", null);
		//desc.loadDataWithBaseURL("http://10.0.2.2:80/images/fetchdata.php",jFeed.getItem(fPos).getDescription(),"image/png", "UTF-8",null);

		return view;
	}
	
	
	@Override
	public void onDestroy() 
	{
	
		super.onDestroy();
		video.clearFocus();
		//getActivity().finish();
	}

	class myTask extends AsyncTask<String, Uri, Void> 
	{
		Integer track = 0;
        ProgressDialog dialog;
 
        protected void onPreExecute()
        {
            dialog = new ProgressDialog(getActivity());
            dialog.setMessage("Video Loading, Please Wait...");
            dialog.setCancelable(true);
            dialog.show();
        }
 
        protected void onProgressUpdate(final Uri... uri) {
 
            try {
 
                media=new MediaController(getActivity());
                video.setMediaController(media);
                media.setPrevNextListeners(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                      
 
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
 
                        getActivity().finish();
                    }
                });
                media.show(10000);
 
                video.setVideoURI(uri[0]);
                video.requestFocus();
                video.setOnPreparedListener(new OnPreparedListener()
                {
 
                    public void onPrepared(MediaPlayer arg0)
                    {
                        video.start();
                        dialog.dismiss();
                        
                    }
                });
               
                 
 
            } 
            catch (IllegalArgumentException e) 
            {
                e.printStackTrace();
            } 
            catch (IllegalStateException e)
            {
                e.printStackTrace();
            } 
            catch (SecurityException e)
            {
               
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
            catch (Exception e)
            {
                e.printStackTrace();
            }
 
            return null;
        }
 
         
    }
		
 public	String getHtmlComment(String idPost, String shortName)
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
				+ "dsq.src = 'http://' + babu001 + '.disqus.com/embed.js';"
				+ "(document.getElementsByTagName('head')[0] || document.getElementsByTagName('body')[0]).appendChild(dsq); })();"
				+ "</script>";
	}
	/*private String getDataSource(String path) throws IOException 
	{
		if (!URLUtil.isNetworkUrl(path)) 
		{
			return path;
		}
		else 
		{
			URL url = new URL(path);
			URLConnection cn = url.openConnection();
			cn.connect();
			InputStream stream = cn.getInputStream();
			if (stream == null)
				throw new RuntimeException("stream is null");
			File temp = File.createTempFile("mediaplayertmp", "dat");
			temp.deleteOnExit();
			
			String tempPath = temp.getAbsolutePath();
			FileOutputStream out = new FileOutputStream(temp);
			byte buf[] = new byte[1024*24];
			do
			{
				int numread = stream.read(buf);
				if (numread <= 0)
					break;
				out.write(buf, 0, numread);
			}
			while (true);
			try
			{
				stream.close();
			} 
			catch (IOException ex)
			{
				Log.e("this is error", "error: " + ex.getMessage(), ex);
			}
			return tempPath;
		}
	}*/
	
	}
