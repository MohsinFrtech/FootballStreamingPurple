package com.tsz.live.football.tv.streaming.hd.horizontalcalendar.utils;

import com.tsz.live.football.tv.streaming.hd.horizontalcalendar.model.CalendarEvent;

import java.util.Calendar;
import java.util.List;

/**
 * @author Mulham-Raee
 * @since v1.3.2
 */
public interface CalendarEventsPredicate {


    List<CalendarEvent> events(Calendar date);
}
