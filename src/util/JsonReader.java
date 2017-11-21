package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.neo4j.cypher.internal.compiler.v1_9.commands.Not;

public class JsonReader {

	private static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}

	public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
		InputStream is = new URL(url).openStream();
		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			String jsonText = readAll(rd);
			System.out.println(jsonText);
			JSONObject json = new JSONObject(jsonText);
			return json;
		} finally {
			is.close();
		}
	}

	public static String getPosterUrl(String link) throws IOException, JSONException {
		JSONObject json = readJsonFromUrl("http://theapache64.xyz:8080/movie_db/search?keyword="+link);
		String posterUrl = json.get("data").toString().split("poster_url\":\"")[1];
		posterUrl = posterUrl.split("\"")[0];
		return posterUrl;
	}


	public static String getBookImage(String imdbId) throws IOException	{

		try {

			String link = "http://covers.openlibrary.org/b/isbn/"+imdbId+"-L.jpg?default=false";
			System.out.println(link);

			Response resultImageResponse = Jsoup.connect(link)
					.ignoreContentType(true).execute();

			return link;
		} catch (Exception e) {
			return "not found";
		}

	}

	public static void main(String[] args) throws IOException	{
		System.out.println(getBookImage("9780385533225"));
	}

}