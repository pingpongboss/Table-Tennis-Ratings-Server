package wei.mark.tabletennisratingsserver.util;

import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import wei.mark.tabletennisratingsserver.model.PlayerModel;

public class USATTParser implements ProviderParser {
	private static USATTParser mParser;

	private Map<String, ArrayList<PlayerModel>> mCache;

	private USATTParser() {
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

	public static synchronized USATTParser getParser() {
		if (mParser == null)
			mParser = new USATTParser();
		return mParser;
	}

	@Override
	public ArrayList<PlayerModel> playerNameSearch(String query) {
		return playerNameSearch(query, false);
	}

	public ArrayList<PlayerModel> playerNameSearch(String lastName,
			boolean fresh) {
		ArrayList<PlayerModel> players;

		if (!fresh) {
			// first check cache
			players = mCache.get(lastName);
			if (players != null)
				return players;
		}

		try {
			URL url = new URL(
					"http://www.usatt.org/history/rating/history/Allplayers.asp?NSearch="
							+ URLEncoder.encode(lastName, "UTF-8"));

			Document doc = Jsoup.connect(url.toString()).get();

			Elements rows = doc.select("tr");
			players = new ArrayList<PlayerModel>();

			// 0th child is headers
			for (int i = 1; i < rows.size(); i++) {
				Elements row = rows.get(i).children();

				PlayerModel player = new PlayerModel();

				player.mProvider = "usatt";
				player.mId = row.get(0).text().trim();
				player.mExpires = row.get(1).text().trim();
				player.mName = row.get(2).text().trim();
				player.mRating = row.get(3).text().trim();
				player.mState = row.get(4).text().trim();
				player.mLastPlayed = row.get(5).text().trim();
				players.add(player);

			}

			mCache.put(lastName, players);
			return players;
		} catch (Exception ex) {
			return mCache.get(lastName);
		}
	}
}
