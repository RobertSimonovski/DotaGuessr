package com.example.dotaguessr;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

import java.util.List;

public interface SteamWebApi {

    @GET("GetMatchHistory/v1/")
    Call<MatchHistory> GetMatchHistory(@Query("key") String key, @Query("account_id") long playerID);
    @GET("GetMatchDetails/v1/")
    Call<Game> GetMatchDetails(@Query("key") String key, @Query("match_ID") long matchID, @Query("include_persona_names") int persona);
}
