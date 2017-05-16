package com.iris.android.inews;

/**
 * Created by iris on 2017-02-02.
 */
//define unit format of news feed
public class newsInfo {
    private String mWebTitle;
    private String mSectionName;
    private String mTime;
    private String mURL;

    public newsInfo(String webTitle, String sectionName, String time, String url) {
        mWebTitle = webTitle;
        mSectionName = sectionName;
        mTime = time;
        mURL = url;
    }

    public String getmWebTitle() {
        return mWebTitle;
    }

    public String getmSectionName() {
        return mSectionName;
    }

    public String getmTime() {
        return mTime;
    }

    public String getmURL() {
        return mURL;
    }
}
