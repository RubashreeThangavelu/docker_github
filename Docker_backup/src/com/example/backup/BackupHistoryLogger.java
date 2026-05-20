package com.example.backup;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BackupHistoryLogger {

    private static final String LOG_FILE =
            "/usr/local/tomcat/webapps/Docker_backup/backup_history.txt";

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static synchronized void write(
            String source,
            String destination,
            String file,
            String status,
            String reason,
            long size,
            int duration) {

        try {
            File f = new File(LOG_FILE);
            f.getParentFile().mkdirs();

            try (FileWriter fw = new FileWriter(f, true);
                 BufferedWriter bw = new BufferedWriter(fw);
                 PrintWriter out = new PrintWriter(bw)) {

                out.println("======================================");
                out.println("Time       : " + LocalDateTime.now().format(FMT));
                out.println("Source     : " + source);
                out.println("Destination: " + destination);
                out.println("File       : " + file);
                out.println("Status     : " + status);

                if (size >= 0)
                    out.println("Size       : " + size + " bytes");

                if (duration >= 0)
                    out.println("Duration   : " + duration + " sec");

                if (reason != null && !reason.isEmpty())
                    out.println("Reason     : " + reason);

                out.println();
            }

        } catch (IOException e) {
            System.err.println("BackupHistoryLogger failed: " + e.getMessage());
        }
    }

    //  SUCCESS
    public static void logSuccess(
            String source,
            String destination,
            String file,
            long size,
            int duration) {

        write(source, destination, file,
                "Success", null, size, duration);
    }

    //  FAILURE
    public static void logFailure(
            String source,
            String destination,
            String reason,
            int duration) {

        write(source, destination, "N/A",
                "Failed", reason, -1, duration);
    }

    //  SKIPPED
    public static void logSkipped(
            String source,
            String destination,
            String reason) {

        write(source, destination, "None",
                "Skipped", reason, -1, -1);
    }
 // Count total successful backups
    public static int getCompletedBackupCount() {
        int count = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(LOG_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("Status     : Success")) count++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return count;
    }

    // Get number of files in last backup
    public static int getLastBackupFileCount() {
        int fileCount = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(LOG_FILE))) {
            String line;
            String lastFileLine = null;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("File       :")) lastFileLine = line;
            }
            if (lastFileLine != null && !lastFileLine.contains("N/A") && !lastFileLine.contains("None")) {
                fileCount = 1; // or store actual count during BackupJob
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileCount;
    }

}


