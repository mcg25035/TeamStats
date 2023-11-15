package org.teamstats.util;

import java.time.Instant;
import java.time.ZoneOffset;

public class Time {
    public static int dayOfMonth(Instant instant) {
        return instant.atZone(ZoneOffset.systemDefault()).getDayOfMonth();
    }
}
