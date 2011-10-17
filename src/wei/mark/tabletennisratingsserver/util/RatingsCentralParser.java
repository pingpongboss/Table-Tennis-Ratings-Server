package wei.mark.tabletennisratingsserver.util;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import wei.mark.tabletennisratingsserver.model.EventModel;
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

		query = ParserUtils.sanitizeName(query);

		String firstName = ParserUtils.getFirstName(query);
		String lastName = ParserUtils.getLastName(query);

		DAO dao = new DAO();

		try {
			if (!fresh) {
				// first check cache
				ArrayList<PlayerModel> cachedPlayers = ParserUtils
						.getSearchCache(provider, query, dao);
				if (cachedPlayers != null) {
					boolean staleData = false;
					Date now = new Date();
					for (PlayerModel player : cachedPlayers) {
						if (now.getTime() - player.getRefreshed().getTime() > freshThreshold) {
							staleData = true;
							break;
						}
					}

					if (!staleData)
						return cachedPlayers;
				}
			}

			URL url = new URL(ParserUtils.getSearchUrl(provider, query));

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
				if (lastName.equalsIgnoreCase(ParserUtils
						.getLastName(playerName))
						&& (firstName == null || ParserUtils
								.firstNamePartsEqual(firstName,
										ParserUtils.getFirstName(playerName)))) {
					PlayerModel player = new PlayerModel();

					player.setProvider(provider);
					player.setRating(row.get(0).text().trim());
					player.setName(playerName);
					player.setId(row.get(2).text().trim());
					player.setPlayerId(player.getId());
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

	@Override
	public ArrayList<EventModel> getPlayerDetails(String id, boolean fresh,
			String deviceId) {
		PlayerModel player;

		if (id == null || id.equals(""))
			return null;

		DAO dao = new DAO();

		try {
			player = dao.getPlayer(provider, id);

			if (player == null)
				return null;

			if (player.getSearchHistory() == null)
				player.setSearchHistory(new ArrayList<String>());
			if (!player.getSearchHistory().contains(deviceId)) {
				player.getSearchHistory().add(deviceId);
				player.setPopularity(player.getPopularity() + 1);
			}
			dao.ofy().put(player);

			if (!fresh) {
				if (player.getEvents() != null) {
					ArrayList<EventModel> cachedEvents = new ArrayList<EventModel>(
							dao.ofy().get(player.getEvents()).values());
					if (cachedEvents.size() > 0
							&& cachedEvents.get(0).getDate() == player
									.getLastPlayed())
						return cachedEvents;
				}
			}

			URL url = new URL(ParserUtils.getDetailsUrl(provider,
					player.getProviderId()));

			Document doc = Jsoup.connect(url.toString()).get();
			Elements rows = doc.select("td[class=ContentSection] tbody > tr");

			ArrayList<EventModel> events = new ArrayList<EventModel>();

			for (int i = 0; i < rows.size(); i++) {
				Elements row = rows.get(i).children();

				if (row.size() != 5)
					continue;

				EventModel event = new EventModel();

				String idHref = row.get(1).select("a[href]").attr("href");

				event.setPlayerId(id);
				event.setProvider(provider);

				event.setDate(row.get(0).text().trim());
				event.setId(idHref.substring(idHref.indexOf('=') + 1,
						idHref.indexOf('#')));
				event.setName(row.get(1).text().trim());
				event.setRatingBefore(row.get(2).text().trim());
				event.setRatingChange(row.get(3).text().trim());
				event.setRatingAfter(row.get(4).text().trim());

				events.add(event);
			}

			dao.put(player, events);

			return events;
		} catch (Exception ex) {
			return new ArrayList<EventModel>(dao.ofy()
					.get(dao.getPlayer(provider, id).getEvents()).values());
		}
	}
}
