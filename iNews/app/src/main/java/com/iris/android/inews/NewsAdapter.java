package com.iris.android.inews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by iris on 2017-02-02.
 */

public class NewsAdapter extends ArrayAdapter<newsInfo> {
    public NewsAdapter(Context context, ArrayList<newsInfo> newsInfos) {
        super(context, 0, newsInfos);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.newsfeed_view, parent, false);
        }
        // Get the {@link QuakeInfo} object located at this position in the list
        newsInfo currentNews = getItem(position);
        // Find the TextView in the newsfeed_view.xml layout with the ID @articla_name
        TextView mWebTitle = (TextView) listItemView.findViewById(R.id.webTitle_name);
        mWebTitle.setText(currentNews.getmWebTitle());
// Find the TextView in the newsfeed_view.xml layout with the ID @section_name
        TextView mSectionName = (TextView) listItemView.findViewById(R.id.section_name);
        mSectionName.setText(currentNews.getmSectionName());
        //set date&time by String method
        TextView mDate = (TextView) listItemView.findViewById(R.id.date);
        TextView mTime = (TextView) listItemView.findViewById(R.id.time);
        String currentTime = currentNews.getmTime();
        int lengthOfString = currentNews.getmTime().length();
        int index = currentTime.indexOf("T");
        mDate.setText(currentTime.substring(0, index));
        mTime.setText(currentTime.substring(index + 1, lengthOfString - 1));
        // Return the list item view that is now showing the appropriate data
        return listItemView;
    }

}
