package com.example.backup;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.File;
import java.nio.file.Files;
import java.io.IOException;

@WebListener
public class BackupScheduler implements ServletContextListener {

    private static final Logger logger = Logger.getLogger(BackupScheduler.class.getName());

    private ScheduledExecutorService scheduler;
    private final AtomicReference<ScheduledFuture<?>> currentTask = new AtomicReference<>();
    private final AtomicReference<ScheduledFuture<?>> immediateTask = new AtomicReference<>();

    private String configuredSource;
    private String configuredDest;

    private static BackupScheduler instance;

    public BackupScheduler() {
        instance = this;
    }

    public static BackupScheduler getInstance() {
        return instance;
    }

    private static volatile boolean enabled = true;

    public static boolean isBackupEnabled() {
        return enabled;
    }

    public synchronized void disableBackup() {
        enabled = false;

        if (currentTask.get() != null) {
            currentTask.get().cancel(false);
            currentTask.set(null);
        }

        BackupStatus.update("None", BackupStatus.getNow(), "Disabled");
    }

    public synchronized void enableBackup(String source, String dest, LocalTime time) {
        enabled = true;
        scheduleDailyBackup(source, dest, time);
    }

    public static synchronized void scheduleDailyBackup(String sourceDir, String destDir, LocalTime newTime) {
        if (instance == null || instance.scheduler == null) {
            logger.severe("Scheduler not initialized yet.");
            return;
        }
        instance.doScheduleDailyBackup(sourceDir, destDir, newTime);
    }

    private synchronized void doScheduleDailyBackup(String sourceDir, String destDir, LocalTime newTime) {

        // Cancel previous task
        ScheduledFuture<?> prev = currentTask.getAndSet(null);
        if (prev != null && !prev.isDone()) {
            prev.cancel(false);
            logger.info("Cancelled previous daily backup task.");
        }

        long initialDelay = computeInitialDelay(newTime);
        long period = TimeUnit.DAYS.toMillis(1);

        // Set next run time
        Instant nextRunInstant = Instant.now().plusMillis(initialDelay);
        long nextRunEpochMillis = nextRunInstant.toEpochMilli();
        LocalDateTime nextRunLdt = LocalDateTime.ofInstant(nextRunInstant, ZoneId.systemDefault());
        String nextRunReadable = nextRunLdt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        BackupStatus.setNextRun(nextRunEpochMillis, nextRunReadable);

        // Mark backup as scheduled
        BackupStatus.setLastStatus("Scheduled");
        logger.info("Next backup scheduled at: " + nextRunReadable);

        ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(() -> {
           try {
    if (!enabled) return;

    BackupStatus.setLastStatus("Running");
    BackupStatus.setLastBackupTime(BackupStatus.getNow());

    new BackupJob(sourceDir, destDir).run();



} catch (Throwable t) {
    BackupStatus.setLastStatus("Failed");
    logger.log(Level.SEVERE, "Scheduled backup failed", t);
}


            // Update next run time only
            LocalDateTime afterRunLdt = LocalDateTime.now()
                    .withHour(newTime.getHour())
                    .withMinute(newTime.getMinute())
                    .withSecond(0)
                    .withNano(0)
                    .plusDays(1);

            long afterRunMillis = afterRunLdt
                    .atZone(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli();
            String afterRunReadable = afterRunLdt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            BackupStatus.setNextRun(afterRunMillis, afterRunReadable);

        }, initialDelay, period, TimeUnit.MILLISECONDS);

        currentTask.set(future);
        BackupStatus.setScheduledTime(newTime.toString());
        configuredSource = sourceDir;
        configuredDest = destDir;

        logger.info("Scheduled new daily backup at " + newTime + ". Initial delay (ms): " + initialDelay);
    }

    public static void saveTimeToFile(LocalTime time) {
        File file = new File("/usr/local/tomcat/webapps/Docker_backup/backup-time.txt");
        try {
            Files.writeString(file.toPath(), time.toString());
        } catch (IOException e) {
            Logger.getLogger(BackupScheduler.class.getName())
                  .severe("Failed to save backup time: " + e.getMessage());
        }
    }

    public static synchronized void runImmediateBackup(String sourceDir, String destDir) {
        if (instance == null || instance.scheduler == null) {
            logger.severe("Scheduler not initialized yet.");
            return;
        }
        instance.doRunImmediateBackup(sourceDir, destDir);
    }

    private void doRunImmediateBackup(String sourceDir, String destDir) {

    ScheduledFuture<?> prev = immediateTask.getAndSet(null);
    if (prev != null && !prev.isDone()) prev.cancel(false);

    long nowMillis = System.currentTimeMillis();
    long nextScheduledMillis = BackupStatus.getNextRunMillis();

    // Skip immediate backup if scheduled backup is within 1 min
    if (nextScheduledMillis > 0 && (nextScheduledMillis - nowMillis) <= 60_000) {
        logger.info("Immediate backup skipped: next scheduled backup is very close.");
        return;
    }

    ScheduledFuture<?> f = scheduler.schedule(() -> {
        logger.info("Executing immediate backup...");
        new BackupJob(sourceDir, destDir).run();
    }, 0, TimeUnit.SECONDS);

    immediateTask.set(f);
}

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.info("Starting BackupScheduler...");
        scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "BackupScheduler-thread");
            t.setDaemon(false);
            return t;
        });

        String sourceDir = sce.getServletContext().getInitParameter("backup.sourceDir");
        String destDir = sce.getServletContext().getInitParameter("backup.destDir");
        String backupTime = sce.getServletContext().getInitParameter("backup.time");

        if (sourceDir == null || destDir == null) {
            logger.severe("Missing backup.sourceDir or backup.destDir. Scheduler will not start.");
            return;
        }

        LocalTime time = null;
        if (backupTime != null) {
            try { time = LocalTime.parse(backupTime); } catch (Exception e) {
                logger.warning("Invalid backup.time format; skip auto-schedule: " + e.getMessage());
            }
        }

        LocalTime savedTime = readSavedTime();
        LocalTime finalTime = (savedTime != null ? savedTime : time);

        if (finalTime != null) {
            doScheduleDailyBackup(sourceDir, destDir, finalTime);
            logger.info("Backup scheduled using time: " + finalTime);
        } else {
            logger.warning("No backup time set. Scheduler idle.");
        }

        configuredSource = sourceDir;
        configuredDest = destDir;

        logger.info("BackupScheduler initialized.");
    }

    private LocalTime readSavedTime() {
        File file = new File("/usr/local/tomcat/webapps/Docker_backup/backup-time.txt");
        if (!file.exists()) {
            logger.warning("No saved backup-time file found.");
            return null;
        }

        try {
            String line = Files.readString(file.toPath()).trim();
            return LocalTime.parse(line);
        } catch (Exception e) {
            logger.warning("Failed to read saved time: " + e.getMessage());
            return null;
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info("Stopping BackupScheduler...");
        if (currentTask.get() != null) currentTask.get().cancel(false);
        if (immediateTask.get() != null) immediateTask.get().cancel(false);
        if (scheduler != null) scheduler.shutdownNow();
    }

  private long computeInitialDelay(LocalTime targetTime) {
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime nextRun = now.withHour(targetTime.getHour())
                               .withMinute(targetTime.getMinute())
                               .withSecond(0)
                               .withNano(0);

    if (!nextRun.isAfter(now)) {
        nextRun = nextRun.plusDays(1); // schedule for tomorrow instead of immediately
    }

    return Duration.between(now, nextRun).toMillis();
}

}


