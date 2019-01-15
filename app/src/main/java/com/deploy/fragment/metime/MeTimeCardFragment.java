package com.deploy.fragment.metime;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.deploy.AsyncTasks.MeTimeAsyncTask;
import com.deploy.R;
import com.deploy.activity.MeTimeActivity;
import com.deploy.application.CenesApplication;
import com.deploy.fragment.CenesFragment;
import com.deploy.service.MeTimeService;
import com.deploy.util.CenesUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by mandeep on 8/1/19.
 */

public class MeTimeCardFragment extends CenesFragment {

     /*
    *   Long userId;
	*   List<MeTimeEvent> events;
	*   String timezone;
    * */

    public final static String TAG = "MeTimeCardFragment";
    private final static int TIME_PICKER_INTERVAL = 5;

    private LinearLayout rlFooter;
    private Button sunday, monday, tuesday, wednesday, thursday, friday, saturday;
    private Button saveMeTime, deleteMeTime;
    private TextView startTimeText, endTimeText;
    private LinearLayout metimeStartTime, metimeEndTime;
    private EditText etMetimeTitle;

    View opacityFilter;

    JSONObject meTimeJSONObj;
    private JSONObject meTimeData = new JSONObject();
    private CenesApplication cenesApplication;
    private MeTimeService meTimeService;
    private MeTimeAsyncTask meTimeAsyncTask;
    private Long startTimeMillis;
    private Long endTimeMillis;
    JSONObject selectedDaysHolder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_metime_card, container, false);

        rlFooter = ((MeTimeActivity) getActivity()).rlFooter;
        rlFooter.setVisibility(View.GONE);

        opacityFilter = view.findViewById(R.id.opacityFilter);

        etMetimeTitle = (EditText) view.findViewById(R.id.et_metime_title);

        sunday = (Button) view.findViewById(R.id.metime_sun_text);
        monday = (Button) view.findViewById(R.id.metime_mon_text);
        tuesday = (Button) view.findViewById(R.id.metime_tue_text);
        wednesday = (Button) view.findViewById(R.id.metime_wed_text);
        thursday = (Button) view.findViewById(R.id.metime_thu_text);
        friday = (Button) view.findViewById(R.id.metime_fri_text);
        saturday = (Button) view.findViewById(R.id.metime_sat_text);


        metimeStartTime = (LinearLayout) view.findViewById(R.id.metime_start_time);
        metimeEndTime = (LinearLayout) view.findViewById(R.id.metime_end_time);

        startTimeText = (TextView) view.findViewById(R.id.startTimeText);
        endTimeText = (TextView) view.findViewById(R.id.endTimeText);

        saveMeTime = (Button) view.findViewById(R.id.btn_save_metime);
        deleteMeTime = (Button) view.findViewById(R.id.btn_delete_meTime);

        opacityFilter.setOnClickListener(onClickListener);

        sunday.setOnClickListener(onClickListener);
        monday.setOnClickListener(onClickListener);
        tuesday.setOnClickListener(onClickListener);
        wednesday.setOnClickListener(onClickListener);
        thursday.setOnClickListener(onClickListener);
        friday.setOnClickListener(onClickListener);
        saturday.setOnClickListener(onClickListener);
        saveMeTime.setOnClickListener(onClickListener);
        deleteMeTime.setOnClickListener(onClickListener);
        metimeStartTime.setOnClickListener(onClickListener);
        metimeEndTime.setOnClickListener(onClickListener);

        cenesApplication = getCenesActivity().getCenesApplication();
        meTimeService = new MeTimeService();
        meTimeAsyncTask = new MeTimeAsyncTask(cenesApplication, (MeTimeActivity)getActivity());

        meTimeJSONObj = new JSONObject();
        selectedDaysHolder = new JSONObject();

        startTimeText.setText("00:00");
        startTimeMillis = null;

        endTimeText.setText("00:00");
        endTimeMillis = null;

        Bundle meTimeFragmentBundle = getArguments();
        //If user clicked on metime card saved
        if (meTimeFragmentBundle != null && meTimeFragmentBundle.getString("meTimeCard") != null) {
            try {
                meTimeJSONObj = new JSONObject(meTimeFragmentBundle.getString("meTimeCard"));

                etMetimeTitle.setText(meTimeJSONObj.getString("title"));

                if (meTimeJSONObj.has("startTime")) {
                    startTimeMillis = meTimeJSONObj.getLong("startTime");
                    endTimeMillis = meTimeJSONObj.getLong("endTime");

                    startTimeText.setText(CenesUtils.hhmmaa.format(new Date(startTimeMillis)));
                    endTimeText.setText(CenesUtils.hhmmaa.format(new Date(endTimeMillis)));

                    JSONArray recurringPatterns = meTimeJSONObj.getJSONArray("recurringPatterns");

                    for(int i=0; i < recurringPatterns.length(); i++) {

                        JSONObject recJson = recurringPatterns.getJSONObject(i);
                        selectedDaysHolder.put(meTimeService.IndexDayMap().get(recJson.getInt("dayOfWeek")), true);
                    }

                    Iterator<String> daysItr = selectedDaysHolder.keys();
                    while (daysItr.hasNext()) {
                        String day = daysItr.next();
                        if ("Sunday".equals(day)) {
                            sunday.setBackground(getResources().getDrawable(R.drawable.round_button_red));
                            sunday.setTextColor(getResources().getColor(R.color.white));
                        } else if ("Monday".equals(day)) {
                            monday.setBackground(getResources().getDrawable(R.drawable.round_button_red));
                            monday.setTextColor(getResources().getColor(R.color.white));
                        } else if ("Tuesday".equals(day)) {
                            tuesday.setBackground(getResources().getDrawable(R.drawable.round_button_red));
                            tuesday.setTextColor(getResources().getColor(R.color.white));
                        } else if ("Wednesday".equals(day)) {
                            wednesday.setBackground(getResources().getDrawable(R.drawable.round_button_red));
                            wednesday.setTextColor(getResources().getColor(R.color.white));
                        } else if ("Thursday".equals(day)) {
                            thursday.setBackground(getResources().getDrawable(R.drawable.round_button_red));
                            thursday.setTextColor(getResources().getColor(R.color.white));
                        } else if ("Friday".equals(day)) {
                            friday.setBackground(getResources().getDrawable(R.drawable.round_button_red));
                            friday.setTextColor(getResources().getColor(R.color.white));
                        } else if ("Saturday".equals(day)) {
                            saturday.setBackground(getResources().getDrawable(R.drawable.round_button_red));
                            saturday.setTextColor(getResources().getColor(R.color.white));
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return view;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {


        @Override
        public void onClick(View v) {

            switch (v.getId()) {

                case R.id.opacityFilter:
                    getActivity().onBackPressed();
                    break;

                case R.id.metime_sun_text:
                    try {
                        if (!selectedDaysHolder.has("Sunday") || !selectedDaysHolder.getBoolean("Sunday")) {
                            sunday.setBackground(getResources().getDrawable(R.drawable.round_button_red));
                            sunday.setTextColor(getResources().getColor(R.color.white));
                            selectedDaysHolder.put("Sunday", true);
                        } else {
                            sunday.setBackground(getResources().getDrawable(R.drawable.xml_circle_trans_white_border));
                            sunday.setTextColor(getResources().getColor(R.color.white));
                            selectedDaysHolder.remove("Sunday");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.metime_mon_text:
                    try {
                        if (!selectedDaysHolder.has("Monday") || !selectedDaysHolder.getBoolean("Monday")) {
                            monday.setBackground(getResources().getDrawable(R.drawable.round_button_red));
                            monday.setTextColor(getResources().getColor(R.color.white));
                            selectedDaysHolder.put("Monday", true);
                        } else {
                            monday.setBackground(getResources().getDrawable(R.drawable.xml_circle_trans_white_border));
                            monday.setTextColor(getResources().getColor(R.color.white));
                            selectedDaysHolder.remove("Monday");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
                case R.id.metime_tue_text:
                    try {
                        if (!selectedDaysHolder.has("Tuesday") || !selectedDaysHolder.getBoolean("Tuesday")) {
                            tuesday.setBackground(getResources().getDrawable(R.drawable.round_button_red));
                            tuesday.setTextColor(getResources().getColor(R.color.white));
                            selectedDaysHolder.put("Tuesday", true);

                        } else {
                            selectedDaysHolder.put("Tuesday", false);
                            tuesday.setBackground(getResources().getDrawable(R.drawable.xml_circle_trans_white_border));
                            tuesday.setTextColor(getResources().getColor(R.color.white));
                            selectedDaysHolder.remove("Tuesday");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
                case R.id.metime_wed_text:
                    try {
                        if (!selectedDaysHolder.has("Wednesday") || !selectedDaysHolder.getBoolean("Wednesday")) {
                            wednesday.setBackground(getResources().getDrawable(R.drawable.round_button_red));
                            wednesday.setTextColor(getResources().getColor(R.color.white));
                            selectedDaysHolder.put("Wednesday", true);

                        } else {
                            wednesday.setBackground(getResources().getDrawable(R.drawable.xml_circle_trans_white_border));
                            wednesday.setTextColor(getResources().getColor(R.color.white));
                            selectedDaysHolder.remove("Wednesday");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
                case R.id.metime_thu_text:
                    try {
                        if (!selectedDaysHolder.has("Thursday") || !selectedDaysHolder.getBoolean("Thursday")) {
                            thursday.setBackground(getResources().getDrawable(R.drawable.round_button_red));
                            thursday.setTextColor(getResources().getColor(R.color.white));
                            selectedDaysHolder.put("Thursday", true);

                        } else {
                            thursday.setBackground(getResources().getDrawable(R.drawable.xml_circle_trans_white_border));
                            thursday.setTextColor(getResources().getColor(R.color.white));
                            selectedDaysHolder.remove("Thursday");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
                case R.id.metime_fri_text:
                    try {
                        if (!selectedDaysHolder.has("Friday") || !selectedDaysHolder.getBoolean("Friday")) {
                            friday.setBackground(getResources().getDrawable(R.drawable.round_button_red));
                            friday.setTextColor(getResources().getColor(R.color.white));
                            selectedDaysHolder.put("Friday", true);

                        } else {
                            friday.setBackground(getResources().getDrawable(R.drawable.xml_circle_trans_white_border));
                            friday.setTextColor(getResources().getColor(R.color.white));
                            selectedDaysHolder.remove("Friday");

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
                case R.id.metime_sat_text:
                    try {
                        if (!selectedDaysHolder.has("Saturday") || !selectedDaysHolder.getBoolean("Saturday")) {
                            saturday.setBackground(getResources().getDrawable(R.drawable.round_button_red));
                            saturday.setTextColor(getResources().getColor(R.color.white));
                            selectedDaysHolder.put("Saturday", true);
                        } else {
                            saturday.setBackground(getResources().getDrawable(R.drawable.xml_circle_trans_white_border));
                            saturday.setTextColor(getResources().getColor(R.color.white));
                            selectedDaysHolder.remove("Saturday");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
                case R.id.metime_start_time:
                    Calendar mcurrentTimeForStartTime = Calendar.getInstance();
                    int mcurrentTimeForStartTimeHour = mcurrentTimeForStartTime.get(Calendar.HOUR_OF_DAY);
                    int mcurrentTimeForStartTimeMinute = mcurrentTimeForStartTime.get(Calendar.MINUTE);

                    TimePickerDialog.OnTimeSetListener startTimePickerListener = new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            Calendar mcurrentTime = Calendar.getInstance();
                            mcurrentTime.set(Calendar.HOUR_OF_DAY, selectedHour);
                            mcurrentTime.set(Calendar.MINUTE, selectedMinute);
                            String ampm = "AM";
                            if (selectedHour >= 12) {
                                ampm = "PM";
                            }
                            String singleDigitZero = "";
                            if (mcurrentTime.get(Calendar.HOUR) < 10  && ampm.equals("AM")) {
                                singleDigitZero = "0";
                            }
                            String singleDigitMinuteZero = "";
                            if (selectedMinute < 10) {
                                singleDigitMinuteZero = "0";
                            }
                            startTimeText.setText(singleDigitZero + mcurrentTime.get(Calendar.HOUR) + ":" + singleDigitMinuteZero + selectedMinute+ampm);
                            startTimeMillis = mcurrentTime.getTimeInMillis();
                        }
                    };

                    TimePickerDialog startTimeDateTimePicker = new TimePickerDialog(getCenesActivity(), startTimePickerListener, mcurrentTimeForStartTimeHour, mcurrentTimeForStartTimeMinute, false);

                    startTimeDateTimePicker.setTitle("Select Time");
                    startTimeDateTimePicker.show();

                    break;
                case R.id.metime_end_time:
                    Calendar endCalTime = Calendar.getInstance();
                    int endTimeHour = endCalTime.get(Calendar.HOUR_OF_DAY);
                    int endTimeMinute = endCalTime.get(Calendar.MINUTE);

                    TimePickerDialog.OnTimeSetListener onTimeSetListenerEndTime = new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            Calendar mcurrentTime = Calendar.getInstance();
                            mcurrentTime.set(Calendar.HOUR_OF_DAY, selectedHour);
                            mcurrentTime.set(Calendar.MINUTE, selectedMinute);
                            String ampm = "AM";
                            if (selectedHour >= 12) {
                                ampm = "PM";
                            }
                            String singleDigitZero = "";
                            if (mcurrentTime.get(Calendar.HOUR) < 10 && ampm.equals("AM")) {
                                singleDigitZero = "0";
                            }
                            String singleDigitMinuteZero = "";
                            if (selectedMinute < 10) {
                                singleDigitMinuteZero = "0";
                            }
                            endTimeText.setText(singleDigitZero + mcurrentTime.get(Calendar.HOUR) + ":" + singleDigitMinuteZero + selectedMinute+ampm);
                            endTimeMillis = mcurrentTime.getTimeInMillis();
                        }
                    };

                    TimePickerDialog endTimeDateTimePicker;
                    endTimeDateTimePicker = new TimePickerDialog(getCenesActivity(), onTimeSetListenerEndTime, endTimeHour, endTimeMinute / TIME_PICKER_INTERVAL, false);
                    endTimeDateTimePicker.setTitle("Select Time");
                    endTimeDateTimePicker.show();
                    break;
                case R.id.btn_save_metime:

                    if (CenesUtils.isEmpty(etMetimeTitle.getText().toString())) {
                        Toast.makeText(getActivity(), "Please enter title", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (startTimeMillis == null) {
                        Toast.makeText(getActivity(), "Please select Start Time", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (endTimeMillis == null) {
                        Toast.makeText(getActivity(), "Please select End Time", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!selectedDaysHolder.keys().hasNext()) {
                        Toast.makeText(getActivity(), "Please select Days", Toast.LENGTH_SHORT).show();
                        return;
                    }


                    JSONArray meTimeEvents = new JSONArray();
                    Iterator<String> itr = selectedDaysHolder.keys();
                     while(itr.hasNext()) {
                        JSONObject meTimeEvent = new JSONObject();
                        try {
                            meTimeEvent.put("title", etMetimeTitle.getText().toString());
                            meTimeEvent.put("dayOfWeek", itr.next());
                            meTimeEvent.put("startTime", startTimeMillis);
                            meTimeEvent.put("endTime", endTimeMillis);
                            meTimeEvents.put(meTimeEvent);
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    try {
                        meTimeJSONObj.put("events", meTimeEvents);
                    } catch (Exception e){
                        e.printStackTrace();
                    }

                    new MeTimeAsyncTask.MeTimePosting(new MeTimeAsyncTask.MeTimePosting.AsyncResponse() {
                        @Override
                        public void processFinish(JSONObject response) {
                            try {
                                if(response != null) {
                                    //Moving to Previous Fragment After Delete
                                    Toast.makeText(getActivity(),"MyTime Saved",Toast.LENGTH_SHORT).show();
                                    getActivity().onBackPressed();
                                } else {
                                    ((MeTimeActivity) getActivity()).showRequestTimeoutDialog();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).execute(meTimeJSONObj);
                    break;

                case R.id.btn_delete_meTime:

                    try {
                        if (meTimeJSONObj.has("recurringEventId")) {
                            new MeTimeAsyncTask.DeleteMeTimeDataTask(new MeTimeAsyncTask.DeleteMeTimeDataTask.AsyncResponse() {
                                @Override
                                public void processFinish(JSONObject response) {

                                    //Moving to Previous Fragment After Delete
                                    getActivity().onBackPressed();
                                }
                            }).execute(meTimeJSONObj.getLong("recurringEventId"));
                        } else {

                            SharedPreferences prefs = getActivity().getSharedPreferences("DEFAULT_METIME", Context.MODE_PRIVATE);
                            if (prefs != null ) {
                                String meTimeJSONString = prefs.getString("defaultMeTimeJSON", null);
                                if (meTimeJSONString != null) {
                                    try {
                                        JSONArray defaultMetimesArr = new JSONArray(meTimeJSONString);
                                        for (int d=0; d < defaultMetimesArr.length(); d++) {
                                            JSONObject defaultMeTime = defaultMetimesArr.getJSONObject(d);
                                            if (defaultMeTime.getString("title").equals(meTimeJSONObj.getString("title"))) {
                                                defaultMetimesArr.remove(d);
                                            }
                                        }
                                        SharedPreferences.Editor editor = getActivity().getSharedPreferences("DEFAULT_METIME", Context.MODE_PRIVATE).edit();
                                        editor.putString("defaultMeTimeJSON", defaultMetimesArr.toString());
                                        editor.apply();

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            getActivity().onBackPressed();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().overridePendingTransition(R.anim.exit_to_top, R.anim.enter_from_bottom);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().overridePendingTransition(R.anim.enter_from_bottom, R.anim.exit_to_top);
    }
}