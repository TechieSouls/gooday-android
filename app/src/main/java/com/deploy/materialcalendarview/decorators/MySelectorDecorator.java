package com.deploy.materialcalendarview.decorators;

import android.app.Activity;
import android.graphics.drawable.Drawable;

import com.deploy.R;
import com.deploy.materialcalendarview.CalendarDay;
import com.deploy.materialcalendarview.DayViewDecorator;
import com.deploy.materialcalendarview.DayViewFacade;

/**
 * Use a custom selector
 */
public class MySelectorDecorator implements DayViewDecorator {

    private final Drawable drawable;

    public MySelectorDecorator(Activity context) {
        drawable = context.getResources().getDrawable(R.drawable.home_icon);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return true;
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setSelectionDrawable(drawable);
    }
}
