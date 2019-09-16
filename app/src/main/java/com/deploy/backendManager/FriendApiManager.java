package com.deploy.backendManager;

import com.deploy.Manager.Impl.UrlManagerImpl;
import com.deploy.Manager.JsonParsing;
import com.deploy.api.FriendAPI;
import com.deploy.api.GatheringAPI;
import com.deploy.application.CenesApplication;

import org.json.JSONObject;

public class FriendApiManager {

    CenesApplication cenesApplication;

    public FriendApiManager(CenesApplication cenesApplication){
        this.cenesApplication = cenesApplication;
    }

    public JSONObject getFriendsList(String queryStr, String authToken) {
        try {
            JsonParsing jsonParsing = new JsonParsing();
            String apiUrl = UrlManagerImpl.prodAPIUrl+ FriendAPI.get_friends+"?"+queryStr;
            return jsonParsing.httpGetJsonObject(apiUrl,authToken);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
