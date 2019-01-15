package com.deploy.AsyncTasks;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.deploy.activity.MeTimeActivity;
import com.deploy.application.CenesApplication;
import com.deploy.backendManager.MeTimeApiManager;
import com.deploy.bo.Location;
import com.deploy.bo.MeTimeItem;
import com.deploy.bo.User;
import com.deploy.coremanager.CoreManager;
import com.deploy.database.manager.UserManager;
import com.deploy.service.MeTimeService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mandeep on 10/1/19.
 */

public class MeTimeAsyncTask {

    private static CenesApplication cenesApplication;
    private static MeTimeActivity activity;

    public MeTimeAsyncTask (CenesApplication cenesApplication, MeTimeActivity activity) {

        this.cenesApplication = cenesApplication;
        this.activity = activity;
    }


    public static class MeTimePosting extends AsyncTask<JSONObject, JSONObject, JSONObject> {
        private MeTimeService meTimeService;
        private CoreManager coreManager = cenesApplication.getCoreManager();

        ProgressDialog processDialog;

        public interface AsyncResponse {
            void processFinish(JSONObject response);
        }
        public AsyncResponse delegate = null;

        public MeTimePosting(AsyncResponse delegate){
            this.delegate = delegate;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            meTimeService = new MeTimeService();
            processDialog = new ProgressDialog(activity);
            processDialog.setMessage("Processing..");
            processDialog.setIndeterminate(false);
            processDialog.setCanceledOnTouchOutside(false);
            processDialog.setCancelable(false);
            processDialog.show();

        }

        @Override
        protected JSONObject doInBackground(JSONObject... lists) {

            UserManager userManager = coreManager.getUserManager();
            MeTimeApiManager meTimeApiManager= coreManager.getMeTimeApiManager();

            Calendar mcurrentTime = Calendar.getInstance();
            JSONObject meTimeData = lists[0];

            try {
                User user = userManager.getUser();
                meTimeData.put("userId", user.getUserId());
                meTimeData.put("timezone", mcurrentTime.getTimeZone().getID());

                JSONObject responseObj = meTimeApiManager.saveMeTime(user, meTimeData);
                Log.e("Me Time response ", responseObj.toString());
                return responseObj;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject metimeResponse) {
            super.onPostExecute(metimeResponse);
            if (processDialog.isShowing()) {
                processDialog.dismiss();
            }
            processDialog = null;
            delegate.processFinish(metimeResponse);
        }
    }

    public static class GetMeTimeDataTask extends AsyncTask<String, JSONObject, JSONObject> {

        CoreManager coreManager = cenesApplication.getCoreManager();

        public interface AsyncResponse {
            void processFinish(JSONObject response);
        }
        public AsyncResponse delegate = null;

        public GetMeTimeDataTask(AsyncResponse delegate){
            this.delegate = delegate;
        }

        @Override
        protected JSONObject doInBackground(String... lists) {


            UserManager userManager = coreManager.getUserManager();
            MeTimeApiManager meTimeApiManager= coreManager.getMeTimeApiManager();


            User user = userManager.getUser();
            String queryStr = "userId=" + user.getUserId();
            try {
                JSONObject response = meTimeApiManager.getUserMeTimeData(queryStr, user.getAuthToken());
                return response;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject response) {
            super.onPostExecute(response);
            delegate.processFinish(response);
        }
    }

    public static class DeleteMeTimeDataTask extends AsyncTask<Long, JSONObject, JSONObject> {

        private CoreManager coreManager = cenesApplication.getCoreManager();

        public interface AsyncResponse {
            void processFinish(JSONObject response);
        }
        public AsyncResponse delegate = null;

        public DeleteMeTimeDataTask(AsyncResponse delegate){
            this.delegate = delegate;
        }

        ProgressDialog processDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            processDialog = new ProgressDialog(activity);
            processDialog.setMessage("Deleting..");
            processDialog.setIndeterminate(false);
            processDialog.setCancelable(false);
            processDialog.show();
        }

        @Override
        protected JSONObject doInBackground(Long... longs) {

            UserManager userManager = coreManager.getUserManager();
            MeTimeApiManager meTimeApiManager= coreManager.getMeTimeApiManager();


            User user = userManager.getUser();
            Long recurringEventId = longs[0];
            String queryStr = "recurringEventId=" + recurringEventId;
            try {
                JSONObject response = meTimeApiManager.deleteMeTimeData(queryStr, user.getAuthToken());
                return response;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject response) {
            super.onPostExecute(response);
            processDialog.dismiss();
            processDialog = null;

            delegate.processFinish(response);
        }
    }
}
