package wei.mark.tabletennisratingsserver.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import wei.mark.tabletennisratingsserver.model.FriendModel;

import com.google.appengine.repackaged.org.json.JSONArray;
import com.google.appengine.repackaged.org.json.JSONObject;

public class FacebookParser {
	public static final String GRAPH_PATH_BASE = "https://graph.facebook.com/";
	public static final String GRAPH_PATH_PART_SELF = "?access_token=";
	public static final String GRAPH_PATH_PART_FRIENDS = "/friends?access_token=";

	public static ArrayList<FriendModel> getFriends(String facebookId,
			String accessToken, boolean linked) {
		ArrayList<FriendModel> friendsArray = new ArrayList<FriendModel>();
		DAO dao = new DAO();

		// Add self if either not linked only or if self has already been linked
		if (!linked || dao.existsPlayer(facebookId)) {
			HttpURLConnection connection = null;
			try {
				URL url = new URL(GRAPH_PATH_BASE + facebookId
						+ GRAPH_PATH_PART_SELF + accessToken);

				connection = (HttpURLConnection) url.openConnection();

				BufferedReader rd = new BufferedReader(new InputStreamReader(
						connection.getInputStream(), "UTF-8"));

				StringBuilder sb = new StringBuilder();
				String line;
				while ((line = rd.readLine()) != null) {
					sb.append(line);
				}
				rd.close();

				JSONObject data = new JSONObject(sb.toString());

				FriendModel self = new FriendModel(facebookId,
						data.getString("name"));

				friendsArray.add(self);
			} catch (Exception e) {
				return null;
			} finally {
				if (connection != null)
					connection.disconnect();
			}
		}

		// add rest of friends
		HttpURLConnection connection = null;
		try {
			URL url = new URL(GRAPH_PATH_BASE + facebookId
					+ GRAPH_PATH_PART_FRIENDS + accessToken);

			connection = (HttpURLConnection) url.openConnection();

			BufferedReader rd = new BufferedReader(new InputStreamReader(
					connection.getInputStream(), "UTF-8"));

			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = rd.readLine()) != null) {
				sb.append(line);
			}
			rd.close();

			JSONObject data = new JSONObject(sb.toString());
			JSONArray friends = data.getJSONArray("data");
			for (int i = 0; i < friends.length(); i++) {
				JSONObject friendJson = friends.getJSONObject(i);

				FriendModel friend = new FriendModel(
						friendJson.getString("id"),
						friendJson.getString("name"));

				if (!linked || dao.existsPlayer(friend.getId())) {
					friendsArray.add(friend);
				}
			}

			return friendsArray;
		} catch (Exception e) {
			return null;
		} finally {
			if (connection != null)
				connection.disconnect();
		}
	}
}
