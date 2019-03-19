package com.deploy.materialcalendarview.decorators;

import com.deploy.materialcalendarview.CalendarDay;
import com.deploy.materialcalendarview.CalendarFilter;
import com.deploy.materialcalendarview.DayViewDecorator;
import com.deploy.materialcalendarview.DayViewFacade;
import com.deploy.materialcalendarview.spans.CustmMultipleDotSpan;
import com.deploy.materialcalendarview.spans.DotSpan;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class MultipleEventDecorator implements DayViewDecorator {

    private final int[] colors;
    private final HashSet<CalendarDay> dates;

    public MultipleEventDecorator(Collection<CalendarDay> dates, int[] colors) {
        this.dates = new HashSet<>(dates);

        this.colors = colors;
    }

    public MultipleEventDecorator(List<CalendarFilter> filteredEvents) {
        //this.color = color;

        this.dates = new HashSet<>(filteredEvents.get(0).getCalendarDaySet());
        int[]colors = {0};
        colors[0]= filteredEvents.get(0).getColor();
        this.colors = colors;

    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {

        return dates.contains(day);
    }


    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan((new CustmMultipleDotSpan(5, colors)));
    }
}