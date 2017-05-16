package com.example.android.quakereport;

/**
 * Created by iris on 2017-01-06.
 */

public class QuakeInfo {
    private double mMag;//note that magnitude is String type
    private String mLocation;
    private long mTime;
    private String mURL;

    //constructor
    public QuakeInfo(double mag,String location,long time,String url){
        mMag=mag;
        mLocation=location;
        mTime=time;
        mURL=url;
    }

    public double getmMag() {
        return mMag;
    }

    public String getmLocation() {
        return mLocation;
    }

    public long getmTime() {
        return mTime;
    }

    public String getmURL() {
        return mURL;
    }
}
