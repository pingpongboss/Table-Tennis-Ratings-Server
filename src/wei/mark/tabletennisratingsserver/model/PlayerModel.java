package wei.mark.tabletennisratingsserver.model;

public class PlayerModel {
	public String mProvider;
	public String mRating;
	public String mName;
	public String mId;
	public String[] mClubs;
	public String mState;
	public String mCountry;
	public String mLastPlayed;
	public String mExpires;

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

		return String.format("%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t",
				mId, mExpires, mName, mRating, clubs, mState, mCountry,
				mLastPlayed);
	}
}
