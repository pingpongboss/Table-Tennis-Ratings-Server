package wei.mark.tabletennisratingsserver.util;

import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import wei.mark.tabletennisratingsserver.model.PlayerModel;

public class RatingsCentralParser implements ProviderParser {
	private static RatingsCentralParser mParser;

	private Map<String, ArrayList<PlayerModel>> mCache;

	private RatingsCentralParser() {
		mCache = new LinkedHashMap<String, ArrayList<PlayerModel>>(MAX_CACHE,
				.75f, true) {
			private static final long serialVersionUID = 1L;

			@Override
			protected boolean removeEldestEntry(
					java.util.Map.Entry<String, ArrayList<PlayerModel>> eldest) {
				return size() > MAX_CACHE;
			}
		};
	}

	public static synchronized RatingsCentralParser getParser() {
		if (mParser == null)
			mParser = new RatingsCentralParser();
		return mParser;
	}

	@Override
	public ArrayList<PlayerModel> playerNameSearch(String query) {
		return lastFirstNamePlayerSearch(query, false);
	}

	public ArrayList<PlayerModel> lastFirstNamePlayerSearch(
			String lastAndFirstName, boolean fresh) {
		ArrayList<PlayerModel> players;
		if (!fresh) {
			// first check cache
			players = mCache.get(lastAndFirstName);
			if (players != null)
				return players;
		}

		try {
			URL url = new URL(
					"http://www.ratingscentral.com/PlayerList.php?SortOrder=Name&PlayerName="
							+ URLEncoder.encode(lastAndFirstName, "UTF-8"));

			Document doc = Jsoup.connect(url.toString()).get();

			Elements rows = doc.select("td[class=ContentSection] tbody > tr");
			players = new ArrayList<PlayerModel>();

			for (int i = 0; i < rows.size(); i++) {
				Elements row = rows.get(i).children();

				PlayerModel player = new PlayerModel();

				player.mProvider = "rc";
				player.mRating = row.get(0).text().trim();
				player.mName = row.get(1).text().trim();
				player.mId = row.get(2).text().trim();
				Elements clubElements = row.get(3).children();
				ArrayList<String> clubs = new ArrayList<String>();
				for (Element clubElement : clubElements) {
					String club = clubElement.text().trim();
					if (club != null && !club.equals(""))
						clubs.add(club);
				}
				player.mClubs = clubs.toArray(new String[0]);
				player.mState = row.get(4).text().trim();
				player.mCountry = row.get(5).text().trim();
				player.mLastPlayed = row.get(6).text().trim();
				players.add(player);
			}

			mCache.put(lastAndFirstName, players);
			return players;
		} catch (Exception ex) {
			return mCache.get(lastAndFirstName);
		}
	}
}
