package com.example.backup;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BackupJob implements Runnable {

    private static final Logger logger = Logger.getLogger(BackupJob.class.getName());
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final String sourceDir;
    private final String destDir;

    public BackupJob(String sourceDir, String destDir) {
        this.sourceDir = sourceDir;
        this.destDir = destDir;
    }

    @Override
    public void run() {
        long start = System.currentTimeMillis();
        String now = LocalDateTime.now().format(FMT);

        File source = new File(sourceDir);
          validatePath(sourceDir, "source");
            validatePath(destDir, "destination");

        // Source directory missing
        if (!source.exists() || !source.isDirectory()) {
            logger.severe("Source directory not found: " + sourceDir);
            BackupStatus.update("N/A", now, "Failed");
            BackupHistoryLogger.logFailure(sourceDir, destDir, "Source not found", 0);
            return;
        }

        File[] files = source.listFiles(File::isFile);

        // No files to backup
        if (files == null || files.length == 0) {
            logger.warning("No files found in source directory: " + sourceDir);
            BackupStatus.update("None", now, "Skipped");
            BackupHistoryLogger.logSkipped(sourceDir, destDir, "No files found");
            return;
        }

        try {
            // Find the latest file
            File latestFile = files[0];
            for (File f : files) {
                if (f.lastModified() > latestFile.lastModified()) {
                    latestFile = f;
                }
            }

            // Prepare destination
            File destFolder = new File(destDir);
            if (!destFolder.exists() && !destFolder.mkdirs()) {
                logger.severe("Could not create destination directory: " + destDir);
                BackupStatus.update(latestFile.getName(), now, "Failed");
                BackupHistoryLogger.logFailure(sourceDir, destDir, "Cannot create destination", 0);
                return;
            }
if (isRestrictedPath(destDir)) {
    logger.severe("Permission denied for destination: " + destDir);
    BackupStatus.update("Error", now, "Failed");
    throw new RuntimeException("Restricted destination: " + destDir);
}
            // Copy latest file
            Path dst = new File(destFolder, latestFile.getName()).toPath();
            Files.copy(latestFile.toPath(), dst, StandardCopyOption.REPLACE_EXISTING);

            int duration = (int)((System.currentTimeMillis() - start) / 1000);
            BackupStatus.update(latestFile.getName(), LocalDateTime.now().format(FMT), "Success");
            BackupHistoryLogger.logSuccess(sourceDir, destDir, latestFile.getName(), Files.size(dst), duration);

            logger.info("Backup successful: " + latestFile.getName() + " -> " + dst.toAbsolutePath());

        } catch (IOException e) {
            int duration = (int)((System.currentTimeMillis() - start) / 1000);
            logger.log(Level.SEVERE, "Backup failed", e);
            BackupStatus.update("Error", LocalDateTime.now().format(FMT), "Failed");
            BackupHistoryLogger.logFailure(sourceDir, destDir, e.getMessage(), duration);
            throw new RuntimeException("Backup failed due to IO error: " + e.getMessage(), e);

        }
    }
        private void validatePath(String path, String type) {
        if (isRestrictedPath(path)) {
            logger.severe("Permission denied for " + type + ": " + path);
            BackupStatus.update("Error", LocalDateTime.now().format(FMT), "Failed");
            throw new RuntimeException("Restricted " + type + ": " + path);
        }
    }

    
    private boolean isRestrictedPath(String path) {
    return path != null && (
        path.startsWith("/app/restricted") ||
        path.startsWith("/root") ||
        path.contains("restricted") ||
        path.contains("secure")
    );
}
}

