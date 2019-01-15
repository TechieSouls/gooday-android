package com.deploy.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.deploy.R;
import com.deploy.activity.MeTimeActivity;
import com.deploy.bo.MeTime;
import com.deploy.util.CenesUtils;
import com.deploy.util.RoundedImageView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mandeep on 12/1/19.
 */

public class MeTimeService {

    public Map<String,Integer> dayIndexMap() {
        Map<String,Integer> daysIndexMap = new HashMap<>();
        daysIndexMap.put("Sunday",1);
        daysIndexMap.put("Monday",2);
        daysIndexMap.put("Tuesday",3);
        daysIndexMap.put("Wednesday",4);
        daysIndexMap.put("Thursday",5);
        daysIndexMap.put("Friday",6);
        daysIndexMap.put("Saturday",7);
        return daysIndexMap;
    }

    public Map<Integer,String> IndexDayMap() {
        Map<Integer,String> daysIndexMap = new HashMap<>();
        daysIndexMap.put(1,"Sunday");
        daysIndexMap.put(2,"Monday");
        daysIndexMap.put(3,"Tuesday");
        daysIndexMap.put(4, "Wednesday");
        daysIndexMap.put(5, "Thursday");
        daysIndexMap.put(6, "Friday");
        daysIndexMap.put(7, "Saturday");
        return daysIndexMap;
    }

