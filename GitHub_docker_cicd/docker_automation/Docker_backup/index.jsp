<%@ page import="com.example.backup.BackupStatus" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Backup Status</title>

   <style>
body {
    font-family: Arial, sans-serif;
    margin: 0;
    padding: 0;
    background: linear-gradient(135deg, #e3f2fd, #bbdefb);
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: 100vh;
}

/* ===== NAVBAR ===== */
.navbar {
    width: 100%;
    background-color: #2c3e50;
    padding: 12px 0;
    position: fixed;
    top: 0;
    left: 0;
    z-index: 1000;
    text-align: center;
    animation: slideDown 0.5s ease-out;
}

.navbar ul {
    list-style: none;
    margin: 0;
    padding: 0;
    display: inline-flex;
    gap: 20px;
}

.navbar ul li a {
    color: #ffffff;
    text-decoration: none;
    font-weight: bold;
    padding: 6px 12px;
    border-radius: 6px;
    transition: background-color 0.2s ease, transform 0.2s ease;
}

.navbar ul li a:hover {
    background-color: #34495e;
    transform: translateY(-2px);
}

/* ===== CARD ===== */
.card {
    background: #fff;
    padding: 25px 35px;
    border-radius: 12px;
    box-shadow: 0 0 15px rgba(0,0,0,0.1);
    width: 520px;
    margin-top: 90px;
    animation: fadeSlideUp 0.7s ease-out;
}

h2 {
    color: #2c3e50;
    margin-top: 0;
    text-align: center;
    animation: fadeIn 0.8s ease;
}

p {
    margin: 8px 0;
    animation: fadeIn 0.9s ease;
}

hr {
    margin: 15px 0;
}

/* ===== STATUS COLORS ===== */
.status-success {
    color: green;
    font-weight: bold;
    animation: popIn 0.4s ease;
}

.status-failed {
    color: red;
    font-weight: bold;
    animation: popIn 0.4s ease;
}

.status-other {
    color: gray;
    font-weight: bold;
    animation: fadeIn 0.4s ease;
}

/* ===== COUNTDOWN ===== */
.countdown {
    font-size: 1.4em;
    font-weight: bold;
    color: #007bff;
    text-align: center;
    transition: color 0.5s ease;
    animation: fadeIn 1s ease;
}

.soon {
    color: #c62828;
    animation: pulse 1.5s infinite;
}

/* ===== ANIMATIONS ===== */
@keyframes fadeSlideUp {
    from {
        opacity: 0;
        transform: translateY(30px);
    }
    to {
        opacity: 1;
        transform: translateY(0);
    }
}

@keyframes slideDown {
    from {
        transform: translateY(-100%);
    }
    to {
        transform: translateY(0);
    }
}

@keyframes fadeIn {
    from { opacity: 0; }
    to { opacity: 1; }
}

@keyframes popIn {
    0% { transform: scale(0.85); opacity: 0; }
    100% { transform: scale(1); opacity: 1; }
}

@keyframes pulse {
    0% { opacity: 1; }
    50% { opacity: 0.5; }
    100% { opacity: 1; }
}
</style>

</head>

<body>



<!-- ===== STATUS CARD ===== -->
<div class="card">
    <h2>Backup Status</h2>

    <p><strong>Scheduled Time:</strong> <%= BackupStatus.getScheduledTime() %></p>
    <p><strong>Last File:</strong> <%= BackupStatus.getLastFileName() %></p>
    <p><strong>Last Run:</strong> <%= BackupStatus.getLastBackupTime() %></p>

    <p>
        <strong>Status:</strong>
        <span class="<%=
            "Success".equalsIgnoreCase(BackupStatus.getLastStatus()) ? "status-success" :
            "Failed".equalsIgnoreCase(BackupStatus.getLastStatus()) ? "status-failed" :
            "status-other"
        %>">
            <%= BackupStatus.getLastStatus() %>
        </span>
    </p>

    <hr>

    <p>
        <strong>Next Scheduled Run:</strong>
        <span id="nextRunReadable">
            <%= BackupStatus.getNextRunReadable() %>
        </span>
    </p>

    <div id="countdown"
         class="countdown"
         data-next="<%= BackupStatus.getNextRunMillis() %>">
        Loading countdown...
    </div>

    <hr>
    <p style="text-align:center;">
        <em>Page refreshes every 15 seconds.</em>
    </p>
</div>

<!-- ===== COUNTDOWN SCRIPT ===== -->
<script>
(function() {
    function getNextMillis() {
        const el = document.getElementById('countdown');
        const v = el.getAttribute('data-next');
        const n = Number(v);
        return Number.isFinite(n) && n > 0 ? n : null;
    }

    function formatRemaining(ms) {
        if (ms <= 0) return "Running now...";
        const total = Math.floor(ms / 1000);
        const days = Math.floor(total / 86400);
        const hours = Math.floor((total % 86400) / 3600);
        const mins = Math.floor((total % 3600) / 60);
        const secs = total % 60;
        const parts = [];
        if (days) parts.push(days + "d");
        parts.push(String(hours).padStart(2,'0') + "h");
        parts.push(String(mins).padStart(2,'0') + "m");
        parts.push(String(secs).padStart(2,'0') + "s");
        return parts.join(' ');
    }

    function update() {
        const nextMillis = getNextMillis();
        const el = document.getElementById('countdown');
        if (!nextMillis) {
            el.textContent = "Next run not scheduled";
            return;
        }
        const diff = nextMillis - Date.now();
        el.textContent = formatRemaining(diff);
        if (diff <= 60000 && diff > 0) {
            el.classList.add('soon');
        } else {
            el.classList.remove('soon');
        }
        if (diff <= 0) {
            el.textContent = "Running now...";
            setTimeout(() => location.reload(), 5000);
        }
    }

    update();
    setInterval(update, 1000);
})();
</script>

<!-- ===== AUTO REFRESH ===== -->
<script>
    setTimeout(() => location.reload(), 15000);
</script>

</body>
</html>

