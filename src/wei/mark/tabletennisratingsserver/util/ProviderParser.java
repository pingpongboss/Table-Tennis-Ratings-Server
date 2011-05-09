package wei.mark.tabletennisratingsserver.util;

import java.net.URLEncoder;
import java.util.ArrayList;

import wei.mark.tabletennisratingsserver.model.PlayerModel;

public interface ProviderParser {
	public ArrayList<PlayerModel> playerNameSearch(String query, boolean fresh);

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
	}
}