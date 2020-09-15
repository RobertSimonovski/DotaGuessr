package com.example.dotaguessr;

import android.accounts.NetworkErrorException;
import android.annotation.SuppressLint;
import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;
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

import java.security.InvalidParameterException;
import java.util.Random;

public class MatchHistoryViewModel extends AndroidViewModel {

    private final Object lock = new Object();
    private MatchHistory matchHistory;
    private static final String TAG = "MatchHistoryViewModel";
    private long playerID;
    final byte[] success = {-1};

    public MatchHistoryViewModel(@NonNull Application application, long playerID) {
        super(application);
        this.playerID = playerID;
        setMatchHistory();
        Log.d(TAG, "MatchHistoryViewModel: constructing");
//        if (success == 1)
//            throw new NetworkErrorException();
//        else if (success == 2)
//            throw new InvalidParameterException();
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
        @SerializedName("num_results")
        private int numResults;
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

        public int getNumResults() { return numResults; }
    }

    private void setMatchHistory(){
        Log.d(TAG, "setMatchHistory: setting");
        success[0] = 0;
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.steampowered.com/IDOTA2Match_570/")
                .addConverterFactory(GsonConverterFactory.create())
//                .client(okHttpClient)
                .build();
        SteamWebApi steamWebApi = retrofit.create(SteamWebApi.class);

        Call<MatchHistory> call = steamWebApi.GetMatchHistory(getApplication().getString(R.string.SteamWebAPIKey), playerID);

        call.enqueue(new Callback<MatchHistory>() {
            @Override
            public void onResponse(Call<MatchHistory> call, Response<MatchHistory> response) {
                if(!response.isSuccessful()){
                    success[0] = -2;
                    return;
                }
                Log.d(TAG, "onResponse: locking");
                synchronized (lock){
                    matchHistory = response.body();
                    Log.d(TAG, "onResponse: set");
                    lock.notifyAll();
                }
                Log.d(TAG, "onResponse: unlocking");
                assert matchHistory != null;
                if(     matchHistory.getResultMatchHistory().getStatus() != 1 ||
                        matchHistory.getResultMatchHistory().getNumResults() == 0)
                {
                    success[0] = -3;
                }
            }

            @Override
            public void onFailure(Call<MatchHistory> call, Throwable t) { success[0] = -2; }
        });
    }

//    public void getRandomMatch(){
//        WaitMatchHistory asyncTask = new WaitMatchHistory();
//        asyncTask.delegate = this;
//        asyncTask.execute();
//
//        return processFinish();
//    }

    static class WaitMatchHistory extends AsyncTask<Void, Long, Long>{

        public AsyncResponse delegate = null;
        MatchHistoryViewModel matchHistoryViewModel;

        WaitMatchHistory(MatchHistoryViewModel matchHistoryViewModel){
            this.matchHistoryViewModel = matchHistoryViewModel;
        }
        @Override
        protected Long doInBackground(Void... voids) {
            Object lock = matchHistoryViewModel.getLock();
            try {
                synchronized (matchHistoryViewModel.getLock()) {
                    while (matchHistoryViewModel.getMatchHistory() == null) {
                        matchHistoryViewModel.getLock().wait();
                    }
                }
            } catch (InterruptedException e) { e.printStackTrace(); }

            if(matchHistoryViewModel.getSuccess() == 0)
                return matchHistoryViewModel.getMatchHistory().getRandomMatch();
            else
                return (long)matchHistoryViewModel.getSuccess();
        }

        @Override
        protected void onPostExecute(Long aLong) {
            delegate.processFinish(aLong);
        }
    }

    private byte getSuccess(){ return success[0]; }
    public MatchHistory getMatchHistory(){ return matchHistory; }
    private Object getLock(){ return lock; }
    public long getPlayerID(){ return playerID; }
}