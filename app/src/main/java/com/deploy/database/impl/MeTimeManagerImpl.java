package com.deploy.database.impl;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.deploy.application.CenesApplication;
import com.deploy.bo.MeTime;
import com.deploy.bo.MeTimeItem;
import com.deploy.database.CenesDatabase;
import com.deploy.util.CenesUtils;

import java.util.ArrayList;
import java.util.List;

public class MeTimeManagerImpl {

    CenesApplication cenesApplication;
    CenesDatabase cenesDatabase;
    SQLiteDatabase db;
    MeTimePatternManagerImpl meTimePatternManagerImpl;

    public static String CreateTableQuery = "CREATE TABLE metime_recurring_events (recurring_event_id LONG, " +
            "title TEXT, " +
            "user_id LONG, " +
            "start_time LONG, " +
            "end_time LONG, " +
            "timezone TEXT, " +
            "photo TEXT, " +
            "days TEXT)";

    public MeTimeManagerImpl(CenesApplication cenesApplication){
        this.cenesApplication = cenesApplication;
        cenesDatabase = new CenesDatabase(cenesApplication);
        this.db = cenesDatabase.getReadableDatabase();
        this.meTimePatternManagerImpl = new MeTimePatternManagerImpl(this.cenesApplication);
    }

    public void addMeTime(MeTime metime){

        String title = "";
        if (!CenesUtils.isEmpty(metime.getTitle())) {
            title = metime.getTitle().replaceAll("'","''");
        }

        String timezone = "";
        if (!CenesUtils.isEmpty(metime.getTimezone())) {
            timezone = metime.getTimezone().replaceAll("'","''");
        }

        String photo = "";
        if (!CenesUtils.isEmpty(metime.getPhoto())) {
            photo = metime.getPhoto().replaceAll("'","''");
        }

        String days = "";
        if (!CenesUtils.isEmpty(metime.getDays())) {
            days = metime.getDays().replaceAll("'","''");
        }
        String insertQuery = "insert into metime_recurring_events values("+metime.getRecurringEventId()+", '"+title+"', "+metime.getUserId()+"," +
                " "+metime.getStartTime()+", "+metime.getEndTime()+", '"+metime.getTimezone()+"', '"+photo+"',  '"+days+"')";

        System.out.println(insertQuery);
        db.execSQL(insertQuery);

        for (MeTimeItem meTimeItem: metime.getItems()) {
            this.meTimePatternManagerImpl.addMeTimePattern(meTimeItem);
        }
    }

    public List<MeTime> fetchAllMeTimeRecurringEvents() {

        List<MeTime> metimeRecurringEvents = new ArrayList<>();

        String query = "select * from metime_recurring_events";
        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext()) {
            MeTime meTime = new MeTime();
            meTime.setRecurringEventId(cursor.getLong(cursor.getColumnIndex("recurring_event_id")));
            meTime.setTitle(cursor.getString(cursor.getColumnIndex("title")));
            meTime.setUserId(cursor.getLong(cursor.getColumnIndex("user_id")));
            meTime.setStartTime(cursor.getLong(cursor.getColumnIndex("start_time")));
            meTime.setEndTime(cursor.getLong(cursor.getColumnIndex("end_time")));
            meTime.setTimezone(cursor.getString(cursor.getColumnIndex("timezone")));
            meTime.setPhoto(cursor.getString(cursor.getColumnIndex("photo")));

            List<MeTimeItem> items = this.meTimePatternManagerImpl.fetchMeTimePatternByRecurringEventId(meTime.getRecurringEventId());
            meTime.setItems(items);

            metimeRecurringEvents.add(meTime);
        }
        return metimeRecurringEvents;
    }

    public MeTime findMetimeEventByRecurringEventId(Long recurringEventId) {

        String query = "select * from metime_recurring_events where recurring_event_id = "+recurringEventId+"";
        Cursor cursor = db.rawQuery(query, null);
        MeTime meTime = null;
        if (cursor.moveToFirst()) {
            meTime = new MeTime();
            meTime.setRecurringEventId(cursor.getLong(cursor.getColumnIndex("recurring_event_id")));
            meTime.setTitle(cursor.getString(cursor.getColumnIndex("title")));
            meTime.setUserId(cursor.getLong(cursor.getColumnIndex("user_id")));
            meTime.setStartTime(cursor.getLong(cursor.getColumnIndex("start_time")));
            meTime.setEndTime(cursor.getLong(cursor.getColumnIndex("end_time")));
            meTime.setTimezone(cursor.getString(cursor.getColumnIndex("timezone")));
            meTime.setPhoto(cursor.getString(cursor.getColumnIndex("photo")));

            List<MeTimeItem> items = this.meTimePatternManagerImpl.fetchMeTimePatternByRecurringEventId(meTime.getRecurringEventId());
            meTime.setItems(items);

            return meTime;
        }
        return meTime;
    }

    public void deleteAllMeTimeRecurringEventsByRecurringEventId(Long recurringEventId) {
        String deleteQuery = "delete from metime_recurring_events where recurring_event_id = "+recurringEventId+" ";
        db.execSQL(deleteQuery);

        this.meTimePatternManagerImpl.deleteMeTimeRecurringPatternsByRecurringEventId(recurringEventId);
    }


    public void deleteAllMeTimeRecurringEvents() {
        String deleteQuery = "delete from metime_recurring_events";
        db.execSQL(deleteQuery);

        this.meTimePatternManagerImpl.deleteMeTimeRecurringPatterns();
    }

    public void updateMeTimePhoto(Long recurringEventId, String photoUrl) {

        db.execSQL("update metime_recurring_events set photo = '"+photoUrl+"' where recurring_event_id = "+recurringEventId+" ");

    }

}
