package com.example.android.quakereport;

import android.app.Activity;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.example.android.quakereport.R.id.magnitude;

/**
 * Created by iris on 2017-01-06.
 * adapter:give instruction about how to set itemview
 */

public class QuakeAdapter extends ArrayAdapter<QuakeInfo> {
    public QuakeAdapter(Activity context, ArrayList<QuakeInfo> quakeInfos) {
        super(context, 0, quakeInfos);//0 is original for simple_list_view_1
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.info_list, parent, false);
        }

        // Get the {@link QuakeInfo} object located at this position in the list
        QuakeInfo currentEarthquake = getItem(position);

        // Find the TextView in the info_list.xml layout with the ID version_name
        TextView mMag = (TextView) listItemView.findViewById(magnitude);
        mMag.setText(formatMagnitude(currentEarthquake.getmMag()));//mMag=decimal,formatted into string

        GradientDrawable magnitudeCircle = (GradientDrawable) mMag.getBackground();

        // Get the appropriate background color based on the current earthquake magnitude
        int magnitudeColor = getMagnitudeColor(currentEarthquake.getmMag());

        // Set the color on the magnitude circle
        magnitudeCircle.setColor(magnitudeColor);//setColor(int color)

        //split string into primary and offset location
        String currentLocation=currentEarthquake.getmLocation();
        int lengthOfString=currentEarthquake.getmLocation().length();
        int index=currentLocation.indexOf("of");
        if(index==-1){
            TextView offsetLocation = (TextView) listItemView.findViewById(R.id.offset_location);
            offsetLocation.setText(R.string.Nearthe);

            TextView primaryLocation = (TextView) listItemView.findViewById(R.id.primary_location);
            primaryLocation.setText(currentEarthquake.getmLocation());
        }else {
            TextView offsetLocation = (TextView) listItemView.findViewById(R.id.offset_location);
            offsetLocation.setText(currentEarthquake.getmLocation().substring(0,index+2));

            TextView primaryLocation = (TextView) listItemView.findViewById(R.id.primary_location);
            primaryLocation.setText(currentEarthquake.getmLocation().substring(index+2,lengthOfString));
        }
        // Find the TextView in the info_list.xml layout with the ID version_number

//format date/time presented in miliseconds originally
        Date dateObject = new Date(currentEarthquake.getmTime());

        // Find the TextView with view ID date
        TextView dateView = (TextView) listItemView.findViewById(R.id.date);
        // Format the date string (i.e. "Mar 3, 1984")
        String formattedDate = formatDate(dateObject);
        // Display the date of the current earthquake in that TextView
        dateView.setText(formattedDate);

        // Find the TextView with view ID time
        TextView timeView = (TextView) listItemView.findViewById(R.id.time);
        // Format the time string (i.e. "4:30PM")
        String formattedTime = formatTime(dateObject);
        // Display the time of the current earthquake in that TextView
        timeView.setText(formattedTime);

        // Return the list item view that is now showing the appropriate data
        return listItemView;
    }
    /**
     * Return the formatted date string (i.e. "Mar 3, 1984") from a Date object.
     */
    private String formatDate(Date dateObject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("LLL dd, yyyy");
        return dateFormat.format(dateObject);
    }

    /**
     * Return the formatted date string (i.e. "4:30 PM") from a Date object.
     */
    private String formatTime(Date dateObject) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        return timeFormat.format(dateObject);
    }

    //format the magnitude from double to a string
    private String formatMagnitude(double magnitude){
        DecimalFormat formatter=new DecimalFormat("0.0");
        return formatter.format(magnitude);
    }

    private int getMagnitudeColor(double mag){
        int magnitudeColorResourceId;
        int magnitude=(int) Math.floor(mag);
        switch (magnitude){
            case 0:
            case 1:
                magnitudeColorResourceId = R.color.magnitude1;
                break;
            case 2:
                magnitudeColorResourceId = R.color.magnitude2;
                break;
            case 3:
                magnitudeColorResourceId = R.color.magnitude3;
                break;
            case 4:
                magnitudeColorResourceId = R.color.magnitude4;
                break;
            case 5:
                magnitudeColorResourceId = R.color.magnitude5;
                break;
            case 6:
                magnitudeColorResourceId = R.color.magnitude6;
                break;
            case 7:
                magnitudeColorResourceId = R.color.magnitude7;
                break;
            case 8:
                magnitudeColorResourceId = R.color.magnitude8;
                break;
            case 9:
                magnitudeColorResourceId = R.color.magnitude9;
                break;
            default:
                magnitudeColorResourceId = R.color.magnitude10plus;
                break;
        }
        int magnitudeColor = ContextCompat.getColor(getContext(), magnitudeColorResourceId);//why use magnitudeColor?getColor():Returns a color associated with a particular resource ID

        //Starting in Marshmellow, the returned color will be styled for the specified Context 's theme.
        return magnitudeColor;
    }
}
