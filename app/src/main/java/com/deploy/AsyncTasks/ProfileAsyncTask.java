package com.deploy.AsyncTasks;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import com.deploy.Manager.JsonParsing;
import com.deploy.activity.GuestActivity;
import com.deploy.activity.SignInActivity;
import com.deploy.application.CenesApplication;
import com.deploy.backendManager.UserApiManager;
import com.deploy.bo.HolidayCalendar;
import com.deploy.bo.User;
import com.deploy.coremanager.CoreManager;
import com.deploy.database.manager.UserManager;
import com.deploy.dto.AsyncTaskDto;
import com.deploy.fragment.CalenderSyncFragment;
import com.deploy.fragment.guest.ForgotPasswordSuccessFragment;
import com.google.gson.Gson;

import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class ProfileAsyncTask {

    private static CenesApplication cenesApplication;
    private static Activity activity;

    public ProfileAsyncTask(CenesApplication cenesApplication, Activity activity) {
        this.cenesApplication = cenesApplication;
        this.activity = activity;
    }

    public static class ChangePasswordTask extends AsyncTask<JSONObject, JSONObject, JSONObject> {

        private CoreManager coreManager = cenesApplication.getCoreManager();

        ProgressDialog processDialog;

        // you may separate this or combined to caller class.
        public interface AsyncResponse {
            void processFinish(JSONObject response);
        }
        public AsyncResponse delegate = null;

        public ChangePasswordTask(AsyncResponse delegate) {
            this.delegate = delegate;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            processDialog = new ProgressDialog(activity);
            processDialog.setMessage("Updating..");
            processDialog.setIndeterminate(false);
            processDialog.setCanceledOnTouchOutside(false);
            processDialog.setCancelable(false);
            processDialog.show();
        }

        @Override
        protected JSONObject doInBackground(JSONObject... objects) {

            UserApiManager userApiManager = coreManager.getUserAppiManager();
            UserManager userManager = coreManager.getUserManager();
            User user = userManager.getUser();

            JSONObject postDataObj = objects[0];
            JSONObject response = userApiManager.changePassword(postDataObj, user.getAuthToken());
            return response;
        }

        @Override
        protected void onPostExecute(JSONObject stringObjectMap) {
            super.onPostExecute(stringObjectMap);
            if (processDialog != null) {
                processDialog.dismiss();
                processDialog = null;
            }
            delegate.processFinish(stringObjectMap);
        }
    }

    public static class DownloadFacebookImage extends AsyncTask<String, String, Bitmap> {

        private CoreManager coreManager = cenesApplication.getCoreManager();

        ProgressDialog processDialog;

        // you may separate this or combined to caller class.
        public interface AsyncResponse {
            void processFinish(Bitmap response);
        }
        public AsyncResponse delegate = null;

        public DownloadFacebookImage(AsyncResponse delegate) {
            this.delegate = delegate;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            String photo = strings[0];
            URL url;
            Bitmap bm = null;
            try {
                url = new URL(photo);
                URLConnection ucon = url.openConnection();
                InputStream is;
                if (ucon instanceof HttpURLConnection) {
                    HttpURLConnection httpConn = (HttpURLConnection) ucon;
                    int statusCode = httpConn.getResponseCode();
                    if (statusCode == 200) {
                        is = httpConn.getInputStream();
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = 8;
                        BufferedInputStream bis = new BufferedInputStream(is, 8192);
                        ByteArrayBuffer baf = new ByteArrayBuffer(1024);
                        int current = 0;
                        while ((current = bis.read()) != -1) {
                            baf.append((byte) current);
                        }
                        byte[] rawImage = baf.toByteArray();
                        bm = BitmapFactory.decodeByteArray(rawImage, 0, rawImage.length);
                        bis.close();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bm;
        }

        @Override
        protected void onPostExecute(Bitmap x) {
            super.onPostExecute(x);
            delegate.processFinish(x);
        }
    }

    public static class PhoneContactSync extends AsyncTask<JSONObject,Object,Object>{

        private CoreManager coreManager = cenesApplication.getCoreManager();

        ProgressDialog syncContactsDialog;

        public interface AsyncResponse {
            void processFinish(Object response);
        }
        public AsyncResponse delegate = null;

        public PhoneContactSync(AsyncResponse delegate) {
            this.delegate = delegate;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            syncContactsDialog = new ProgressDialog(activity);
            syncContactsDialog.setMessage("Syncing Contacts..");
            syncContactsDialog.setIndeterminate(false);
            syncContactsDialog.setCanceledOnTouchOutside(false);
            syncContactsDialog.setCancelable(false);
            syncContactsDialog.show();
        }

        @Override
        protected Object doInBackground(JSONObject... objects) {
            UserApiManager userApiManager = coreManager.getUserAppiManager();
            UserManager userManager = coreManager.getUserManager();
            User user = userManager.getUser();

            JSONObject deviceTokenInfo = objects[0];

            return userApiManager.syncDevicePhone(deviceTokenInfo, user.getAuthToken());
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

            if (syncContactsDialog != null) {
                syncContactsDialog.dismiss();
            }
            syncContactsDialog = null;
            delegate.processFinish(o);
        }
    }

    public static class UploadProfilePhoto extends AsyncTask<File, JSONObject, JSONObject> {

        private CoreManager coreManager = cenesApplication.getCoreManager();

        ProgressDialog doFileUploadDialog;

        public interface AsyncResponse {
            void processFinish(JSONObject response);
        }
        public AsyncResponse delegate = null;

        public UploadProfilePhoto(AsyncResponse delegate) {
            this.delegate = delegate;
        }

        @Override
        protected void onPreExecute() {

            doFileUploadDialog = new ProgressDialog(activity);
            doFileUploadDialog.setMessage("Uploading..");
            doFileUploadDialog.setIndeterminate(false);
            doFileUploadDialog.setCanceledOnTouchOutside(false);
            doFileUploadDialog.setCancelable(false);
            doFileUploadDialog.show();
        }

        @Override
        protected JSONObject doInBackground(File... params) {

            UserApiManager userApiManager = coreManager.getUserAppiManager();
            UserManager userManager = coreManager.getUserManager();
            User user = userManager.getUser();
            File file = params[0];
            try {
                JSONObject response = userApiManager.uploadProfileImage(user, file);
                return response;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject response) {
            if (doFileUploadDialog != null) {
                doFileUploadDialog.dismiss();
            }
            doFileUploadDialog = null;
            delegate.processFinish(response);
        }
    }


    public static class SignupStepSuccessTask extends AsyncTask<JSONObject, JSONObject, JSONObject> {

        private CoreManager coreManager = cenesApplication.getCoreManager();

        ProgressDialog scanSuccessDialog;

        public interface AsyncResponse {
            void processFinish(JSONObject response);
        }
        public AsyncResponse delegate = null;

        public SignupStepSuccessTask(AsyncResponse delegate) {
            this.delegate = delegate;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            scanSuccessDialog = new ProgressDialog(activity);
            scanSuccessDialog.setMessage("Signing Up..");
            scanSuccessDialog.setIndeterminate(false);
            scanSuccessDialog.setCanceledOnTouchOutside(false);
            scanSuccessDialog.setCancelable(false);
            scanSuccessDialog.show();
        }

        @Override
        protected JSONObject doInBackground(JSONObject... jsonObjects) {

            UserManager userManager = coreManager.getUserManager();
            UserApiManager userApiManager = coreManager.getUserAppiManager();
            User user = userManager.getUser();

            JSONObject postSignupData = jsonObjects[0];
            return userApiManager.emailSignup(postSignupData);
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            if (scanSuccessDialog != null) {
                scanSuccessDialog.dismiss();
            }
            scanSuccessDialog = null;
            delegate.processFinish(jsonObject);
        }
    }

    public static class SignupStepOneSuccessTask extends AsyncTask<JSONObject, JSONObject, JSONObject> {

        private CoreManager coreManager = cenesApplication.getCoreManager();

        ProgressDialog scanSuccessDialog;

        public interface AsyncResponse {
            void processFinish(JSONObject response);
        }
        public AsyncResponse delegate = null;

        public SignupStepOneSuccessTask(AsyncResponse delegate) {
            this.delegate = delegate;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            scanSuccessDialog = new ProgressDialog(activity);
            scanSuccessDialog.setMessage("Signing Up..");
            scanSuccessDialog.setIndeterminate(false);
            scanSuccessDialog.setCanceledOnTouchOutside(false);
            scanSuccessDialog.setCancelable(false);
            scanSuccessDialog.show();
        }

        @Override
        protected JSONObject doInBackground(JSONObject... jsonObjects) {

            UserManager userManager = coreManager.getUserManager();
            UserApiManager userApiManager = coreManager.getUserAppiManager();
            User user = userManager.getUser();

            JSONObject postSignupData = jsonObjects[0];
            return userApiManager.emailSignupStep1(postSignupData);
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            if (scanSuccessDialog != null) {
                scanSuccessDialog.dismiss();
            }
            scanSuccessDialog = null;
            delegate.processFinish(jsonObject);
        }
    }

    public static class SignupStepTwoSuccessTask extends AsyncTask<JSONObject, JSONObject, JSONObject> {

        private CoreManager coreManager = cenesApplication.getCoreManager();

        ProgressDialog scanSuccessDialog;

        public interface AsyncResponse {
            void processFinish(JSONObject response);
        }
        public AsyncResponse delegate = null;

        public SignupStepTwoSuccessTask(AsyncResponse delegate) {
            this.delegate = delegate;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            scanSuccessDialog = new ProgressDialog(activity);
            scanSuccessDialog.setMessage("Updating..");
            scanSuccessDialog.setIndeterminate(false);
            scanSuccessDialog.setCanceledOnTouchOutside(false);
            scanSuccessDialog.setCancelable(false);
            scanSuccessDialog.show();
        }

        @Override
        protected JSONObject doInBackground(JSONObject... jsonObjects) {

            UserManager userManager = coreManager.getUserManager();
            UserApiManager userApiManager = coreManager.getUserAppiManager();
            User user = userManager.getUser();

            JSONObject postSignupData = jsonObjects[0];
            return userApiManager.emailSignupSignupStep2(postSignupData);
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            if (scanSuccessDialog != null) {
                scanSuccessDialog.dismiss();
            }
            scanSuccessDialog = null;
            delegate.processFinish(jsonObject);
        }
    }

    public static class SignupProfileUpdateTask extends AsyncTask<JSONObject, JSONObject, JSONObject> {

        private CoreManager coreManager = cenesApplication.getCoreManager();

        ProgressDialog scanSuccessDialog;

        public interface AsyncResponse {
            void processFinish(JSONObject response);
        }
        public AsyncResponse delegate = null;

        public SignupProfileUpdateTask(AsyncResponse delegate) {
            this.delegate = delegate;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            scanSuccessDialog = new ProgressDialog(activity);
            scanSuccessDialog.setMessage("Updating..");
            scanSuccessDialog.setIndeterminate(false);
            scanSuccessDialog.setCanceledOnTouchOutside(false);
            scanSuccessDialog.setCancelable(false);
            scanSuccessDialog.show();
        }

        @Override
        protected JSONObject doInBackground(JSONObject... jsonObjects) {

            UserManager userManager = coreManager.getUserManager();
            UserApiManager userApiManager = coreManager.getUserAppiManager();
            User user = userManager.getUser();

            JSONObject postSignupData = jsonObjects[0];
            return userApiManager.emailPostUserDetails(postSignupData, user.getAuthToken());
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            if (scanSuccessDialog != null) {
                scanSuccessDialog.dismiss();
            }
            scanSuccessDialog = null;
            delegate.processFinish(jsonObject);
        }
    }
    public static class HolidaySyncTask extends AsyncTask<HolidayCalendar, JSONObject, JSONObject> {

        private CoreManager coreManager = cenesApplication.getCoreManager();
        private  Gson gson = new Gson();
        ProgressDialog progressDialog;

        public interface AsyncResponse {
            void processFinish(JSONObject response);
        }
        public AsyncResponse delegate = null;

        public HolidaySyncTask(AsyncResponse delegate) {
            this.delegate = delegate;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(activity);
            progressDialog.setMessage("Syncing....");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected JSONObject doInBackground(HolidayCalendar... holidayCalendars) {
            HolidayCalendar holidayCalendar = holidayCalendars[0];

            UserManager userManager = coreManager.getUserManager();
            UserApiManager userApiManager = coreManager.getUserAppiManager();
            User user = userManager.getUser();

            JSONObject jsonObject =  null;
            try {
                jsonObject = new JSONObject(gson.toJson(holidayCalendar));
            } catch (Exception e) {
                e.printStackTrace();
            }

            return userApiManager.syncHolidayCalendar(jsonObject, user.getAuthToken());
        }

        @Override
        protected void onPostExecute(JSONObject response) {
            super.onPostExecute(response);
            if (progressDialog != null) {
                progressDialog.dismiss();
                progressDialog = null;
            }
            delegate.processFinish(response);
        }
    }

    public static class HolidayFetchSyncTask extends AsyncTask<String, JSONObject, JSONObject> {

        private CoreManager coreManager = cenesApplication.getCoreManager();
        private  Gson gson = new Gson();
        ProgressDialog progressDialog;

        public interface AsyncResponse {
            void processFinish(JSONObject response);
        }
        public AsyncResponse delegate = null;

        public HolidayFetchSyncTask(AsyncResponse delegate) {
            this.delegate = delegate;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(activity);
            progressDialog.setMessage("Syncing....");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... strings) {

            UserManager userManager = coreManager.getUserManager();
            UserApiManager userApiManager = coreManager.getUserAppiManager();
            User user = userManager.getUser();

            String queryStr = "userId="+user.getUserId();
            return userApiManager.holidayCalendarByUserId(queryStr, user.getAuthToken());
        }

        @Override
        protected void onPostExecute(JSONObject response) {
            super.onPostExecute(response);
            if (progressDialog != null) {
                progressDialog.dismiss();
                progressDialog = null;
            }
            delegate.processFinish(response);
        }
    }

    public static class UpdateUserTask extends AsyncTask<JSONObject, JSONObject, JSONObject> {

        private CoreManager coreManager = cenesApplication.getCoreManager();
        private  Gson gson = new Gson();
        public interface AsyncResponse {
            void processFinish(JSONObject response);
        }
        public AsyncResponse delegate = null;

        public UpdateUserTask(AsyncResponse delegate) {
            this.delegate = delegate;
        }


        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(activity);
            pDialog.setMessage("Updating...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(JSONObject... objects) {
            JSONObject userData = objects[0];
            try {

                UserManager userManager = coreManager.getUserManager();
                User user = userManager.getUser();
                UserApiManager userApiManager = coreManager.getUserAppiManager();

                JSONObject response = userApiManager.updateUserInfo(userData, user.getAuthToken());
                return response;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject response) {
            if (pDialog != null) {
                pDialog.dismiss();
                pDialog = null;
            }
            delegate.processFinish(response);
        }
    }

    public static class ForgotPasswordRequest extends AsyncTask<String,JSONObject,JSONObject> {
        ProgressDialog progressDialog;
        private CoreManager coreManager = cenesApplication.getCoreManager();

        public interface AsyncResponse {
            void processFinish(JSONObject response);
        }
        public AsyncResponse delegate = null;

        public ForgotPasswordRequest(AsyncResponse delegate) {
            this.delegate = delegate;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(activity);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Loading...");
            progressDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            String email = strings[0];

            UserApiManager userApiManager = coreManager.getUserAppiManager();


            String queryStr = "email="+email;
            JSONObject userResp = userApiManager.forgotPassword(queryStr);
            return userResp;
        }

        @Override
        protected void onPostExecute(JSONObject response) {
            super.onPostExecute(response);
            if (progressDialog != null) {
                progressDialog.dismiss();
                progressDialog = null;
            }

            delegate.processFinish(response);
        }
    }

    public static class ForgotPasswordSendEmailRequest extends AsyncTask<String,JSONObject,JSONObject> {
        ProgressDialog progressDialog;
        private CoreManager coreManager = cenesApplication.getCoreManager();

        public interface AsyncResponse {
            void processFinish(JSONObject response);
        }
        public AsyncResponse delegate = null;

        public ForgotPasswordSendEmailRequest(AsyncResponse delegate) {
            this.delegate = delegate;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(activity);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Loading...");
            progressDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            String email = strings[0];

            UserApiManager userApiManager = coreManager.getUserAppiManager();


            String queryStr = "email="+email;
            JSONObject userResp = userApiManager.sendForgetPasswordEmail(queryStr);
            return userResp;
        }

        @Override
        protected void onPostExecute(JSONObject response) {
            super.onPostExecute(response);
            if (progressDialog != null) {
                progressDialog.dismiss();
                progressDialog = null;
            }

            delegate.processFinish(response);
        }
    }

    public static class SendVerrificationTask extends AsyncTask<JSONObject, JSONObject, JSONObject> {

        private CoreManager coreManager = cenesApplication.getCoreManager();

        public interface AsyncResponse {
            void processFinish(JSONObject response);
        }
        public AsyncResponse delegate = null;

        public SendVerrificationTask(AsyncResponse delegate) {
            this.delegate = delegate;
        }

        ProgressDialog sendCodeDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            sendCodeDialog = new ProgressDialog(activity);
            sendCodeDialog.setMessage("Sending Verification Code");
            sendCodeDialog.setIndeterminate(false);
            sendCodeDialog.setCanceledOnTouchOutside(false);
            sendCodeDialog.setCancelable(false);
            sendCodeDialog.show();
        }

        @Override
        protected JSONObject doInBackground(JSONObject... jsons) {

            JSONObject postDta = jsons[0];
            UserApiManager userApiManager = coreManager.getUserAppiManager();
            return userApiManager.sentVerificationCode(postDta);
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            sendCodeDialog.dismiss();
            sendCodeDialog = null;

            delegate.processFinish(jsonObject);
        }
    }

    public static class EmailoginTask extends AsyncTask<JSONObject, JSONObject, JSONObject> {

        private CoreManager coreManager = cenesApplication.getCoreManager();

        public interface AsyncResponse {
            void processFinish(JSONObject response);
        }
        public AsyncResponse delegate = null;

        public EmailoginTask(AsyncResponse delegate) {
            this.delegate = delegate;
        }

        ProgressDialog sendCodeDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            sendCodeDialog = new ProgressDialog(activity);
            sendCodeDialog.setMessage("Loading...");
            sendCodeDialog.setIndeterminate(false);
            sendCodeDialog.setCanceledOnTouchOutside(false);
            sendCodeDialog.setCancelable(false);
            sendCodeDialog.show();
        }

        @Override
        protected JSONObject doInBackground(JSONObject... jsons) {

            JSONObject postDta = jsons[0];
            UserApiManager userApiManager = coreManager.getUserAppiManager();
            return userApiManager.emailLogin(postDta);
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            sendCodeDialog.dismiss();
            sendCodeDialog = null;

            delegate.processFinish(jsonObject);
        }
    }

    public static class DeviceTokenSyncTask extends AsyncTask<JSONObject, JSONObject, JSONObject> {

        private CoreManager coreManager = cenesApplication.getCoreManager();

        public interface AsyncResponse {
            void processFinish(JSONObject response);
        }
        public AsyncResponse delegate = null;

        public DeviceTokenSyncTask(AsyncResponse delegate) {
            this.delegate = delegate;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(JSONObject... jsons) {

            JSONObject postDta = jsons[0];
            UserApiManager userApiManager = coreManager.getUserAppiManager();
            UserManager  userManager = coreManager.getUserManager();
            User user = userManager.getUser();
            return userApiManager.syncDeviceToken(postDta, user.getAuthToken());
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            delegate.processFinish(jsonObject);
        }
    }

    public static class CheckVerificationCodeTask extends AsyncTask<JSONObject, JSONObject, JSONObject> {
        ProgressDialog verifyCodeDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            verifyCodeDialog = new ProgressDialog(activity);
            verifyCodeDialog.setMessage("Verifying Code");
            verifyCodeDialog.setIndeterminate(false);
            verifyCodeDialog.setCanceledOnTouchOutside(false);
            verifyCodeDialog.setCancelable(false);
            verifyCodeDialog.show();
        }

        private CoreManager coreManager = cenesApplication.getCoreManager();

        public interface AsyncResponse {
            void processFinish(JSONObject response);
        }
        public AsyncResponse delegate = null;

        public CheckVerificationCodeTask(AsyncResponse delegate) {
            this.delegate = delegate;
        }

        @Override
        protected JSONObject doInBackground(JSONObject... jsons) {

            JSONObject popstData = jsons[0];
            UserApiManager userApiManager = coreManager.getUserAppiManager();
            return userApiManager.checkVerificationCode(popstData);
        }

        @Override
        protected void onPostExecute(JSONObject response) {
            super.onPostExecute(response);
            verifyCodeDialog.dismiss();
            verifyCodeDialog = null;

            delegate.processFinish(response);
        }
    }

    public static class UpdatePasswordTask extends AsyncTask<JSONObject, JSONObject, JSONObject> {

        private CoreManager coreManager = cenesApplication.getCoreManager();

        ProgressDialog processDialog;

        // you may separate this or combined to caller class.
        public interface AsyncResponse {
            void processFinish(JSONObject response);
        }
        public AsyncResponse delegate = null;

        public UpdatePasswordTask(AsyncResponse delegate) {
            this.delegate = delegate;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            processDialog = new ProgressDialog(activity);
            processDialog.setMessage("Updating..");
            processDialog.setIndeterminate(false);
            processDialog.setCanceledOnTouchOutside(false);
            processDialog.setCancelable(false);
            processDialog.show();
        }

        @Override
        protected JSONObject doInBackground(JSONObject... objects) {

            UserApiManager userApiManager = coreManager.getUserAppiManager();
            UserManager userManager = coreManager.getUserManager();
            User user = userManager.getUser();

            JSONObject postDataObj = objects[0];
            JSONObject response = userApiManager.updatePassword(postDataObj, user.getAuthToken());
            return response;
        }

        @Override
        protected void onPostExecute(JSONObject stringObjectMap) {
            super.onPostExecute(stringObjectMap);
            if (processDialog != null) {
                processDialog.dismiss();
                processDialog = null;
            }
            delegate.processFinish(stringObjectMap);
        }
    }


    public static class DeleteAccountTask extends AsyncTask<JSONObject, JSONObject, JSONObject> {

        private CoreManager coreManager = cenesApplication.getCoreManager();

        ProgressDialog processDialog;

        // you may separate this or combined to caller class.
        public interface AsyncResponse {
            void processFinish(JSONObject response);
        }
        public AsyncResponse delegate = null;

        public DeleteAccountTask(AsyncResponse delegate) {
            this.delegate = delegate;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            processDialog = new ProgressDialog(activity);
            processDialog.setMessage("Deleting..");
            processDialog.setIndeterminate(false);
            processDialog.setCanceledOnTouchOutside(false);
            processDialog.setCancelable(false);
            processDialog.show();
        }

        @Override
        protected JSONObject doInBackground(JSONObject... objects) {

            UserApiManager userApiManager = coreManager.getUserAppiManager();
            UserManager userManager = coreManager.getUserManager();
            User user = userManager.getUser();

            JSONObject postDataObj = objects[0];
            JSONObject response = userApiManager.deleteAccountRequest(postDataObj, user.getAuthToken());
            return response;
        }

        @Override
        protected void onPostExecute(JSONObject stringObjectMap) {
            super.onPostExecute(stringObjectMap);
            if (processDialog != null) {
                processDialog.dismiss();
                processDialog = null;
            }
            delegate.processFinish(stringObjectMap);
        }
    }

    public static class CommonGetRequestTask extends AsyncTask<AsyncTaskDto, JSONObject, JSONObject> {

        // you may separate this or combined to caller class.
        public interface AsyncResponse {
            void processFinish(JSONObject response);
        }
        public AsyncResponse delegate = null;

        public CommonGetRequestTask(AsyncResponse delegate) {
            this.delegate = delegate;
        }

        @Override
        protected JSONObject doInBackground(AsyncTaskDto... asyncTaskDtos) {

            AsyncTaskDto asyncTaskDto = asyncTaskDtos[0];

            JsonParsing jsonParsing = new JsonParsing();
            String apiUrl = asyncTaskDto.getApiUrl();
            if (asyncTaskDto.getQueryStr() != null) {
                apiUrl = apiUrl +"?"+asyncTaskDto.getQueryStr();
            }
            return jsonParsing.httpGetJsonObject(apiUrl,null);
        }

        @Override
        protected void onPostExecute(JSONObject stringObjectMap) {
            super.onPostExecute(stringObjectMap);
            delegate.processFinish(stringObjectMap);
        }
    }

}
