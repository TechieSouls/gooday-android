package com.deploy.backendManager;

import com.deploy.Manager.Impl.UrlManagerImpl;
import com.deploy.Manager.JsonParsing;
import com.deploy.api.CenesCommonAPI;
import com.deploy.application.CenesApplication;

import org.json.JSONObject;

public class CenesCommonAPIManager {

    CenesApplication cenesApplication;

    public CenesCommonAPIManager(CenesApplication cenesApplication){
        this.cenesApplication = cenesApplication;
    }

    public JSONObject getBadgeCounts(String queryStr, String authToken) {
        try {
            JsonParsing jsonParsing = new JsonParsing();
            String apiUrl = UrlManagerImpl.prodAPIUrl+ CenesCommonAPI.get_badge_counts_api+"?"+queryStr;
            return jsonParsing.httpGetJsonObject(apiUrl,authToken);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONObject updateBadgeCountsToZero(String queryStr, String authToken) {
        try {
            JsonParsing jsonParsing = new JsonParsing();
            String apiUrl = UrlManagerImpl.prodAPIUrl+ CenesCommonAPI.update_badge_counts_api+"?"+queryStr;
            return jsonParsing.httpGetJsonObject(apiUrl,authToken);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
