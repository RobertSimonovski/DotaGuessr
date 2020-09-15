package com.example.dotaguessr;

import android.app.Application;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class GameDataProviderFactory implements ViewModelProvider.Factory {
    private Application mApplication;
    private long mParam;


    public GameDataProviderFactory(Application application, long param) {
        mApplication = application;
        mParam = param;
    }


    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new GameDataProvider(mApplication, mParam);
    }
}
