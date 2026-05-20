package com.example.backup;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.time.LocalTime;

@WebServlet("/set-backup-time")
public class SetBackupTimeServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String sourceDir = request.getParameter("sourceDir");
        String destDir = request.getParameter("destDir");
        String backupTime = request.getParameter("backupTime");

        if (sourceDir == null || sourceDir.isEmpty()
                || destDir == null || destDir.isEmpty()
                || backupTime == null || backupTime.isEmpty()) {

            request.setAttribute("message", "All fields are required!");
            request.getRequestDispatcher("/index.jsp").forward(request, response);
            return;
        }

        try {
            LocalTime newTime = LocalTime.parse(backupTime);

            BackupScheduler.saveTimeToFile(newTime);
            BackupScheduler.scheduleDailyBackup(sourceDir, destDir, newTime);

            // ✅ STORE VALUES IN SESSION
            HttpSession session = request.getSession();
            session.setAttribute("sourceDir", sourceDir);
            session.setAttribute("destDir", destDir);
            session.setAttribute("backupTime", backupTime);
            session.setAttribute("message", "Backup scheduled successfully!");

            // ✅ redirect is OK now
            response.sendRedirect(request.getContextPath() + "/index.jsp");

        } catch (Exception e) {
            request.setAttribute("message", "Invalid time format!");
            request.getRequestDispatcher("/index.jsp").forward(request, response);
        }
    }
}

