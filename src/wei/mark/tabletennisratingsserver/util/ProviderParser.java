package wei.mark.tabletennisratingsserver.util;

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
	}
}