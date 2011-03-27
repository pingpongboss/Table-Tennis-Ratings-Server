package wei.mark.tabletennisratingsserver;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import wei.mark.tabletennisratingsserver.model.PlayerModel;
import wei.mark.tabletennisratingsserver.util.ProviderParser;
import wei.mark.tabletennisratingsserver.util.RatingsCentralParser;
import wei.mark.tabletennisratingsserver.util.USATTParser;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@SuppressWarnings("serial")
public class Table_Tennis_Ratings_ServerServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		String id = req.getParameter("id");
		String provider = req.getParameter("provider");
		String query = req.getParameter("query");

		String response = null;

		if (query != null && !query.equals("") && verify(id)) {
			ArrayList<PlayerModel> players = new ArrayList<PlayerModel>();

			players = getProviderParser(provider).playerNameSearch(query);

			Gson gson = new Gson();
			Type type = new TypeToken<ArrayList<PlayerModel>>() {
			}.getType();
			response = gson.toJson(players, type);
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
		return id != null && !id.equals("");
	}
}
