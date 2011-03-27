package wei.mark.tabletennisratingsserver.util;

import java.util.ArrayList;

import wei.mark.tabletennisratingsserver.model.PlayerModel;

public interface ProviderParser {
	public static final int MAX_CACHE = 100;
	
	public ArrayList<PlayerModel> playerNameSearch(String query);
}
