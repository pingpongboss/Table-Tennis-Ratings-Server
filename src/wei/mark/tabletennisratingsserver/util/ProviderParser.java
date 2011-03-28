package wei.mark.tabletennisratingsserver.util;

import java.util.ArrayList;

import wei.mark.tabletennisratingsserver.model.PlayerModel;

public interface ProviderParser {
	public ArrayList<PlayerModel> playerNameSearch(String query, boolean fresh);
}
