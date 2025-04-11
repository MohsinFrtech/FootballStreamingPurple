package com.tsz.live.football.tv.streaming.hd.horizontalcalendar.utils;

import com.tsz.live.football.tv.streaming.hd.horizontalcalendar.HorizontalCalendarView;

import java.util.Calendar;

public abstract class HorizontalCalendarListener {

    public abstract void onDateSelected(Calendar date, int position);

    public void onCalendarScroll(HorizontalCalendarView calendarView, int dx, int dy) {
    }

    public boolean onDateLongClicked(Calendar date, int position) {
        return false;
    }

}