package wei.mark.tabletennisratingsserver.util;

import java.net.URLEncoder;
import java.util.ArrayList;

import wei.mark.tabletennisratingsserver.model.EventModel;
import wei.mark.tabletennisratingsserver.model.PlayerModel;

public interface ProviderParser {
	public static final long freshThreshold = 1*24*60*60*1000;
	
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
		
		public static boolean firstNamePartsEqual(String firstNameQuery, String firstName) {
			String[] querySplit = firstNameQuery.split(" ");
			String[] firstNameSplit = firstName.split(" ", querySplit.length + 1);
			
			if (querySplit.length > firstNameSplit.length) return false;
			
			for (int i = 0; i < querySplit.length; i++) {
				if (!querySplit[i].equals(firstNameSplit[i])) return false;
			}
			return true;
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

		public static String getEventDetailsUrl(String provider, String playerId, String eventId) {
			try {
				if ("usatt".equals(provider)) {
					return String
							.format("http://www.usatt.org/history/rating/history/TResult.asp?Pid=%s&Tid=%s",
									URLEncoder.encode(playerId, "UTF-8"),
									URLEncoder.encode(eventId, "UTF-8"));
				} else if ("rc".equals(provider)) {
					return String
							.format("http://ratingscentral.com/EventDetail.php?EventID=%s#P%s",
									URLEncoder.encode(eventId, "UTF-8"),
									URLEncoder.encode(playerId, "UTF-8"));
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