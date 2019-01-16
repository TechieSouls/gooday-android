package com.deploy.fragment.metime;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.deploy.AsyncTasks.MeTimeAsyncTask;
import com.deploy.R;
import com.deploy.activity.AlarmActivity;
import com.deploy.activity.DiaryActivity;
import com.deploy.activity.GatheringScreenActivity;
import com.deploy.activity.HomeScreenActivity;
import com.deploy.activity.MeTimeActivity;
import com.deploy.activity.ReminderActivity;
import com.deploy.application.CenesApplication;
import com.deploy.bo.MeTime;
import com.deploy.bo.MeTimeItem;
import com.deploy.fragment.CenesFragment;
import com.deploy.service.MeTimeService;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Mandeep on 10/10/17.
 */

public class MeTimeFragment extends CenesFragment {

    /*
    *   Long userId;
	*   List<MeTimeEvent> events;
	*   String timezone;
    * */

    public final static String TAG = "MeTimeFragment";

    ImageView footerHomeIcon, footerGatheringIcon;
    ImageView ivAddMetime;
    LinearLayout llMetimeTilesContainer;


    private FragmentManager fragmentManager;

    Map<String, MeTimeItem> meTimeItemMap;
    List<String> meTimeCategoryHeaders;
    Map<String, JSONObject> meTimeDataCategoryMap;
    Map<String, Boolean> daysSelectionMap;

