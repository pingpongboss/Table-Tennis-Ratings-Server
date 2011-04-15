package wei.mark.tabletennisratingsserver.model;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Id;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cached;

@Cached
public class PlayerModelCache {
	@Id
	Long key;

	String provider;
	String query;
	Collection<Key<PlayerModel>> players;

	// used by Objectify
	@SuppressWarnings("unused")
	private PlayerModelCache() {
	}

	public PlayerModelCache(String provider, String query) {
		this(provider, query, new ArrayList<Key<PlayerModel>>());
	}

	public PlayerModelCache(String provider, String query,
			Collection<Key<PlayerModel>> players) {
		this.provider = provider;
		this.query = query;
		this.players = players;
	}

	public void addPlayer(Long id) {
		this.players.add(new Key<PlayerModel>(PlayerModel.class, id));
	}

	public Long getKey() {
		return key;
	}

	public void setKey(Long key) {
		this.key = key;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public Collection<Key<PlayerModel>> getPlayers() {
		return players;
	}

	public void setPlayers(Collection<Key<PlayerModel>> players) {
		this.players = players;
	}
}
