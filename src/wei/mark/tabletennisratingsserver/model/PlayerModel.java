package wei.mark.tabletennisratingsserver.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.google.appengine.api.datastore.Key;

@Entity
public class PlayerModel {
	private String mProvider;
	private String mRating;
	private String mName;
	private String mId;
	private String[] mClubs;
	private String mState;
	private String mCountry;
	private String mLastPlayed;
	private String mExpires;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Key key;

	public PlayerModel() {
	}

	@Override
	public String toString() {
		return String.format("%s (%s)", mName, mRating);
	}

	public String toDetailedString() {
		String clubs;

		if (mClubs == null)
			clubs = null;
		else {
			StringBuilder sb = new StringBuilder();
			for (String club : mClubs) {
				if (sb.length() != 0)
					sb.append(", ");
				sb.append(club);
			}
			clubs = sb.toString();
		}

		return String.format("%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t", mId, mExpires,
				mName, mRating, clubs, mState, mCountry, mLastPlayed);
	}

	public String getProvider() {
		return mProvider;
	}

	public void setProvider(String provider) {
		this.mProvider = provider;
	}

	public String getRating() {
		return mRating;
	}

	public void setRating(String rating) {
		this.mRating = rating;
	}

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		this.mName = name;
	}

	public String getId() {
		return mId;
	}

	public void setId(String id) {
		this.mId = id;
	}

	public String[] getClubs() {
		return mClubs;
	}

	public void setClubs(String[] clubs) {
		this.mClubs = clubs;
	}

	public String getState() {
		return mState;
	}

	public void setState(String state) {
		this.mState = state;
	}

	public String getCountry() {
		return mCountry;
	}

	public void setCountry(String country) {
		this.mCountry = country;
	}

	public String getLastPlayed() {
		return mLastPlayed;
	}

	public void setLastPlayed(String lastPlayed) {
		this.mLastPlayed = lastPlayed;
	}

	public String getExpires() {
		return mExpires;
	}

	public void setExpires(String expires) {
		this.mExpires = expires;
	}

	public Key getKey() {
		return key;
	}

	public void setKey(Key key) {
		this.key = key;
	}
}
