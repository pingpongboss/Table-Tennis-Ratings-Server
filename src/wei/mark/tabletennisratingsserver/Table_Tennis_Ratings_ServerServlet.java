package wei.mark.tabletennisratingsserver;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.BitSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import wei.mark.tabletennisratingsserver.model.EventModel;
import wei.mark.tabletennisratingsserver.model.FriendModel;
import wei.mark.tabletennisratingsserver.model.PlayerModel;
import wei.mark.tabletennisratingsserver.util.FacebookParser;
import wei.mark.tabletennisratingsserver.util.ProviderParser;
import wei.mark.tabletennisratingsserver.util.RatingsCentralParser;
import wei.mark.tabletennisratingsserver.util.USATTParser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

@SuppressWarnings("serial")
public class Table_Tennis_Ratings_ServerServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		String response = null;
		try {
			String path = req.getPathInfo();
			if (path == null) {
				// do nothing
				return;
			}

			path = path.substring(1);
			AEAction action = AEAction.valueOf(path.toUpperCase());

			String id = req.getParameter("id");
			String provider = req.getParameter("provider");
			String query = req.getParameter("query");
			boolean fresh = Boolean.parseBoolean(req.getParameter("fresh"));

			if (verify(id)) {
				switch (action) {
				case SEARCH:
					if (exists(provider, query)) {
						ArrayList<PlayerModel> players = new ArrayList<PlayerModel>();

						ProviderParser parser = getProviderParser(provider);
						if (parser != null)
							players = parser.playerNameSearch(query, fresh);

						GsonBuilder builder = new GsonBuilder();
						builder.registerTypeAdapter(BitSet.class,
								new BitSetSerializer());
						Gson gson = builder.create();
						Type type = new TypeToken<ArrayList<PlayerModel>>() {
						}.getType();
						response = gson.toJson(players, type);
					}
					break;
				case DETAILS:
					if (exists(provider, query)) {
						ArrayList<EventModel> events = new ArrayList<EventModel>();
						ProviderParser parser = getProviderParser(provider);
						if (parser != null)
							events = parser.getPlayerDetails(query, fresh, id);

						GsonBuilder builder = new GsonBuilder();
						builder.registerTypeAdapter(BitSet.class,
								new BitSetSerializer());
						Gson gson = builder.create();
						Type type = new TypeToken<ArrayList<EventModel>>() {
						}.getType();
						response = gson.toJson(events, type);
					}
					break;
				default:
					break;
				}
			}
		} catch (Exception ex) {
			log(ex.getMessage());
		}

		resp.setContentType("text/plain");
		resp.setCharacterEncoding("UTF-8");
		resp.getWriter().println(response);
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String response = null;
		try {
			String path = req.getPathInfo();
			if (path == null) {
				// do nothing
				return;
			}

			path = path.substring(1);
			AEAction action = AEAction.valueOf(path.toUpperCase());

			String id = req.getParameter("id");
			String facebookId = req.getParameter("facebookId");
			String accessToken = req.getParameter("accessToken");
			boolean linked = Boolean.parseBoolean(req.getParameter("linked"));
			String playerId = req.getParameter("playerId");
			String editor = req.getParameter("editor");

			if (verify(id)) {
				switch (action) {
				case FRIENDS:
					if (exists(facebookId, accessToken)) {
						ArrayList<FriendModel> friends = FacebookParser
								.getFriends(facebookId, accessToken, linked);
						GsonBuilder builder = new GsonBuilder();
						builder.registerTypeAdapter(BitSet.class,
								new BitSetSerializer());
						Gson gson = builder.create();
						Type type = new TypeToken<ArrayList<FriendModel>>() {
						}.getType();
						response = gson.toJson(friends, type);
					}
					break;
				case LINK:
					if (exists(playerId, facebookId, editor)) {
						ArrayList<FriendModel> friends = FacebookParser
								.getFriends(facebookId, accessToken, linked);
						GsonBuilder builder = new GsonBuilder();
						builder.registerTypeAdapter(BitSet.class,
								new BitSetSerializer());
						Gson gson = builder.create();
						Type type = new TypeToken<ArrayList<FriendModel>>() {
						}.getType();
						response = gson.toJson(friends, type);
					}
					break;
				default:
					break;
				}
			}
		} catch (Exception ex) {
			log(ex.getMessage());
		}

		resp.setContentType("text/plain");
		resp.setCharacterEncoding("UTF-8");
		resp.getWriter().println(response);
	}

	private ProviderParser getProviderParser(String provider) {
		if ("rc".equals(provider))
			return RatingsCentralParser.getParser();
		else if ("usatt".equals(provider))
			return USATTParser.getParser();
		else
			return null;
	}

	private boolean verify(String id) {
		return exists(id);
	}

	private boolean exists(String... params) {
		boolean result = true;
		for (String param : params)
			result = result && param != null && !param.equals("");
		return result;
	}

	private class BitSetSerializer implements JsonSerializer<BitSet> {

		@Override
		public JsonElement serialize(BitSet src, Type arg1,
				JsonSerializationContext arg2) {
			return null;
		}

	}

	public enum AEAction {
		SEARCH, DETAILS, FRIENDS, LINK
	}
}
