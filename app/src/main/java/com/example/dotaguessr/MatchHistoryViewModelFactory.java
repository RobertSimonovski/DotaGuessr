package com.example.dotaguessr;

import android.app.Application;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class MatchHistoryViewModelFactory implements ViewModelProvider.Factory {
    private Application mApplication;
    private long mParam;


    public MatchHistoryViewModelFactory(Application application, long param) {
        mApplication = application;
        mParam = param;
    }


    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new MatchHistoryViewModel(mApplication, mParam);
    }
}