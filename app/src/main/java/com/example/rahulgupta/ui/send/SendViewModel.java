package com.example.rahulgupta.ui.send;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SendViewModel extends ViewModel {

    static MutableLiveData<String> mText;

    public LiveData<String> getText() {
        if(mText==null){
            mText = new MutableLiveData<>();
            mText.setValue("Set your Text Here!");
        }
        return mText;
    }
}