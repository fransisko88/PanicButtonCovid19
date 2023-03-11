package com.example.demo.ui.beranda;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeAdminViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public HomeAdminViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Beranda Admin");
    }

    public LiveData<String> getText() {
        return mText;
    }

}