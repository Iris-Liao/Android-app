package com.example.android.miwok;

/**
 * Created by iris on 2016-10-29.
 */

public class word {
    private String mMiwokWord;
    private String mDefaultTranslation;
    private int mImageResourceId;
    private int mAudioResourceId;
    private boolean hasImage = false;

    public word(String miwokWord, String defaultTranslation, int imageResourceId,int audioResourceId) {
        mMiwokWord = miwokWord;
        mDefaultTranslation = defaultTranslation;
        mImageResourceId = imageResourceId;
        mAudioResourceId=audioResourceId;
        hasImage = true;
    }//constructor for words with images

    public word(String miwokWord, String defaultTranslation,int audioResourceId) {
        mMiwokWord = miwokWord;
        mDefaultTranslation = defaultTranslation;
        mAudioResourceId=audioResourceId;
    }//constructor for words without images

    public String getmDefaultTranslation() {
        return mDefaultTranslation;
    }

    public String getmMiwokWord() {
        return mMiwokWord;
    }

    public int getImageResourceId() {
        return mImageResourceId;
    }

    public int getAudioResourceId() {return mAudioResourceId;  }

    public boolean hasImage() {
        return hasImage;
    }
}
