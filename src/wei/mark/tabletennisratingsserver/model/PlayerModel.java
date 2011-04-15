package wei.mark.tabletennisratingsserver.model;

import java.util.Date;

import javax.persistence.Id;

import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Unindexed;

@Cached
public class PlayerModel {
	@Id
	Long key;

	String provider;
	String id;

	@Unindexed
	String rating;
	@Unindexed
	String name;
	@Unindexed
	String[] clubs;
	@Unindexed
	String state;
	@Unindexed
	String country;
	@Unindexed
	String lastPlayed;
	@Unindexed
	String expires;
	@Unindexed
	Date refreshed;

	public PlayerModel() {
	}

	@Override
	public String toString() {
		return String.format("%s (%s)", name, rating);
	}

	public String toDetailedString() {
		String clubsString;

		if (clubs == null)
			clubsString = null;
		else {
			StringBuilder sb = new StringBuilder();
			for (String club : clubs) {
				if (sb.length() != 0)
					sb.append(", ");
				sb.append(club);
			}
			clubsString = sb.toString();
		}

		return String.format("%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t", id, expires,
				name, rating, clubsString, state, country, lastPlayed);
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

	public String getRating() {
		return rating;
	}

	public void setRating(String rating) {
		this.rating = rating;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String[] getClubs() {
		return clubs;
	}

	public void setClubs(String[] clubs) {
		this.clubs = clubs;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getLastPlayed() {
		return lastPlayed;
	}

	public void setLastPlayed(String lastPlayed) {
		this.lastPlayed = lastPlayed;
	}

	public String getExpires() {
		return expires;
	}

	public void setExpires(String expires) {
		this.expires = expires;
	}

	public Date getRefreshed() {
		return refreshed;
	}

	public void setRefreshed(Date refreshed) {
		this.refreshed = refreshed;
	}
}
