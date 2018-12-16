package com.deploy.materialcalendarview.decorators;

import com.deploy.materialcalendarview.CalendarDay;
import com.deploy.materialcalendarview.DayViewDecorator;
import com.deploy.materialcalendarview.DayViewFacade;

import java.util.Collection;
import java.util.HashSet;

public class DayEnableDecorator implements DayViewDecorator {
    private HashSet<CalendarDay> dates;

    public DayEnableDecorator(Collection<CalendarDay> dates) {
        this.dates = new HashSet<>(dates);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setDaysDisabled(false);
    }
}
