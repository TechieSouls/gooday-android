package com.deploy.database.impl;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.deploy.application.CenesApplication;
import com.deploy.bo.EventMember;
import com.deploy.bo.User;
import com.deploy.database.CenesDatabase;
import com.deploy.util.CenesUtils;

import java.util.ArrayList;
import java.util.List;

public class EventMemberManagerImpl  {

    CenesApplication cenesApplication;
    CenesDatabase cenesDatabase;
    SQLiteDatabase db;
    CenesUserManagerImpl cenesUserManagerImpl;

    public static String createTableQuery = "CREATE TABLE event_members (event_member_id LONG, " +
            "event_id LONG, " +
            "name TEXT, " +
            "picture TEXT, " +
            "status TEXT, " +
            "user_id INTEGER, " +
            "phone TEXT," +
            "cenes_member TEXT," +
            "user_contact_id INTEGER)";

    public EventMemberManagerImpl(CenesApplication cenesApplication){
        this.cenesApplication = cenesApplication;
        cenesDatabase = new CenesDatabase(cenesApplication);
        this.db = cenesDatabase.getReadableDatabase();
        this.cenesUserManagerImpl = new CenesUserManagerImpl(cenesApplication);
    }

    public void addEventMember(List<EventMember> eventMembers) {

        for (EventMember eventMember: eventMembers) {
            addEventMember(eventMember);
        }
    }
    public void addEventMember(EventMember eventMember){

        if (CenesUtils.isEmpty(eventMember.getPicture())) {
            eventMember.setPicture("");
        }
        if (CenesUtils.isEmpty(eventMember.getPhone())) {
            eventMember.setPhone("");
        }
        if (CenesUtils.isEmpty(eventMember.getCenesMember())) {
            eventMember.setCenesMember("");
        }
        String insertQuery = "insert into event_members values("+eventMember.getEventMemberId()+", "+eventMember.getEventId()+", '"+eventMember.getName()+"'," +
                " '"+eventMember.getPicture()+"', '"+eventMember.getStatus()+"', "+eventMember.getUserId()+", '"+eventMember.getPhone()+"', " +
                "'"+eventMember.getCenesMember()+"', "+eventMember.getUserContactId()+")";
        db.execSQL(insertQuery);

        if (eventMember.getUser() != null) {
            cenesUserManagerImpl.addUser(eventMember.getUser());
        }
    }

    public List<EventMember> fetchEventMembersByEventId(Long eventId) {

        List<EventMember> eventMembers = new ArrayList<>();

        String query = "select * from event_members where event_id = "+eventId;
        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext()) {

            EventMember eventMember = new EventMember();
            eventMember.setEventId(cursor.getLong(cursor.getColumnIndex("event_id")));
            eventMember.setEventMemberId(cursor.getLong(cursor.getColumnIndex("event_member_id")));
            eventMember.setName(cursor.getString(cursor.getColumnIndex("name")));
            eventMember.setUserId(cursor.getInt(cursor.getColumnIndex("user_id")));
            eventMember.setUserContactId(cursor.getInt(cursor.getColumnIndex("user_contact_id")));
            eventMember.setPicture(cursor.getString(cursor.getColumnIndex("picture")));

            if (eventMember.getUserId() != null) {
                User user = this.cenesUserManagerImpl.fetchCenesUserByUserId(eventMember.getUserId());
                eventMember.setUser(user);
            }
            eventMembers.add(eventMember);
        }

        return eventMembers;
    }

    public List<EventMember> fetchEventMembersByEventAtScreen(String displayAtScreen) {

        List<EventMember> eventMembers = new ArrayList<>();

        String query = "select * from event_members where event_id in (select event_id from events where display_at_screen = '"+displayAtScreen+"' )";
        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext()) {

            EventMember eventMember = new EventMember();
            eventMember.setEventId(cursor.getLong(cursor.getColumnIndex("event_id")));
            eventMember.setEventMemberId(cursor.getLong(cursor.getColumnIndex("event_member_id")));
            eventMember.setName(cursor.getString(cursor.getColumnIndex("name")));
            eventMember.setUserId(cursor.getInt(cursor.getColumnIndex("user_id")));
            eventMember.setUserContactId(cursor.getInt(cursor.getColumnIndex("user_contact_id")));
            eventMember.setPicture(cursor.getString(cursor.getColumnIndex("picture")));

            if (eventMember.getUserId() != null) {
                User user = this.cenesUserManagerImpl.fetchCenesUserByUserId(eventMember.getUserId());
                eventMember.setUser(user);
            }
            eventMembers.add(eventMember);
        }
        cursor.close();
        return eventMembers;
    }

    public void deleteAllFromEventMembers() {
        String deleteQuery = "delete from event_members";
        db.execSQL(deleteQuery);

        cenesUserManagerImpl.deleteAllUsers();
    }
}
