<%@ page import="com.example.backup.BackupStatus, com.example.backup.BackupHistoryLogger" %>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8" />

<title>Welcome to My Web App</title>
<!--meta http-equiv="refresh" content="15"-->
<style>
body {
    margin: 0;
    font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
    background: linear-gradient(to bottom, #e3f2fd 0%, #d6ecff 50%, #cfe8ff 100%);
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 60px 20px;
    min-height: 100vh;
    color: #2c3e50;
}
header { margin-bottom: 30px; }
h1 { font-size: 2.2rem; margin: 0; text-align: center; color: #0d47a1; }
main { max-width: 600px; text-align: center; }
p { font-size: 1.05rem; line-height: 1.6; }
a.button {
    display: inline-block;
    margin-top: 25px;
    padding: 12px 28px;
    background-color: #1976d2;
    color: #fff;
    border: none;
    border-radius: 6px;
    text-decoration: none;
    font-size: 1rem;
    font-weight: 600;
    transition: all 0.3s ease;
}
a.button:hover { background-color: #0d47a1;     }
.stats-box {
    margin-top:40px;
    padding:20px;
    border-radius:10px;
    background-color:#f1f8ff;
    box-shadow:0 2px 6px rgba(0,0,0,0.1);
    text-align: left;
        animation: fadeSlideUp 0.7s ease-out;
}
.stats-box h2 { color:#0d47a1;  text-align:center;}
.stats-box hr { margin: 10px 0; }
.stats-box p { margin: 8px 0; }

/* Animations */
@keyframes fadeSlideUp {
    from { opacity:0; transform:translateY(25px); }
    to { opacity:1; transform:translateY(0); }
}
@keyframes fadeDown {
    from { opacity:0; transform:translateY(-15px); }
    to { opacity:1; transform:translateY(0); }
}
@keyframes fadeIn {
    from { opacity:0; }
    to { opacity:1; }
}
</style>

<script>
// Optional: auto-refresh the status every 15 seconds
function refreshBackupStatus() {
    fetch('BackupStatusServlet') // create a servlet that returns JSON with current backup status
        .then(res => res.json())
        .then(data => {
            document.getElementById('backupStatus').innerText = data.status;
            document.getElementById('lastBackupTime').innerText = data.lastBackupTime;
            document.getElementById('totalBackups').innerText = data.totalBackups;
            document.getElementById('filesLastBackup').innerText = data.filesLastBackup;
        })
        .catch(err => console.log(err));
}

setInterval(refreshBackupStatus, 15000);
</script>

</head>
<body>
<header>
  <h1>Welcome to Backup Scheduler</h1>

</header>

<main>
  <p>Manage and monitor your system backups easily.</p>
  <p>Schedule daily backups, run immediate backups, and track backup history all from this dashboard.</p>
  <p>Ensure your important files are safe and always up-to-date.</p>
<a href="login.html" class="button">Go to Login Page</a>


<div class="stats-box">
  <h2>Backup Statistics</h2>
  <hr>
  <p><strong>Last Backup Time:</strong> <span id="lastBackupTime"><%= BackupStatus.getLastBackupTime() %></span></p>
  <p><strong>Total Backups Completed:</strong> <span id="totalBackups"><%= BackupHistoryLogger.getCompletedBackupCount() %></span></p>
  <p><strong>Files in Last Backup:</strong> <span id="filesLastBackup"><%= BackupHistoryLogger.getLastBackupFileCount() %></span></p>
<p><strong>Status:</strong> 
  <span id="backupStatus"><%= BackupStatus.getLastStatus() %></span>
</p>

</div>
</main>
</body>
</html>

