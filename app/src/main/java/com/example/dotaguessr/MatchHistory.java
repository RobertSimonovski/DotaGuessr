package com.example.dotaguessr;

import com.google.gson.annotations.SerializedName;

public class MatchHistory {
    @SerializedName("result")
    private ResultMatchHistory resultMatchHistory;

    public ResultMatchHistory getResultMatchHistory() {
        return resultMatchHistory;
    }
}
