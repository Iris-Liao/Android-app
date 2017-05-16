package com.iris.android.inews;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * Created by iris on 2017-02-03.
 */

public class NewsFeedAsyncTaskLoader extends AsyncTaskLoader<List<newsInfo>> {
    /** Tag for log messages */
    private static final String LOG_TAG = NewsFeedAsyncTaskLoader.class.getName();
    String mUrl;
    //constructor
    public NewsFeedAsyncTaskLoader(Context context, String url) {
        super(context);
        //Log.i(LOG_TAG,"test:new loader is created");
        mUrl=url;
    }
    @Override
    protected void onStartLoading() {
        //Log.i(LOG_TAG,"test:onStartLoading");
        forceLoad();//initLoader->onCreateLoader[if no such specific ID loader exist]->onStartLoading->loadInBackground-onLoaderFinished
    }

    /**
     * This is on a background thread.
     */
    @Override
    public List<newsInfo> loadInBackground() {
        //Log.i(LOG_TAG,"test:loadInBackground");
        if (mUrl == null) {
            return null;
        }
        List<newsInfo> quakeInfos = QueryUtils.fetchNewsFeed(mUrl);
        return quakeInfos;
    }
}
