package com.clothing.management.utils;

import com.clothing.management.enums.SystemStatus;

import java.util.List;
import java.util.stream.Stream;

import static com.clothing.management.enums.SystemStatus.*;

public class SystemStatusUtil {

    public static List<String> getActiveStatuses() {
        return Stream.of(IN_PROGRESS_1, IN_PROGRESS_2, IN_PROGRESS_3, TO_VERIFY)
                .map(String::valueOf)
                .toList();
    }

    public static List<String> getActiveAndConfirmedStatuses() {
        return Stream.concat(getActiveStatuses().stream(), Stream.of(CONFIRMED))
                .map(String::valueOf)
                .toList();
    }

    public static List<String> getDeliveredStatuses() {
        return Stream.of(DELIVERED, PAID).map(String::valueOf).toList();
    }

    public static List<String> getActiveAndDeliveredStatuses() {
        return Stream.concat(getActiveStatuses().stream(), getDeliveredStatuses().stream())
                .map(String::valueOf)
                .toList();
    }

    public static List<String> getActiveConfirmedAndDeliveredAStatuses() {
        return Stream.concat(getActiveAndDeliveredStatuses().stream(), Stream.of(CONFIRMED))
                .map(String::valueOf)
                .toList();
    }

    public static List<String> getReturnStatuses() {
        return Stream.of(RETURN, RETURN_RECEIVED)
                .map(String::valueOf)
                .toList();
    }

    public static List<String> getActiveDeliveredAndReturnStatuses() {
        return Stream.concat(getActiveAndDeliveredStatuses().stream(), getReturnStatuses().stream())
                .map(String::valueOf)
                .toList();
    }

    public static List<String> getActiveConfirmedDeliveredAndReturnStatuses() {
        return Stream.concat(getActiveConfirmedAndDeliveredAStatuses().stream(), getReturnStatuses().stream())
                .map(String::valueOf)
                .toList();
    }

    public static List<String> getActiveConfirmedDeliveredReturnAndOosStatuses() {
        return Stream.concat(getActiveConfirmedDeliveredAndReturnStatuses().stream(), Stream.of(OOS))
                .map(String::valueOf)
                .toList();
    }

    public static List<String> getIgnoredDateStatusList() {
        return Stream.of(RETURN, NOT_CONFIRMED, UNREACHABLE, PROBLEM, TO_VERIFY, OOS, IN_PROGRESS_1, IN_PROGRESS_2, IN_PROGRESS_3)
                .map(String::valueOf)
                .toList();
    }
}
