package com.example.dotaguessr;

import android.annotation.SuppressLint;
import android.app.Application;
import android.os.AsyncTask;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import com.google.gson.annotations.SerializedName;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.Random;

public class MatchHistoryViewModel extends AndroidViewModel {

    private final Object lock = new Object();

    private MatchHistory matchHistory;

    public MatchHistoryViewModel(@NonNull Application application) {
        super(application);
    }

    static class MatchHistory{
        @SerializedName("result")
        private ResultMatchHistory resultMatchHistory;


        public ResultMatchHistory getResultMatchHistory() {
            return resultMatchHistory;
        }

        public long getRandomMatch(){ return resultMatchHistory.getRandomMatch(); }
    }

    static class ResultMatchHistory {
        private Match[] matches;
        private int status;
        private static final Random random = new Random();

        private static class Match {
            @SerializedName("match_id")
            private long matchID;

            public long getMatchID() { return matchID; }
        }

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

    public boolean setMatchHistory(long playerID){
        final boolean[] success = {true};
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.steampowered.com/IDOTA2Match_570/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
        SteamWebApi steamWebApi = retrofit.create(SteamWebApi.class);

        Call<MatchHistory> call = steamWebApi.GetMatchHistory(getApplication().getString(R.string.SteamWebAPIKey), playerID);

        call.enqueue(new Callback<MatchHistory>() {
            @Override
            public void onResponse(Call<MatchHistory> call, Response<MatchHistory> response) {
                if(!response.isSuccessful()){
                    success[0] = false;
                    return;
                }
                synchronized (lock){
                    matchHistory = response.body();
                    lock.notifyAll();
                }
                assert matchHistory != null;
                if(matchHistory.getResultMatchHistory().getStatus() != 1){
                    success[0] = false;
                }
            }

            @Override
            public void onFailure(Call<MatchHistory> call, Throwable t) { success[0] = false; }
        });

        return success[0];
    }

    public long getRandomMatch(){
        return matchHistory.getRandomMatch();
    }
    public MatchHistory getMatchHistory(){ return matchHistory; }
    public Object getLock(){ return lock; }
}