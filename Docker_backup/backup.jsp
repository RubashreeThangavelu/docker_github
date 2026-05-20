<%@ page import="com.example.backup.BackupScheduler" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Backup Scheduler</title>
<style>
body {
    margin:0;
    padding:0;
    font-family: Arial,sans-serif;
    background: linear-gradient(135deg,#e3f2fd,#bbdefb);
    min-height:100vh;
}

/* Page title */
h2 {
    text-align:center;
    margin-top:40px;
    color:#0d47a1;
    animation: fadeDown 0.6s ease-out;
}

/* Card animation */
.card {
    width:380px;
    margin:30px auto;
    padding:25px;
    background:white;
    border-radius:12px;
    box-shadow:0 0 18px rgba(0,0,0,0.15);
    animation: fadeSlideUp 0.7s ease-out;
}

/* Labels & inputs */
label {
    display:block;
    margin-bottom:15px;
    font-weight:bold;
    color:#333;
}

input[type="text"],
input[type="time"] {
    width:100%;
    padding:9px;
    margin-top:6px;
    border:1px solid #90caf9;
    border-radius:6px;
    transition: border-color 0.3s, box-shadow 0.3s;
}

/* Input focus glow */
input:focus {
    outline:none;
    border-color:#1976d2;
    box-shadow:0 0 6px rgba(25,118,210,0.5);
}

/* Buttons */
button {
    width:100%;
    padding:11px;
    margin-top:15px;
    border:none;
    border-radius:6px;
    cursor:pointer;
    font-size:15px;
    color:white;
    transition: transform 0.2s ease, box-shadow 0.2s ease;
}

/* Button hover lift */
button:hover {
    transform: translateY(-2px);
    box-shadow:0 6px 12px rgba(0,0,0,0.2);
}

.enable-btn {
    background-color:#1976d2;
}

.enable-btn:hover {
    background-color:#0d47a1;
}

.disable-btn {
    background-color:#d32f2f;
}

.disable-btn:hover {
    background-color:#b71c1c;
}

/* Status text */
.status {
    text-align:center;
    font-weight:bold;
    margin-top:15px;
    color:#0d47a1;
    animation: fadeIn 0.8s ease-in;
}

/* Message animation */
.message {
    text-align:center;
    color:green;
    margin-top:10px;
    font-weight:bold;
    animation: popIn 0.4s ease;
}

/* Link */
.link {
    text-align:center;
    margin-top:20px;
}

a {
    color:#0d47a1;
    font-weight:bold;
    text-decoration:none;
    transition: color 0.2s;
}

a:hover {
    color:#08306b;
}

/* ===== ANIMATIONS ===== */

@keyframes fadeSlideUp {
    from {
        opacity:0;
        transform: translateY(25px);
    }
    to {
        opacity:1;
        transform: translateY(0);
    }
}

@keyframes fadeDown {
    from {
        opacity:0;
        transform: translateY(-15px);
    }
    to {
        opacity:1;
        transform: translateY(0);
    }
}

@keyframes fadeIn {
    from { opacity:0; }
    to { opacity:1; }
}

@keyframes popIn {
    0% { transform: scale(0.8); opacity:0; }
    100% { transform: scale(1); opacity:1; }
}
</style>

</head>
<body>

<h2>Backup Scheduler</h2>
<div class="card">
    <form method="post" action="BackupControlServlet">
        <label>Source Directory
            <input type="text" name="sourceDir" required placeholder="/home/user/source">
        </label>
        <label>Destination Directory
            <input type="text" name="destDir" required placeholder="/home/user/backup">
        </label>
        <label>Backup Time
            <input type="time" name="backupTime" required>
        </label>
        <input type="hidden" name="action" value="enable">
        <button type="submit" class="enable-btn">
            <%= BackupScheduler.isBackupEnabled() ? "Update & Reschedule Backup" : "Enable & Schedule Backup" %>
        </button>
    </form>


    <form method="post" action="BackupControlServlet" style="margin-top:10px;">
        <input type="hidden" name="action" value="disable">
        <button type="submit" class="disable-btn">Disable Backup</button>
    </form>

    <p class="status">Current Status: <%= BackupScheduler.isBackupEnabled() ? "Enabled" : "Disabled" %></p>

    <% if (request.getAttribute("message") != null) { %>
        <p class="message"><%= request.getAttribute("message") %></p>
    <% } %>
</div>

<div class="link">
    <a href="index.jsp">View Backup Status</a>
</div>
</body>
</html>

