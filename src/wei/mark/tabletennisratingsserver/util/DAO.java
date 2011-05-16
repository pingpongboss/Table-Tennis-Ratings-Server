package wei.mark.tabletennisratingsserver.util;

import java.util.ArrayList;
import java.util.Collection;

import wei.mark.tabletennisratingsserver.model.EventModel;
import wei.mark.tabletennisratingsserver.model.PlayerModel;
import wei.mark.tabletennisratingsserver.model.PlayerModelCache;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.DAOBase;

public class DAO extends DAOBase {
	public static final int VERSION = 2;

	static {
		ObjectifyService.register(PlayerModelCache.class);
		ObjectifyService.register(PlayerModel.class);
		ObjectifyService.register(EventModel.class);
	}

	public ArrayList<PlayerModel> getPlayersFromCache(String provider,
			String query) {
		PlayerModelCache cache = ofy().query(PlayerModelCache.class)
				.filter("provider", provider).filter("query", query).get();
		if (cache != null) {
			Collection<Key<PlayerModel>> players = cache.getPlayers();
			Collection<PlayerModel> cachedPlayers;
			if (players != null) {
				cachedPlayers = ofy().get(players).values();
				return new ArrayList<PlayerModel>(cachedPlayers);
			}

			return new ArrayList<PlayerModel>();
		}
		return null;
	}

	public void put(PlayerModelCache cache, ArrayList<PlayerModel> players) {
		for (PlayerModel player : players) {
			Key<PlayerModel> key = ofy().query(PlayerModel.class)
					.filter("provider", player.getProvider())
					.filter("id", player.getId()).getKey();
			player.setKey(key == null ? null : key.getId());
		}

		Collection<Key<PlayerModel>> keys = ofy().put(players).keySet();

		cache.setPlayers(keys);
		Key<PlayerModelCache> key = ofy().query(PlayerModelCache.class)
				.filter("provider", cache.getProvider())
				.filter("query", cache.getQuery()).getKey();
		cache.setKey(key == null ? null : key.getId());

		ofy().put(cache);
	}

	public PlayerModel getPlayer(String provider, String id) {
		return ofy().query(PlayerModel.class).filter("provider", provider)
				.filter("id", id).get();
	}

	public void put(PlayerModel player, ArrayList<EventModel> events) {
		for (EventModel event : events) {
			Key<EventModel> key = ofy().query(EventModel.class)
					.filter("provider", player.getProvider())
					.filter("playerId", event.getPlayerId())
					.filter("id", event.getId()).getKey();
			event.setKey(key == null ? null : key.getId());
		}

		Collection<Key<EventModel>> keys = ofy().put(events).keySet();

		player.setEvents(keys);

		ofy().put(player);
	}
}
