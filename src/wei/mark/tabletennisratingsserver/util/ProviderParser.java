package wei.mark.tabletennisratingsserver.util;

import java.net.URLEncoder;
import java.util.ArrayList;

import wei.mark.tabletennisratingsserver.model.EventModel;
import wei.mark.tabletennisratingsserver.model.PlayerModel;

public interface ProviderParser {
	public ArrayList<PlayerModel> playerNameSearch(String query, boolean fresh);

	public ArrayList<EventModel> getPlayerDetails(String id, boolean fresh,
			String deviceId);

	public class ParserUtils {
		public static String getFirstName(String fullName) {
			int commaIndex = fullName.indexOf(",");
			if (commaIndex != -1)
				return fullName.substring(commaIndex + 1).trim();
			else
				return null;
		}

		public static String getLastName(String fullName) {
			int commaIndex = fullName.indexOf(",");
			if (commaIndex != -1)
				return fullName.substring(0, commaIndex).trim();
			else
				return fullName.trim();
		}

		public static String sanitizeName(String name) {
			return name.trim();
		}

		public static String getSearchUrl(String provider, String query) {
			try {
				if ("usatt".equals(provider)) {
					return String
							.format("http://www.usatt.org/history/rating/history/Allplayers.asp?NSearch=%s",
									URLEncoder.encode(getLastName(query),
											"UTF-8"));
				} else if ("rc".equals(provider)) {
					return String
							.format("http://www.ratingscentral.com/PlayerList.php?SortOrder=Name&PlayerName=%s",
									URLEncoder.encode(query, "UTF-8"));
				}
			} catch (Exception ex) {
			}
			return null;
		}

		public static String getDetailsUrl(String provider, String id) {
			try {
				if ("usatt".equals(provider)) {
					return String
							.format("http://www.usatt.org/history/rating/history/Phistory.asp?Pid=%s",
									URLEncoder.encode(id, "UTF-8"));
				} else if ("rc".equals(provider)) {
					return String
							.format("http://www.ratingscentral.com/PlayerHistory.php?PlayerID=%s",
									URLEncoder.encode(id, "UTF-8"));
				}
			} catch (Exception ex) {
			}
			return null;
		}

		public static ArrayList<PlayerModel> getSearchCache(String provider,
				String query, DAO dao) {
			String firstName = ParserUtils.getFirstName(query);
			String lastName = ParserUtils.getLastName(query);

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

			return null;
		}
	}
}