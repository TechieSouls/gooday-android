package com.deploy.backendManager;

import com.deploy.Manager.Impl.UrlManagerImpl;
import com.deploy.Manager.JsonParsing;
import com.deploy.api.HomeScreenAPI;
import com.deploy.api.UserAPI;
import com.deploy.application.CenesApplication;

import org.json.JSONObject;

/**
 * Created by mandeep on 25/10/18.
 */

public class HomeScreenApiManager {

    CenesApplication cenesApplication;

    public HomeScreenApiManager(CenesApplication cenesApplication){
        this.cenesApplication = cenesApplication;
    }

    public JSONObject refreshGoogleEvents(String params, String authToken) {
        String url = UrlManagerImpl.prodAPIUrl+ HomeScreenAPI.get_refreshGoogleEvents+"?"+params;
        JsonParsing jsonParsing = new JsonParsing();
        return jsonParsing.httpGetJsonObject(url, authToken);
    }

    public JSONObject refreshOutlookEvents(String params, String authToken) {
        String url = UrlManagerImpl.prodAPIUrl+ HomeScreenAPI.get_refreshGOutlookEvents+"?"+params;
        JsonParsing jsonParsing = new JsonParsing();
        return jsonParsing.httpGetJsonObject(url, authToken);
    }


    public JSONObject getHomescreenEvents(String params, String authToken) {
        String url = UrlManagerImpl.prodAPIUrl+ HomeScreenAPI.get_homescreen_events+"?"+params;
        JsonParsing jsonParsing = new JsonParsing();
        return jsonParsing.httpGetJsonObject(url, authToken);
    }

}
