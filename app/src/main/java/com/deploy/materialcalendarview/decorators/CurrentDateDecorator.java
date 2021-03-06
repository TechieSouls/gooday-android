package com.deploy.materialcalendarview.decorators;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.style.ForegroundColorSpan;

import com.deploy.materialcalendarview.CalendarDay;
import com.deploy.materialcalendarview.DayViewDecorator;
import com.deploy.materialcalendarview.DayViewFacade;

public class CurrentDateDecorator implements DayViewDecorator {

    private CalendarDay date;
    private Drawable drawable;

    public CurrentDateDecorator(Context context, int resId) {
        date = CalendarDay.today();
        drawable = ContextCompat.getDrawable(context, resId);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return date != null && day.equals(date);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new ForegroundColorSpan(Color.WHITE));
        view.setBackgroundDrawable(drawable);
        view.setSelectionDrawable(new ColorDrawable(Color.TRANSPARENT));
    }
}
