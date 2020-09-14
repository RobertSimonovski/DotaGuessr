package com.example.dotaguessr;

import android.app.Application;
import android.content.Intent;
import android.util.Log;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GameDataProvider extends AndroidViewModel {
    public GameDataProvider(@NonNull Application application) {
        super(application);
    }


}
