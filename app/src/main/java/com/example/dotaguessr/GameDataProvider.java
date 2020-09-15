package com.example.dotaguessr;

import android.app.Application;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModel;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GameDataProvider extends AndroidViewModel {
    private final long matchID;
    private Game game;
    private final Object lock = new Object();
    private byte[] status;
    public GameDataProvider(@NonNull Application application, long matchID) {
        super(application);
        this.matchID = matchID;
        status = new byte[]{0};
        setMatchDetails();
    }

    public void setMatchDetails(){
//        if(matchID == -1){
//            Log.e("TAG", "getGameData: Invalid matchID" );
//            return;
//        }
////        matchID = 5594596529L;
//
//        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
//        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//
//        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .addInterceptor(loggingInterceptor)
//                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.steampowered.com/IDOTA2Match_570/")
                .addConverterFactory(GsonConverterFactory.create())
                //.client(okHttpClient)
                .build();
        SteamWebApi steamWebApi = retrofit.create(SteamWebApi.class);

        Call<Game> call = steamWebApi.GetMatchDetails(getApplication().getBaseContext().getString(R.string.SteamWebAPIKey), matchID, 1);
        call.enqueue(new Callback<Game>() {
            @Override
            public void onResponse(Call<Game> call, Response<Game> response) {
                if(!response.isSuccessful()){
                    status[0] = -1;
                    return;
                }
                synchronized (lock) {
                    game = response.body();
                    lock.notifyAll();
                }
            }

            @Override
            public void onFailure(Call<Game> call, Throwable t) {
                status[0] = -1;
            }
        });
    }

    static class GetGameData extends AsyncTask<Void, Void, Game>{
        GameDataProvider gameDataProvider;

        GetGameData(GameDataProvider gameDataProvider) { this.gameDataProvider = gameDataProvider; }
        public AsyncResponseGame delegate = null;

        @Override
        protected Game doInBackground(Void... voids) {
            synchronized (gameDataProvider.lock){
                while(gameDataProvider.game == null && gameDataProvider.status[0] == 0) {
                    try {
                        gameDataProvider.lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            return gameDataProvider.game;
        }

        @Override
        protected void onPostExecute(Game game) {
            delegate.processFinish(game);
        }
    }
}


