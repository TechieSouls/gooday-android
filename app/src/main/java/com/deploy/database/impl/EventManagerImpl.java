package com.deploy.database.impl;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.deploy.application.CenesApplication;
import com.deploy.bo.Event;
import com.deploy.database.CenesDatabase;

import java.util.ArrayList;
import java.util.List;

public class EventManagerImpl {

    CenesApplication cenesApplication;
    CenesDatabase cenesDatabase;
    SQLiteDatabase db;

    public static String createTableQuery = "CREATE TABLE events (event_id LONG, " +
            "title TEXT, " +
            "description TEXT, " +
            "start_time LONG, " +
            "end_time LONG, " +
            "photo TEXT," +
            "schedule_as TEXT," +
            "created_by_id INTEGER," +
            "location TEXT," +
            "latitude TEXT," +
            "longitude TEXT," +
            "source TEXT," +
            "key TEXT)";

    public EventManagerImpl(CenesApplication cenesApplication){
        this.cenesApplication = cenesApplication;
        cenesDatabase = new CenesDatabase(cenesApplication);
        this.db = cenesDatabase.getReadableDatabase();
    }

    public List<Event> fetchAllEvents() {

        List<Event> events = new ArrayList<>();

        String query = "select * from events";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToNext()) {

            Event event = new Event();
            event.setEventId(cursor.getLong(cursor.getColumnIndex("event_id")));
            event.setTitle(cursor.getString(cursor.getColumnIndex("title")));
            event.setDescription(cursor.getString(cursor.getColumnIndex("description")));
            event.setStartTime(cursor.getLong(cursor.getColumnIndex("start_time")));
            event.setEndTime(cursor.getLong(cursor.getColumnIndex("end_time")));
            event.setEventPicture(cursor.getString(cursor.getColumnIndex("photo")));
            event.setScheduleAs(cursor.getString(cursor.getColumnIndex("schedule_as")));
            event.setCreatedById(cursor.getInt(cursor.getColumnIndex("created_by_id")));
            event.setLocation(cursor.getString(cursor.getColumnIndex("location")));
            event.setLatitude(cursor.getString(cursor.getColumnIndex("latitude")));
            event.setLongitude(cursor.getString(cursor.getColumnIndex("longitude")));
            event.setSource(cursor.getString(cursor.getColumnIndex("source")));
            event.setKey(cursor.getString(cursor.getColumnIndex("key")));
            events.add(event);
            return events;
        }


        return events;
    }

    public void deleteAllEvents() {
        String deleteQuery = "delete from events";
        db.execSQL(deleteQuery);
    }
}
