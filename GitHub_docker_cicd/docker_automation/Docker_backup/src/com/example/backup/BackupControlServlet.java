package com.example.backup;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.time.LocalTime;

@WebServlet("/BackupControlServlet")
public class BackupControlServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        BackupScheduler scheduler = BackupScheduler.getInstance();
        if (scheduler == null) {
            req.setAttribute("message", "Scheduler not initialized.");
            req.getRequestDispatcher("backup.jsp").forward(req, res);
            return;
        }

        String action = req.getParameter("action");

        try {
            if ("enable".equals(action)) {
                String source = req.getParameter("sourceDir");
                String dest = req.getParameter("destDir");
                String t = req.getParameter("backupTime");

                if (source == null || dest == null || t == null || source.isEmpty() || dest.isEmpty() || t.isEmpty()) {
                    throw new IllegalArgumentException("Source, destination and backup time are required");
                }

                LocalTime time = LocalTime.parse(t);
                scheduler.enableBackup(source, dest, time);
                scheduler.runImmediateBackup(source, dest); // immediate test run
                BackupScheduler.saveTimeToFile(time);
                req.setAttribute("message", "Backup scheduled at " + time);

            } else if ("disable".equals(action)) {
                scheduler.disableBackup();
                req.setAttribute("message", "Backup disabled.");
            }
        } catch (Exception e) {
            req.setAttribute("message", "Error: " + e.getMessage());
        }

        req.getRequestDispatcher("backup.jsp").forward(req, res);
    }
}

