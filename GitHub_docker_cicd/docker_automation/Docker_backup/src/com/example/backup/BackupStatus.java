package com.example.backup;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public final class BackupStatus {
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final AtomicReference<String> scheduledTime = new AtomicReference<>("Not set");
    private static final AtomicReference<String> lastFileName = new AtomicReference<>("None");
    private static final AtomicReference<String> lastBackupTime = new AtomicReference<>("None");
    private static final AtomicReference<String> lastStatus = new AtomicReference<>("None");

    private static final AtomicReference<String> nextRunTimeReadable = new AtomicReference<>("N/A");
    private static final AtomicLong nextRunMillis = new AtomicLong(-1L);

    private static String lastFile = "N/A";

    private BackupStatus() {}

    // ------------------ Scheduled time ------------------
    public static void setScheduledTime(String time) { scheduledTime.set(time); }
    public static String getScheduledTime() { return scheduledTime.get(); }

    // ------------------ Last file, status, and backup time ------------------
    public static synchronized void update(String fileName, String time, String status) {
        lastFileName.set(fileName);
        lastBackupTime.set(time);
        lastStatus.set(status);
    }

    public static synchronized void setLastStatus(String status) {
        lastStatus.set(status != null ? status : "Unknown");
    }

    public static synchronized void setLastBackupTime(String time) {
        lastBackupTime.set(time != null ? time : getNow());
    }

    public static String getLastFileName() { return lastFileName.get(); }
    public static String getLastBackupTime() { return lastBackupTime.get(); }
    public static String getLastStatus() { return lastStatus.get(); }
    public static String getNow() { return LocalDateTime.now().format(FMT); }

    // ------------------ Next run ------------------
    public static void setNextRun(long epochMillis, String readable) {
        nextRunMillis.set(epochMillis);
        nextRunTimeReadable.set(readable);
    }
    public static long getNextRunMillis() { return nextRunMillis.get(); }
    public static String getNextRunReadable() { return nextRunTimeReadable.get(); }

    // ------------------ Last file for BackupJob ------------------
    public static synchronized void setLastFile(String file) { lastFile = file; }
    public static synchronized String getLastFile() { return lastFile; }
}

