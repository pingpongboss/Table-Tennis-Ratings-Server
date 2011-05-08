package wei.mark.tabletennisratingsserver.util;

import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import wei.mark.tabletennisratingsserver.model.PlayerModel;
import wei.mark.tabletennisratingsserver.model.PlayerModelCache;

public class USATTParser implements ProviderParser {
	private static USATTParser mParser;
	private static final String provider = "usatt";

	private USATTParser() {
	}

	public static synchronized USATTParser getParser() {
		if (mParser == null)
			mParser = new USATTParser();
		return mParser;
	}

	@Override
	public ArrayList<PlayerModel> playerNameSearch(String query, boolean fresh) {
		ArrayList<PlayerModel> players;
		PlayerModelCache cache;

		query = ParserUtils.sanitizeName(query);

		String firstName = ParserUtils.getFirstName(query);
		String lastName = ParserUtils.getLastName(query);

		DAO dao = new DAO();

		try {
			if (!fresh) {
				// first check cache
				ArrayList<PlayerModel> cachedPlayers = dao.getPlayersFromCache(
						provider, query);
				if (cachedPlayers != null)
					return cachedPlayers;
				else if (firstName != null) {
					// If the search has a first name, check cache for a last
					// name only search
					cachedPlayers = dao.getPlayersFromCache(provider, lastName);
					if (cachedPlayers != null) {
						ArrayList<PlayerModel> filteredPlayers = new ArrayList<PlayerModel>();
						for (PlayerModel player : cachedPlayers) {
							if (player.getFirstName().equals(firstName))
								filteredPlayers.add(player);
						}
						return filteredPlayers;
					}
				}
			}

			URL url = new URL(
					"http://www.usatt.org/history/rating/history/Allplayers.asp?NSearch="
							+ URLEncoder.encode(lastName, "UTF-8"));

			Document doc = Jsoup.connect(url.toString()).get();
			Elements rows = doc.select("tr");

			players = new ArrayList<PlayerModel>();
			cache = new PlayerModelCache(provider, query);

			Date now = new Date();

			// 0th child is headers
			for (int i = 1; i < rows.size(); i++) {
				Elements row = rows.get(i).children();

				String playerName = row.get(2).text().trim();
				// match last name && first name
				if (lastName.equalsIgnoreCase(ParserUtils
						.getLastName(playerName))
						&& (firstName == null || firstName
								.equalsIgnoreCase(ParserUtils
										.getFirstName(playerName)))) {
					PlayerModel player = new PlayerModel();

					player.setProvider(provider);
					player.setId(row.get(0).text().trim());
					player.setExpires(row.get(1).text().trim());
					player.setName(playerName);
					player.setRating(row.get(3).text().trim());
					player.setState(row.get(4).text().trim());
					player.setLastPlayed(row.get(5).text().trim());
					player.setRefreshed(now);

					players.add(player);
				}
			}

			// save cache mapping and update players
			dao.put(cache, players);

			return players;
		} catch (Exception ex) {
			ArrayList<PlayerModel> cachedPlayers = dao.getPlayersFromCache(
					provider, query);
			return cachedPlayers;
		}
	}
}
