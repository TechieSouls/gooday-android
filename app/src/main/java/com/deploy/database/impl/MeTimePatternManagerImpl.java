package com.deploy.database.impl;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.deploy.application.CenesApplication;
import com.deploy.bo.MeTimeItem;
import com.deploy.database.CenesDatabase;
import com.deploy.util.CenesUtils;

import java.util.ArrayList;
import java.util.List;

public class MeTimePatternManagerImpl {

    CenesApplication cenesApplication;
    CenesDatabase cenesDatabase;
    SQLiteDatabase db;

    public static String CreateTableQuery = "CREATE TABLE metime_recurring_patterns (recurring_event_id LONG, " +
        "day_of_week TEXT, " +
        "day_of_week_timestamp LONG)";

        public MeTimePatternManagerImpl(CenesApplication cenesApplication){
                this.cenesApplication = cenesApplication;
                cenesDatabase = new CenesDatabase(cenesApplication);
                this.db = cenesDatabase.getReadableDatabase();
        }

        public void addMeTimePattern(MeTimeItem meTimeItem){

                String dayOfWeek = "";
                if (!CenesUtils.isEmpty(meTimeItem.getDay_Of_week())) {
                        dayOfWeek = meTimeItem.getDay_Of_week().replaceAll("'","''");
                }

                String insertQuery = "insert into metime_recurring_patterns values("+meTimeItem.getRecurringEventId()+", '"+dayOfWeek+"', "+meTimeItem.getDayOfWeekTimestamp()+")";

                System.out.println(insertQuery);
                db.execSQL(insertQuery);

        }

        public List<MeTimeItem> fetchMeTimePatternByRecurringEventId(Long recurringEventId) {

                List<MeTimeItem> metimeRecurringPatterns = new ArrayList<>();

                String query = "select * from metime_recurring_patterns where recurring_event_id = "+recurringEventId+" ";
                Cursor cursor = db.rawQuery(query, null);

                while (cursor.moveToNext()) {
                        MeTimeItem meTimeItem = new MeTimeItem();
                        meTimeItem.setRecurringEventId(cursor.getLong(cursor.getColumnIndex("recurring_event_id")));
                        meTimeItem.setDay_Of_week(cursor.getString(cursor.getColumnIndex("day_of_week")));
                        meTimeItem.setDayOfWeekTimestamp(cursor.getLong(cursor.getColumnIndex("day_of_week_timestamp")));
                        metimeRecurringPatterns.add(meTimeItem);
                }
                return metimeRecurringPatterns;
        }

        public void deleteMeTimeRecurringPatternsByRecurringEventId(Long recurringEventId) {
                String deleteQuery = "delete from metime_recurring_patterns where recurring_event_id = "+recurringEventId+" ";
                db.execSQL(deleteQuery);
        }

        public void deleteMeTimeRecurringPatterns() {
                String deleteQuery = "delete from metime_recurring_patterns";
                db.execSQL(deleteQuery);
        }
}
