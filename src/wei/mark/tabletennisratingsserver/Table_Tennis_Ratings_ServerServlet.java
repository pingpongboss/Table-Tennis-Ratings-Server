package wei.mark.tabletennisratingsserver;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.BitSet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import wei.mark.tabletennisratingsserver.model.PlayerModel;
import wei.mark.tabletennisratingsserver.util.DAO;
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
			AEAction action = AEAction.valueOf(req.getParameter("action")
					.toUpperCase());
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
				case OPEN:
					if (exists(provider, query)) {
						DAO dao = new DAO();
						boolean result = dao.addSearchHistory(provider, query,
								id);
						response = String.valueOf(result);
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
		SEARCH, OPEN
	}
}
