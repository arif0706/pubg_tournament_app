package com.example.client.ui.gallery;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.client.CustomModalSheetFragment;

public class GalleryViewModel extends ViewModel implements CustomModalSheetFragment.BottomSheetListener {

    private MutableLiveData<String> mText;

    public GalleryViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is gallery fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }

    @Override
    public void onButtonClick(String text) {
        System.out.println("arif"+text);
    }
}