package kah.json;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;

import android.app.Activity;
import android.os.Bundle;
import android.os.Debug;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;


public class JsonSampleActivity extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onStart()
	{
		super.onStart();

		// Downloading the RSS feed needs to be done on a separate thread.
		Thread downloadThread = new Thread(new Runnable() {

			public void run() {
				Debug.startMethodTracing("JsonReader");
				loadData();
				Debug.stopMethodTracing();
			}
		}, "Reading Thread");

		downloadThread.start();
	}

	private void loadData() {
		JsonReader reader = null;
		InputStream inStream ;
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpParams params = httpclient.getParams();
			HttpConnectionParams.setConnectionTimeout(params, 5000);
			HttpConnectionParams.setSoTimeout(params, 5000);
			HttpConnectionParams.setTcpNoDelay(params, true);

			HttpGet httppost = new HttpGet();
			httppost.setURI(new URI("baburajannamalai.webs.com/Publisher/qaa.json"));
			//httppost.setURI(new URI("baburajannamalai.webs.com/Publisher/qaa.json"));
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			inStream = entity.getContent();
		//= getResources().openRawResource(R.raw.json);

			BufferedInputStream bufferedStream = new BufferedInputStream(
					inStream);
			InputStreamReader streamReader = new InputStreamReader(
					bufferedStream);

			reader = new JsonReader(streamReader);

			populateTable(reader);
		} 
		catch (Exception e) {
			Log.e("Json Sample", e.getLocalizedMessage(), e);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					// Do nothing
				}
			}
		}
	}

	
	private void populateTable(JsonReader reader) throws JSONException,
			IOException
			{
		// Search for the data array.
		boolean hasData = findArray(reader, "data");

		if (hasData)
		{
			parseDataArray(reader);
		}
	}

	
	private void parseDataArray(JsonReader reader) throws IOException {
		final TableLayout table = (TableLayout) findViewById(R.id.table);
		reader.beginArray();

		JsonToken token = reader.peek();
		while (token != JsonToken.END_ARRAY) {
			parseDataObject(reader, table);
			token = reader.peek();
		}
	}

	
	private void parseDataObject(JsonReader reader, final TableLayout table)
			throws IOException {
		if (findNextTokenType(reader, JsonToken.BEGIN_OBJECT)) {
			final View row = getLayoutInflater().inflate(R.layout.rows, null);

			reader.beginObject();
			while (reader.hasNext()) {
				parseData(reader, row);
			}

			table.post(new Runnable() {

				public void run() {
					table.addView(row);
				}
			});

			// Consume end of object so that we can just look for the start of
			// the next object on the next call.
			if (findNextTokenType(reader, JsonToken.END_OBJECT)) {
				reader.endObject();
			}
		}
	}

	
	private void parseData(JsonReader reader, View row) throws IOException 
	{
		int columnId = toRowId(reader.nextName());

		if (columnId != -1)
		{
			((TextView) row.findViewById(columnId))
					.setText(reader.nextString());
		}
		else 
		{
			consume(reader, reader.peek());
		}
	}


	private int toRowId(String name) {
		if (name.equals("local_date_time_full")) {
			return R.id.localTime;
		} else if (name.equals("apparent_t")) {
			return R.id.apprentTemp;
		} else if (name.equals("wind_spd_kmh")) {
			return R.id.windSpeed;
		}
		return -1;
	}

	private boolean findArray(JsonReader reader, String objectName)
			throws IOException {
		while (findNextTokenType(reader, JsonToken.NAME)) {

			String name = reader.nextName();
			if (name.equals(objectName)) {
				JsonToken token = reader.peek();
				if (token == JsonToken.BEGIN_ARRAY) {
					return true;
				}
			}
		}

		return false;
	}


	private boolean findNextTokenType(JsonReader reader, JsonToken type)
			throws IOException {

		JsonToken token = reader.peek();
		while (token != JsonToken.END_DOCUMENT) {
			if (token == type) {
				return true;
			}

			consume(reader, token);
			token = reader.peek();
		}

		return false;
	}

	/**
	 * Consumes tokens from the reader.
	 * 
	 * @param reader
	 *            the instance of the reader
	 * @param type
	 *            the type of token to expect
	 * @throws IOException
	 */
	private void consume(JsonReader reader, JsonToken type) throws IOException {
		switch (type) {
		case BEGIN_ARRAY:
			reader.beginArray();
			break;
		case BEGIN_OBJECT:
			reader.beginObject();
			break;
		case END_ARRAY:
			reader.endArray();
			break;
		case END_OBJECT:
			reader.endObject();
			break;
		default:
			reader.skipValue();
		}
	}
}