    public JSONObject populateMetimeData(Map.Entry<String, JSONObject> meTimeEntryMap, String day) {

        JSONObject jsonEventObject = new JSONObject();
        try {
            Calendar dayOfWeekCalendar = Calendar.getInstance();
            dayOfWeekCalendar.setTimeInMillis(meTimeEntryMap.getValue().getLong("start_time"));
            dayOfWeekCalendar.set(Calendar.DAY_OF_WEEK,dayIndexMap().get(day));

            jsonEventObject.put("title", meTimeEntryMap.getKey());
            jsonEventObject.put("start_time", dayOfWeekCalendar.getTimeInMillis());
            jsonEventObject.put("end_time", meTimeEntryMap.getValue().getString("end_time"));
            jsonEventObject.put("description", meTimeEntryMap.getKey());
            jsonEventObject.put("day_of_week", day);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonEventObject;
    }

    public JSONArray getMeTimeEvents(Map<String, JSONObject> meTimeData) {

        JSONArray events = new JSONArray();
        try {
            for (Map.Entry<String, JSONObject> meTimeEntryMap : meTimeData.entrySet()) {
                if (meTimeEntryMap.getValue().getString("start_time") != null && meTimeEntryMap.getValue().getString("end_time") != null) {

                    if (meTimeEntryMap.getValue().has("Sunday") && meTimeEntryMap.getValue().getBoolean("Sunday") == true) {
                        events.put(populateMetimeData(meTimeEntryMap, "Sunday"));
                    }
                    if (meTimeEntryMap.getValue().has("Monday") && meTimeEntryMap.getValue().getBoolean("Monday") == true) {
                        events.put(populateMetimeData(meTimeEntryMap, "Monday"));
                    }
                    if (meTimeEntryMap.getValue().has("Tuesday") && meTimeEntryMap.getValue().getBoolean("Tuesday") == true) {
                        events.put(populateMetimeData(meTimeEntryMap, "Tuesday"));
                    }
                    if (meTimeEntryMap.getValue().has("Wednesday") && meTimeEntryMap.getValue().getBoolean("Wednesday") == true) {
                        events.put(populateMetimeData(meTimeEntryMap, "Wednesday"));
                    }
                    if (meTimeEntryMap.getValue().has("Thursday") && meTimeEntryMap.getValue().getBoolean("Thursday") == true) {
                        events.put(populateMetimeData(meTimeEntryMap, "Thursday"));
                    }
                    if (meTimeEntryMap.getValue().has("Friday") && meTimeEntryMap.getValue().getBoolean("Friday") == true) {
                        events.put(populateMetimeData(meTimeEntryMap, "Friday"));
                    }
                    if (meTimeEntryMap.getValue().has("Saturday") && meTimeEntryMap.getValue().getBoolean("Saturday") == true) {
                        events.put(populateMetimeData(meTimeEntryMap, "Saturday"));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return events;
    }

    public List<MeTime> getDefaultMeTimeValues(MeTimeActivity activity) {
        List<MeTime> defaultMetimeCards = new ArrayList<>();
        JSONArray defaultMeTimeArray = new JSONArray();

        MeTime metimeCard = new MeTime();
        metimeCard.setTitle("Sleep Cycle");
        defaultMetimeCards.add(metimeCard);
        JSONObject meTimeCardJSON = null;
        try {
            meTimeCardJSON = new JSONObject();
            meTimeCardJSON.put("title","Sleep Cycle");
            defaultMeTimeArray.put(meTimeCardJSON);
        } catch (Exception e) {
            e.printStackTrace();
        }


        metimeCard = new MeTime();
        metimeCard.setTitle("Excercise");
        defaultMetimeCards.add(metimeCard);
        try {
            meTimeCardJSON = new JSONObject();
            meTimeCardJSON.put("title","Sleep Cycle");
            defaultMeTimeArray.put(meTimeCardJSON);
        } catch (Exception e) {
            e.printStackTrace();
        }

        metimeCard = new MeTime();
        metimeCard.setTitle("Family Time");
        defaultMetimeCards.add(metimeCard);
        try {
            meTimeCardJSON = new JSONObject();
            meTimeCardJSON.put("title","Sleep Cycle");
            defaultMeTimeArray.put(meTimeCardJSON);
        } catch (Exception e) {
            e.printStackTrace();
        }

        SharedPreferences.Editor editor = activity.getSharedPreferences("DEFAULT_METIME", Context.MODE_PRIVATE).edit();
        editor.putString("defaultMeTimeJSON", defaultMeTimeArray.toString());
        editor.apply();

        return defaultMetimeCards;
    }

    public LinearLayout createMetimeCards(MeTimeActivity activity, MeTime meTime) {
        LinearLayout.LayoutParams metimeTileParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        metimeTileParams.setMargins(0, CenesUtils.dpToPx(30), 0, 0);

        LinearLayout metimeTile = new LinearLayout(activity);
        metimeTile.setOrientation(LinearLayout.HORIZONTAL);
        metimeTile.setLayoutParams(metimeTileParams);
        metimeTile.setPadding(CenesUtils.dpToPx(10), CenesUtils.dpToPx(10), CenesUtils.dpToPx(10), CenesUtils.dpToPx(10));
        metimeTile.setBackgroundResource(R.drawable.xml_round_rect_whitebg);

        //MeTimeImage
        RoundedImageView meTimeImage = new RoundedImageView(activity);
        LinearLayout.LayoutParams imageViewParams = new LinearLayout.LayoutParams(CenesUtils.dpToPx(50), CenesUtils.dpToPx(50));
        imageViewParams.gravity = Gravity.CENTER;
        imageViewParams.setMargins(CenesUtils.dpToPx(20),0,0,0);
        meTimeImage.setLayoutParams(imageViewParams);
        meTimeImage.setImageDrawable(activity.getResources().getDrawable(R.drawable.profile_icon));

        metimeTile.addView(meTimeImage);

        //MeTimeDetails
        LinearLayout detailsLayout = new LinearLayout(activity);
        LinearLayout.LayoutParams detailLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        detailLayoutParams.setMargins(CenesUtils.dpToPx(10), 0, 0, 0);
        detailsLayout.setLayoutParams(detailLayoutParams);
        detailsLayout.setOrientation(LinearLayout.VERTICAL);

        try {
            TextView metimeTitle = new TextView(activity);
            metimeTitle.setText(meTime.getTitle());
            metimeTitle.setTextColor(Color.parseColor("#FF4A90E2"));
            metimeTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            detailsLayout.addView(metimeTitle);
        } catch (Exception e) {
            e.printStackTrace();
        }


        if (meTime.getStartTime() != null) {
            TextView metimeDays = new TextView(activity);
            metimeDays.setText(meTime.getDays());
            metimeDays.setTextColor(activity.getResources().getColor(R.color.cenes_new_orange));
            metimeDays.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            detailsLayout.addView(metimeDays);

            Calendar startCal = Calendar.getInstance();
            startCal.setTimeInMillis(meTime.getStartTime());

            Calendar endCal = Calendar.getInstance();
            endCal.setTimeInMillis(meTime.getEndTime());

            TextView metimeHours = new TextView(activity);
            metimeHours.setText(CenesUtils.hhmmaa.format(startCal.getTime()) +"-"+CenesUtils.hhmmaa.format(endCal.getTime()));
            metimeHours.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            detailsLayout.addView(metimeHours);

        } else {
            TextView metimeDays = new TextView(activity);
            metimeDays.setText("Not Scheduled");
            metimeDays.setTextColor(activity.getResources().getColor(R.color.cenes_light_gray));
            metimeDays.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            detailsLayout.addView(metimeDays);
        }
        metimeTile.addView(detailsLayout);


        return metimeTile;
    }

}