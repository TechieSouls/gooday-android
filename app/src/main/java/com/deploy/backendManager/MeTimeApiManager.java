package com.deploy.backendManager;

import android.support.v7.app.AppCompatActivity;

import com.deploy.Manager.Impl.UrlManagerImpl;
import com.deploy.Manager.JsonParsing;
import com.deploy.api.MeTimeAPI;
import com.deploy.application.CenesApplication;
import com.deploy.bo.User;

import org.json.JSONObject;

/**
 * Created by mandeep on 10/1/19.
 */

public class MeTimeApiManager {

    CenesApplication cenesApplication;

    public MeTimeApiManager(CenesApplication cenesApplication) {
        this.cenesApplication = cenesApplication;
    }

    public JSONObject saveMeTime(User user, JSONObject postDataJson) {
        try {
            JsonParsing jsonParsing = new JsonParsing();
            String apiUrl = UrlManagerImpl.prodAPIUrl+MeTimeAPI.post_metimeData;
            return jsonParsing.httpPost(apiUrl,postDataJson,user.getAuthToken());
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONObject getUserMeTimeData(String queryStr, String authToken) {
        try {
            String apiUrl = UrlManagerImpl.prodAPIUrl+MeTimeAPI.get_metimeData+"?"+queryStr;
            JsonParsing jsonParsing = new JsonParsing();
            return jsonParsing.httpGetJsonObject(apiUrl,authToken);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONObject deleteMeTimeData(String queryStr, String authToken) {
        try {
            String apiUrl = UrlManagerImpl.prodAPIUrl+MeTimeAPI.put_deleteByRecurringId+"?"+queryStr;
            JsonParsing jsonParsing = new JsonParsing();
            return jsonParsing.httpPutJsonObject(apiUrl,authToken);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
