package com.deploy.database.impl;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.deploy.application.CenesApplication;
import com.deploy.bo.Event;
import com.deploy.bo.EventMember;
import com.deploy.bo.Notification;
import com.deploy.database.CenesDatabase;
import com.deploy.util.CenesUtils;

import java.util.ArrayList;
import java.util.List;

public class EventManagerImpl {

    CenesApplication cenesApplication;
    CenesDatabase cenesDatabase;
    SQLiteDatabase db;
    EventMemberManagerImpl eventMemberManagerImpl;

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
            "display_at_screen TEXT," +
            "key TEXT)";

    public EventManagerImpl(CenesApplication cenesApplication){
        this.cenesApplication = cenesApplication;
        cenesDatabase = new CenesDatabase(cenesApplication);
        this.db = cenesDatabase.getReadableDatabase();
        this.eventMemberManagerImpl = new EventMemberManagerImpl(cenesApplication);
    }

    public void addEvent(List<Event> events, String displayAtScreen) {

        for (Event event: events) {
            event.setDisplayAtScreen(displayAtScreen);
            addEvent(event);
        }
    }
    public void addEvent(Event event){

        String description = "";
        if (!CenesUtils.isEmpty(event.getDescription())) {
            description = event.getDescription().replaceAll("'","''");
        }

        String location = "";
        if (!CenesUtils.isEmpty(event.getLocation())) {
            location = event.getLocation().replaceAll("'","''");
        }
        String insertQuery = "insert into events values("+event.getEventId()+", '"+event.getTitle().replaceAll("'","''")+"', '"+description+"'," +
                " "+event.getStartTime()+", "+event.getEndTime()+", '"+event.getEventPicture()+"', '"+event.getScheduleAs()+"', " +
                ""+event.getCreatedById()+", '"+location+"', '"+event.getLatitude()+"', '"+event.getLongitude()+"', " +
                "'"+event.getSource()+"', '"+event.getDisplayAtScreen()+"', '"+event.getKey()+"')";

        System.out.println(insertQuery);
        db.execSQL(insertQuery);

        eventMemberManagerImpl.addEventMember(event.getEventMembers());
    }

    public List<Event> fetchAllEventsByScreen(String displayAtScreen) {

        List<Event> events = new ArrayList<>();

        String query = "select * from events where display_at_screen = '"+displayAtScreen+"' ";
        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext()) {
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
            event.setDisplayAtScreen(cursor.getString(cursor.getColumnIndex("display_at_screen")));

            List<EventMember> eventMembers = eventMemberManagerImpl.fetchEventMembersByEventId(event.getEventId());
            //List<EventMember> eventMembers = eventMemberManagerImpl.fetchEventMembersByEventAtScreen(displayAtScreen);
            event.setEventMembers(eventMembers);
            events.add(event);
        }
        cursor.close();
        return events;
    }

    public Event findEventByEventId(Long eventId) {

        String query = "select * from events where event_id = "+eventId+"";
        Cursor cursor = db.rawQuery(query, null);
        Event event = null;
        if (cursor.moveToFirst()) {
            event = new Event();
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
            event.setDisplayAtScreen(cursor.getString(cursor.getColumnIndex("display_at_screen")));

            List<EventMember> eventMembers = eventMemberManagerImpl.fetchEventMembersByEventId(event.getEventId());
            //List<EventMember> eventMembers = eventMemberManagerImpl.fetchEventMembersByEventAtScreen(Event.EventDisplayScreen.HOME.toString());

            event.setEventMembers(eventMembers);
            return event;
        }
        return event;
    }

    public boolean isEventExist(Event event){
        Cursor cursor = db.rawQuery("select * from events where event_id = "+event.getEventId()+" ", null);
        if (cursor.moveToNext()) {
            return true;
        }
        return false;
    }

    public void deleteAllEventsByDisplayAtScreen(String dispayAtScreen) {
        String deleteQuery = "delete from events where display_at_screen = '"+dispayAtScreen+"'";
        db.execSQL(deleteQuery);
        eventMemberManagerImpl.deleteAllFromEventMembers();
    }

    public void deleteAllEvents() {
        String deleteQuery = "delete from events";
        db.execSQL(deleteQuery);

        eventMemberManagerImpl.deleteAllFromEventMembers();
    }
}
