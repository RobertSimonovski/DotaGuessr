package com.example.dotaguessr;

import java.util.Random;

public class MatchHistoryRepository {
    private long[] matchIDs;
    private long playerID;
    private static final Random random = new Random();

    private MatchHistoryRepository() {
        matchIDs = null;
        playerID = -1;
    }

    private static class MatchHistoryRepositoryHolder{
        private static final MatchHistoryRepository INSTANCE = new MatchHistoryRepository();
    }

    public static MatchHistoryRepository getInstance(){
        return MatchHistoryRepositoryHolder.INSTANCE;
    }

    public void setMatchIDs(long[] matchIDs, long playerID){
        this.matchIDs = matchIDs;
        this.playerID = playerID;
    }
    public long getRandomID() { return matchIDs[random.nextInt(matchIDs.length)]; }
    public long getPlayerID() { return playerID; }
}
