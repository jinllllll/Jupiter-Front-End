package external;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TicketMasterClient {
	private static final String HOST = "https://app.ticketmaster.com";
	private static final String PATH = "/discovery/v2/events.json";
	private static final String DEFAULT_KEYWORD = "event";
	private static final int DEFAULT_RADIUS = 50;
	private static final String API_KEY = "	0jKVGFxEVrX6erBY5zkxmKPjhiWsovyk";
	
	public JSONArray search(double lat, double lon, String keyword) {
		if (keyword == null) {
			keyword = DEFAULT_KEYWORD;
		}
		try {
			keyword = URLEncoder.encode(keyword, "UTF-8"); // "Rick Sun" => "Rick%20Sun"
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		String query = String.format("apikey=%s&latlong=%s,%s&keyword=%s&radius=%s", API_KEY, lat, lon, keyword, DEFAULT_RADIUS);
		String url = HOST + PATH + "?" + query;
		StringBuilder responseBuilder = new StringBuilder();
		try {
			// Create a URLConnection instance that represents a connection to the remote
			// object referred to by the URL. The HttpUrlConnection class allows us to
			// perform basic HTTP requests without the use of any additional libraries.
			HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
			connection.setRequestMethod("GET");
			connection.connect();

			// Get the status code from an HTTP response message. To execute the request we
			// can use the getResponseCode(), connect(), getInputStream() or
			// getOutputStream() methods.
			int responseCode = connection.getResponseCode();
			System.out.println("Sending requets to url: " + url);
			System.out.println("Response code: " + responseCode);

			if (responseCode != 200) {
				return new JSONArray();
			}

			// Create a BufferedReader to help read text from a character-input stream.
			// Provide for the efficient reading of characters, arrays, and lines.
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) {
				responseBuilder.append(line);
			}
			reader.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			// Extract events array only.
			JSONObject obj = new JSONObject(responseBuilder.toString());
			if (!obj.isNull("_embedded")) {
				JSONObject embedded = obj.getJSONObject("_embedded");
				return embedded.getJSONArray("events");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return new JSONArray();
	}
	
	/**
	 * Main entry to test TicketMasterClient.
	 */
	public static void main(String[] args) {
		TicketMasterClient client = new TicketMasterClient();
		JSONArray events = client.search(37.38, -122.08, null);
		try {
		    for (int i = 0; i < events.length(); ++i) {
		       JSONObject event = events.getJSONObject(i);
		       System.out.println(event.toString(2));
		    }
		} catch (Exception e) {
	                  e.printStackTrace();
		}	
	}


}


