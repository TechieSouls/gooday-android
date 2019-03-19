package com.deploy.materialcalendarview;

import java.util.Set;

public class CalendarFilter {

    private Set<CalendarDay> calendarDaySet;

    private int color;

    public Set<CalendarDay> getCalendarDaySet() {
        return calendarDaySet;
    }

    public void setCalendarDaySet(Set<CalendarDay> calendarDaySet) {
        this.calendarDaySet = calendarDaySet;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
