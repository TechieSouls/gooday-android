package com.deploy.api;

/**
 * Created by mandeep on 25/10/18.
 */

public class HomeScreenAPI {

    //GET
    public static String get_refreshGoogleEvents = "/api/google/refreshEvents";
    public static String get_refreshGOutlookEvents = "/api/outlook/refreshevents";
    public static String get_homescreen_events = "/api/getEvents"; //user_id, date(in millis)

}
