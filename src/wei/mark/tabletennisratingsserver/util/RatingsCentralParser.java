package wei.mark.tabletennisratingsserver.util;

import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import wei.mark.tabletennisratingsserver.model.PlayerModel;
import wei.mark.tabletennisratingsserver.model.PlayerModelCache;

public class RatingsCentralParser implements ProviderParser {
	private static RatingsCentralParser mParser;
	private static final String provider = "rc";

	private RatingsCentralParser() {
	}

	public static synchronized RatingsCentralParser getParser() {
		if (mParser == null)
			mParser = new RatingsCentralParser();
		return mParser;
	}

	@Override
	public ArrayList<PlayerModel> playerNameSearch(String query, boolean fresh) {
		ArrayList<PlayerModel> players;
		PlayerModelCache cache;

		String firstName = Utils.getFirstName(query);
		String lastName = Utils.getLastName(query);

		DAO dao = new DAO();

		try {
			if (!fresh) {
				// first check cache
				ArrayList<PlayerModel> cachedPlayers = dao.getPlayersFromCache(
						provider, query);
				if (cachedPlayers != null)
					return cachedPlayers;
			}

			URL url = new URL(
					"http://www.ratingscentral.com/PlayerList.php?SortOrder=Name&PlayerName="
							+ URLEncoder.encode(query, "UTF-8"));

			Document doc = Jsoup.connect(url.toString()).get();
			Elements rows = doc.select("td[class=ContentSection] tbody > tr");

			players = new ArrayList<PlayerModel>();
			cache = new PlayerModelCache(provider, query);

			Date now = new Date();

			for (int i = 0; i < rows.size(); i++) {
				Elements row = rows.get(i).children();

				if (row.size() <= 1)
					continue;

				String playerName = row.get(1).text().trim();
				// match last name && first name
				if (lastName.equalsIgnoreCase(Utils.getLastName(playerName))
						&& (firstName.equals("") || firstName
								.equalsIgnoreCase(Utils
										.getFirstName(playerName)))) {
					PlayerModel player = new PlayerModel();

					player.setProvider(provider);
					player.setRating(row.get(0).text().trim());
					player.setName(playerName);
					player.setId(row.get(2).text().trim());
					Elements clubElements = row.get(3).children();
					ArrayList<String> clubs = new ArrayList<String>();
					for (Element clubElement : clubElements) {
						String club = clubElement.text().trim();
						if (club != null && !club.equals(""))
							clubs.add(club);
					}
					player.setClubs(clubs.toArray(new String[0]));
					player.setState(row.get(4).text().trim());
					player.setCountry(row.get(5).text().trim());
					player.setLastPlayed(row.get(6).text().trim());
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
