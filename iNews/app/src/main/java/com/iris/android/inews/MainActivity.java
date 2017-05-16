package com.iris.android.inews;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<newsInfo>> {
    private static final String LOG_TAG = MainActivity.class.getName();
    private static String GUARDIAN_REQUEST_URL = "http://content.guardianapis.com/search";//?q=politics&api-key=test";
    private NewsAdapter newsAdapter;
    private ProgressBar progressBar;
    private ListView newsListView;
    SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView mEmptyStateTextView;
    private String url;
    private LoaderManager loaderManager;
    private boolean isRefresh = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_view);
        newsAdapter = new NewsAdapter(this, new ArrayList<newsInfo>());//take an empty arraylist first,then execute asynctask to fill in this empty
        // Find a reference to the {@link ListView} in the layout
        newsListView = (ListView) findViewById(R.id.list);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!isRefresh) {
                    isRefresh = true;
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            mSwipeRefreshLayout.setRefreshing(false);// hide the progress bar
                            loaderManager.initLoader(0, null, MainActivity.this).forceLoad();
                            isRefresh = false;
                        }
                    }, 3000);
                }

            }
        });
        //set empty state view
        mEmptyStateTextView = (TextView) findViewById(R.id.empty);
        newsListView.setEmptyView(mEmptyStateTextView);
        //set a progress bar
        progressBar = (ProgressBar) findViewById(R.id.loading_spinner);
        newsListView.setAdapter(newsAdapter);
        //check internet
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if (isConnected) {
            loaderManager = getLoaderManager();
            loaderManager.initLoader(0, null, MainActivity.this);
        } else {
            progressBar.setVisibility(View.GONE);
            mEmptyStateTextView.setText(R.string.no_internet);
        }
        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                newsInfo currentNews = newsAdapter.getItem(position);
                Uri uri = Uri.parse(currentNews.getmURL());
                Intent i = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(i);
            }
        });
    }

    /*check the menu setting first,and then inflate a related new loader for customized URL/uri*/
    @Override
    public Loader<List<newsInfo>> onCreateLoader(int i, Bundle bundle) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String test = "test";
        String section = sharedPrefs.getString(
                getString(R.string.settings_search_by_key),
                getString(R.string.settings_search_by_defaultValue));//look up the user’s preferred sort order when we build the URI for making the HTTP request,if there is no value for key, use default value
        Uri baseUri = Uri.parse(GUARDIAN_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendQueryParameter("q", section);
        uriBuilder.appendQueryParameter("api-key", test);
        url = uriBuilder.toString();
        return new NewsFeedAsyncTaskLoader(this, url);
    }

    @Override
    public void onLoadFinished(Loader<List<newsInfo>> loader, List<newsInfo> newsInfos) {
        //  Update the UI with the result
        //Log.i(LOG_TAG,"test:onLoadFinished");
        progressBar.setVisibility(View.GONE);
        newsAdapter.clear();

        // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (newsInfos != null && !newsInfos.isEmpty()) {
            newsAdapter.addAll(newsInfos);
        }
        mEmptyStateTextView.setText(R.string.no_news);
    }

    @Override
    public void onLoaderReset(Loader<List<newsInfo>> loader) {
        // Loader reset, so we can clear out our existing data.
        //Log.i(LOG_TAG,"test:onLoaderReset");
        newsAdapter.clear();
    }

    //inflate menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Log.i(LOG_TAG,"test:onCreateOptionMenu");
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    //when one of menu item selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Log.i(LOG_TAG,"test:onOptionsItemSelected");
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
