package com.example.dotaguessr;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResultGame {
    private Player[] players;
    @SerializedName("radiant_win")
    private Boolean radiantWin;
    private long duration;
    @SerializedName("match_id")
    private long matchId;
    @SerializedName("lobby_type")
    private int lobbyType;
    @SerializedName("game_mode")
    private int gameMode;
    @SerializedName("dire_score")
    private int direKills;
    @SerializedName("radiant_score")
    private int radiantKills;
    private int cluster;
    @SerializedName("tower_status_radiant")
    private int radiantTowers;
    @SerializedName("tower_status_dire")
    private int direTowers;
    @SerializedName("barracks_status_radiant")
    private int radiantBarracks;
    @SerializedName("barracks_status_dire")
    private int direBarracks;

    public long getDuration() { return duration; }
    public Player[] getPlayers() { return players; }

    public Boolean getRadiantWin() { return radiantWin; }

    public long getMatchId() { return matchId;  }

    public int getLobbyType() { return lobbyType; }

    public int getDireKills() { return direKills; }

    public int getRadiantKills() { return radiantKills; }

    public int getRadiantTowers() { return radiantTowers; }

    public int getDireTowers() { return direTowers; }

    public int getRadiantBarracks() { return radiantBarracks; }

    public int getDireBarracks() { return direBarracks;  }

    public String getGameMode() {
        switch (gameMode){
            case 1:

            case 22:
                return "All Pick";

            case 2:
                return "Captain's Mode";

            case 3:
                return "Random Draft";

            case 4:
                return "Single Draft";

            case 5:
                return "All Random";

            case 7:
                return "Diretide";

            case 8:
                return "Reverse Captain's Mode";

            case 9:
                return "Greeviling";

            case 10:
                return "Tutorial";

            case 11:
                return "Mid Only";

            case 12:
                return "Least Played";

            case 13:
                return "New Player Pool";

            case 14:
                return "Compendium Matchmaking";

            case 15:
                return "Custom";

            case 16:
                return "Captain's Draft";

            case 17:
                return "Balanced Draft";

            case 18:
                return "Ability Draft";

            case 19:
                return "Event game";

            case 20:
                return "All Random Deathmatch";

            case 21:
                return "Solo Mid 1v1";

            default:
                return "Unknown game mode";
        }
    }
    public String getRegion(){

        switch (cluster){
            case 111:

            case 112:

            case 114:
                return "US West";

            case 121:

            case 122:

            case 123:

            case 124:
                return "US East";

            case 131:

            case 132:

            case 133:

            case 134:

            case 135:

            case 136:
                return "Europe West";

            case 142:

            case 143:
                return "South Korea";

            case 151:

            case 152:

            case 153:
                return "Southeast Asia";

            case 161:

            case 163:
                return "China *DEPRECATED";

            case 171: return "Australia";

            case 181:

            case 182:

            case 183:

            case 184:
                return "Russia";

            case 191: return "Europe East";

            case 200:

            case 204:
                return "South America";

            case 211:

            case 212:

            case 213:
                return "South Africa";

            case 221:

            case 222:

            case 223:

            case 231:
                return "China";
            default:
                return "Unknown";
        }

    }

    @Override
    public String toString() {
        return "ResultGame{" +
                "radiantWin=" + radiantWin +
                ", duration=" + duration +
                ", matchId=" + matchId +
                ", lobbyType=" + lobbyType +
                ", gameMode=" + gameMode +
                ", direKills=" + direKills +
                ", radiantKills=" + radiantKills +
                ", cluster=" + cluster +
                '}';
    }
}

