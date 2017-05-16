/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.quakereport;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class EarthquakeActivity extends AppCompatActivity {

    private static final String LOG_TAG = EarthquakeActivity.class.getName();
    /**
     * URL to query the USGS dataset for earthquake information
     */
    private static final String USGS_REQUEST_URL = "http://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&eventtype=earthquake&orderby=time&minmag=6&limit=10";
    /** Adapter for the list of earthquakes */
    private QuakeAdapter quakeAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);
        // Create a new adapter that takes an empty list of earthquakes as input
       quakeAdapter = new QuakeAdapter(this, new ArrayList<QuakeInfo>());//take an empty arraylist first,then execute asynctask to fill in this empty
        // Find a reference to the {@link ListView} in the layout
        ListView earthquakeListView = (ListView) findViewById(R.id.list);
        earthquakeListView.setAdapter(quakeAdapter);
        QuakeAsyncTask task = new QuakeAsyncTask();
        task.execute(USGS_REQUEST_URL);
        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                QuakeInfo currentEarthquake = quakeAdapter.getItem(position);
                Uri uri = Uri.parse(currentEarthquake.getmURL());
                Intent i = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(i);
            }
    });

    }


    /**
     * {@link AsyncTask} to perform the network request on a background thread, and then
     * update the UI with the first earthquake in the response.
     */

    private class QuakeAsyncTask extends AsyncTask<String, Void, List<QuakeInfo> >{


        @Override
        protected List<QuakeInfo> doInBackground(String... urls) {
            if (urls.length < 1 ||urls[0] == null) {
                return null;
            }
            List<QuakeInfo> quakeInfos= QueryUtils.fetchEarthquakeData(urls[0]);
            return quakeInfos;
        }

        @Override
        protected void onPostExecute(List<QuakeInfo> quakeInfos) {
            // Clear the adapter of previous earthquake data
            quakeAdapter.clear();

            // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
            // data set. This will trigger the ListView to update.
            if (quakeInfos != null && !quakeInfos.isEmpty()) {
                quakeAdapter.addAll(quakeInfos);
            }
                }

        }
    }




