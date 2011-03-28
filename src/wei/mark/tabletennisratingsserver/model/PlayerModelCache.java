package wei.mark.tabletennisratingsserver.model;

import java.util.ArrayList;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class PlayerModelCache {
	@Id
	private String key;
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private ArrayList<PlayerModel> players;

	public PlayerModelCache() {
	}

	public PlayerModelCache(String provider, String query,
			ArrayList<PlayerModel> players) {
		this.key = calculateKey(provider, query);
		this.players = players;
	}

	public static String calculateKey(String provider, String query) {
		return provider + "_" + query;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public ArrayList<PlayerModel> getPlayers() {
		return players;
	}

	public void setPlayers(ArrayList<PlayerModel> players) {
		this.players = players;
	}
}
