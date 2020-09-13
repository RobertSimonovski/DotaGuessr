package com.example.dotaguessr;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ResultMatchHistory {
    private class Match {
        @SerializedName("match_id")
        private long matchID;

        public long getMatchID() { return matchID; }
    }

    Match[] matches;

    private int status;

    private static final Random random = new Random();

    public long[] getMatches() {
        long[] IDs = new long[matches.length];
        for(int i = 0; i<matches.length; i++) IDs[i] = matches[i].getMatchID();

        return IDs;
    }

    public long getRandomMatch(){
        return matches[random.nextInt(matches.length)].getMatchID();
    }

    public int getStatus() {
        return status;
    }
}
