package wei.mark.tabletennisratingsserver.util;

import java.util.ArrayList;
import java.util.Collection;

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

	public boolean addSearchHistory(String provider, String playerId,
			String deviceId) {
		PlayerModel player = ofy().query(PlayerModel.class)
				.filter("provider", provider).filter("id", playerId).get();

		if (player != null) {
			if (player.getSearchHistory() == null)
				player.setSearchHistory(new ArrayList<String>());
			if (!player.getSearchHistory().contains(deviceId))
				player.getSearchHistory().add(deviceId);
			ofy().put(player);
			return true;
		}
		return false;
	}
}
