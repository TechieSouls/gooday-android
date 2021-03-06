package com.deploy.AsyncTasks;

import android.os.AsyncTask;

import com.deploy.application.CenesApplication;
import com.deploy.backendManager.FriendApiManager;
import com.deploy.bo.User;
import com.deploy.coremanager.CoreManager;
import com.deploy.database.manager.UserManager;

import org.json.JSONObject;

public class FriendAsyncTask {

    private static CenesApplication cenesApplication;
    public FriendAsyncTask(CenesApplication cenesApplication) {
        this.cenesApplication = cenesApplication;
    }

    public static class FriendListTask extends AsyncTask<Integer, JSONObject, JSONObject> {

        // you may separate this or combined to caller class.
        public interface AsyncResponse {
            void processFinish(JSONObject response);
        }
        public AsyncResponse delegate = null;

        public FriendListTask(AsyncResponse delegate){
            this.delegate = delegate;
        }

        @Override
        protected JSONObject doInBackground(Integer... longs) {

            CoreManager coreManager = cenesApplication.getCoreManager();
            UserManager userManager = coreManager.getUserManager();
            FriendApiManager friendApiManager = coreManager.getFriendAPIManager();

            User user = userManager.getUser();
            String params = "userId="+user.getUserId();
            return friendApiManager.getFriendsList(params, user.getAuthToken());
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            delegate.processFinish(jsonObject);
        }
    }

}