    CenesApplication cenesApplication;
    private MeTimeService meTimeService;
    private MeTimeAsyncTask meTimeAsyncTask;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_metime, container, false);

        cenesApplication = getCenesActivity().getCenesApplication();
        meTimeService = new MeTimeService();
        meTimeAsyncTask = new MeTimeAsyncTask(cenesApplication, (MeTimeActivity) getActivity());

        footerHomeIcon = ((MeTimeActivity) getActivity()).footerHomeIcon;
        footerGatheringIcon = ((MeTimeActivity) getActivity()).footerGatheringIcon;
        ivAddMetime = v.findViewById(R.id.iv_add_metime);
        llMetimeTilesContainer = v.findViewById(R.id.ll_metime_tiles_container);

        footerHomeIcon.setOnClickListener(onClickListener);
        footerGatheringIcon.setOnClickListener(onClickListener);
        ivAddMetime.setOnClickListener(onClickListener);

        meTimeItemMap = new HashMap<>();

        return v;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.footer_home_icon:
                    getActivity().startActivity(new Intent((MeTimeActivity)getActivity(), HomeScreenActivity.class));
                    getActivity().finish();
                    break;

                case R.id.footer_gathering_icon:
                    getActivity().startActivity(new Intent((MeTimeActivity)getActivity(), GatheringScreenActivity.class));
                    getActivity().finish();
                    break;

                case R.id.iv_add_metime:
                    MeTimeCardFragment ll = new MeTimeCardFragment();
                    fragmentManager = getActivity().getSupportFragmentManager();
                    android.support.v4.app.FragmentTransaction ft = fragmentManager.beginTransaction();
                    ft.setCustomAnimations(R.anim.enter_from_bottom, R.anim.exit_to_top, R.anim.enter_from_top, R.anim.exit_to_bottom);
                    ft.replace(R.id.fragment_container, ll);
                    ft.addToBackStack(null).commit();
                    break;
            }
        }
    };


    @Override
    public void onResume() {
        super.onResume();
        loadMeTimes();
    }

    public void loadMeTimes() {

        JSONArray defaultMetimesArr = null;
        Boolean defaultExists = false;
        SharedPreferences prefs = getActivity().getSharedPreferences("DEFAULT_METIME", Context.MODE_PRIVATE);
        if (prefs != null ) {
            defaultExists = true;
            String meTimeJSONString = prefs.getString("defaultMeTimeJSON", null);
            if (meTimeJSONString != null) {
                try {
                    defaultMetimesArr = new JSONArray(meTimeJSONString);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        //Fetching MeTimeData
        if (defaultMetimesArr == null) {
            defaultMetimesArr = new JSONArray();
        }
        final JSONArray finalDefaultMetimesArr = defaultMetimesArr;
        new MeTimeAsyncTask.GetMeTimeDataTask(new MeTimeAsyncTask.GetMeTimeDataTask.AsyncResponse() {
            @Override
            public void processFinish(JSONObject response) {
                try {

                    if (getActivity() == null) {
                        return;
                    }

                    if (response.getBoolean("success")) {
                        meTimeCategoryHeaders = new ArrayList<>();
                        meTimeDataCategoryMap = new HashMap<>();
                        daysSelectionMap = new HashMap<>();

                        JSONArray meTimeData = response.getJSONArray("data");

                        for (int m = 0; m < meTimeData.length(); m++) {
                            JSONObject meTimeJSON = meTimeData.getJSONObject(m);
                            finalDefaultMetimesArr.put(meTimeJSON);
                        }
                        if (finalDefaultMetimesArr != null && finalDefaultMetimesArr.length() > 0) {

                            for (int i = 0; i < finalDefaultMetimesArr.length(); i++) {
                                final JSONObject meTimeJSON = finalDefaultMetimesArr.getJSONObject(i);

                                Gson gson = new Gson();
                                MeTime meTime = gson.fromJson(meTimeJSON.toString(), MeTime.class);

                                if (meTimeJSON.has("recurringPatterns")) {
                                    JSONArray recurringPatterns = meTimeJSON.getJSONArray("recurringPatterns");
                                    if (recurringPatterns.length() > 0) {
                                        String daysStr = "";
                                        for(int j=0; j < recurringPatterns.length(); j++) {
                                            JSONObject recJson = recurringPatterns.getJSONObject(j);
                                            daysStr += meTimeService.IndexDayMap().get(recJson.getInt("dayOfWeek")).substring(0,3).toUpperCase() +",";
                                        }
                                        meTime.setDays(daysStr.substring(0, daysStr.length() - 1));
                                    }
                                }

                                //MeTimeDetails
                                LinearLayout detailsLayout = meTimeService.createMetimeCards((MeTimeActivity)getActivity(), meTime);
                                detailsLayout.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Bundle bundle = new Bundle();
                                        bundle.putString("meTimeCard", meTimeJSON.toString());
                                        MeTimeCardFragment ll = new MeTimeCardFragment();
                                        ll.setArguments(bundle);
                                        fragmentManager = getActivity().getSupportFragmentManager();
                                        android.support.v4.app.FragmentTransaction ft = fragmentManager.beginTransaction();
                                        ft.setCustomAnimations(R.anim.enter_from_bottom, R.anim.exit_to_top, R.anim.enter_from_top, R.anim.exit_to_bottom);
                                        ft.replace(R.id.fragment_container, ll);
                                        ft.addToBackStack(TAG).commit();

                                    }
                                });
                                llMetimeTilesContainer.addView(detailsLayout);

                            }

                        } else {
                            //showDefaultMeData();
                        }
                    } else {
                        //showDefaultMeData();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).execute();
    }

    public void showDefaultMeData() {
        List<MeTime> defaultCards = meTimeService.getDefaultMeTimeValues((MeTimeActivity) getActivity());
        for (MeTime meTime: defaultCards) {
            //MeTimeDetails
            final JSONObject meTimeJSON = new JSONObject();
            try {
                meTimeJSON.put("title", meTime.getTitle());
            } catch (Exception e) {
                e.printStackTrace();
            }
            LinearLayout detailsLayout = meTimeService.createMetimeCards((MeTimeActivity)getActivity(), meTime);
            detailsLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("meTimeCard", meTimeJSON.toString());
                    MeTimeCardFragment ll = new MeTimeCardFragment();
                    ll.setArguments(bundle);
                    fragmentManager = getActivity().getSupportFragmentManager();
                    android.support.v4.app.FragmentTransaction ft = fragmentManager.beginTransaction();
                    ft.setCustomAnimations(R.anim.enter_from_bottom, R.anim.exit_to_top, R.anim.enter_from_top, R.anim.exit_to_bottom);
                    ft.replace(R.id.fragment_container, ll);
                    ft.addToBackStack(TAG).commit();

                }
            });
            llMetimeTilesContainer.addView(detailsLayout);
        }
    }
}
