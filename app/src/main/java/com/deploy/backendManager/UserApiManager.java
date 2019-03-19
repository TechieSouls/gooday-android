package com.deploy.backendManager;

import android.support.v7.app.AppCompatActivity;

import com.deploy.Manager.Impl.UrlManagerImpl;
import com.deploy.Manager.JsonParsing;
import com.deploy.api.GatheringAPI;
import com.deploy.api.UserAPI;
import com.deploy.application.CenesApplication;
import com.deploy.bo.User;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;

/**
 * Created by mandeep on 23/9/18.
 */

public class UserApiManager {
    CenesApplication cenesApplication;

    public UserApiManager(CenesApplication cenesApplication){
        this.cenesApplication = cenesApplication;
    }

    public JSONObject sentVerificationCode(JSONObject postData) {
        JsonParsing jsonParsing = new JsonParsing();
        return jsonParsing.httpPost(UrlManagerImpl.prodAPIUrl+ UserAPI.post_sendVerificationCodeAPI, postData, null);
    }

    public JSONObject checkVerificationCode(JSONObject postData) {
        JsonParsing jsonParsing = new JsonParsing();
        return jsonParsing.httpPost(UrlManagerImpl.prodAPIUrl+ UserAPI.post_checkVerificationCodeAPI, postData, null);
    }

    public JSONObject emailSignup(JSONObject postData) {
        JsonParsing jsonParsing = new JsonParsing();
        return jsonParsing.httpPost(UrlManagerImpl.prodAPIUrl+ UserAPI.post_signupAPI, postData, null);
    }

    public JSONObject uploadProfileImage(User user, File file) {
        JsonParsing jsonParsing = new JsonParsing();
        return jsonParsing.httpPostMultipart(UrlManagerImpl.prodAPIUrl+ UserAPI.post_imageUplaodAPI, user, file);
    }

    public JSONObject syncDeviceToken(JSONObject postData, String userToken) {
        JsonParsing jsonParsing = new JsonParsing();
        return jsonParsing.httpPost(UrlManagerImpl.prodAPIUrl+UserAPI.post_deviceTokenSyncAPI, postData,userToken);
    }

    public JSONObject syncDevicePhone(JSONObject postData, String userToken) {
        JsonParsing jsonParsing = new JsonParsing();
        return jsonParsing.httpPost(UrlManagerImpl.prodAPIUrl+UserAPI.post_syncPhoneContactsAPI, postData,userToken);
    }

    public JSONObject changePassword(JSONObject postData, String userToken) {
        JsonParsing jsonParsing = new JsonParsing();
        return jsonParsing.httpPost(UrlManagerImpl.prodAPIUrl+UserAPI.post_changePasswordAPI, postData,userToken);
    }

    public JSONObject syncHolidayCalendar(JSONObject postData, String userToken) {
        JsonParsing jsonParsing = new JsonParsing();
        return jsonParsing.httpPost(UrlManagerImpl.prodAPIUrl+UserAPI.post_saveHolidayCalendar, postData,userToken);
    }

    public JSONObject holidayCalendarByUserId(String queryStr, String authToken) {
        try {
            JsonParsing jsonParsing = new JsonParsing();
            String apiUrl = UrlManagerImpl.prodAPIUrl+ UserAPI.get_holidayCalendarByUserId+"?"+queryStr;
            return jsonParsing.httpGetJsonObject(apiUrl,authToken);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONObject updateUserInfo(JSONObject postData, String userToken) {
        try {
            JsonParsing jsonParsing = new JsonParsing();
            String apiUrl = UrlManagerImpl.prodAPIUrl+ UserAPI.post_update_profile_data;
            return jsonParsing.httpPost(apiUrl,postData,userToken);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONObject forgotPassword(String queryStr) {
        try {
            String apiUrl = UrlManagerImpl.prodAPIUrl+UserAPI.get_forget_password_api+"?"+queryStr;
            JsonParsing jsonParsing = new JsonParsing();
            return jsonParsing.httpGetJsonObject(apiUrl,null);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
