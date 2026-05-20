package com.example.backup;

import java.io.*;
import java.sql.*;
import java.util.Properties;
import jakarta.servlet.*;
import jakarta.servlet.http.*;


public class LoginServlet extends HttpServlet {

    // Handles GET request - show login page
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Forward to login.html (or .jsp, depending on what you're using)
        RequestDispatcher rd = request.getRequestDispatcher("login.html"); // or "login.jsp" if you're using JSP
        rd.forward(request, response);
    }

    // Handles POST request - process login
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String uname = request.getParameter("username");
        String pass = request.getParameter("password");

        Properties prop = new Properties();
try (InputStream input = getServletContext().getResourceAsStream("/WEB-INF/db.properties")) {
    if (input == null) {
        throw new FileNotFoundException("Property file not found in /WEB-INF/db.properties");
    }
    prop.load(input);
} catch (IOException e) {
    e.printStackTrace();
}

String jdbcURL = prop.getProperty("jdbc.url");
String dbUser = prop.getProperty("jdbc.username");
String dbPass = prop.getProperty("jdbc.password");

        boolean isValid = false;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(jdbcURL, dbUser, dbPass);
            String sql = "SELECT * FROM Backup_users WHERE username = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, uname);
            stmt.setString(2, pass);

            ResultSet rs = stmt.executeQuery();
            isValid = rs.next();

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

       if (isValid) {
    RequestDispatcher rd = request.getRequestDispatcher("backup.jsp");
    rd.forward(request, response);
} else {
    request.setAttribute("error", "Login failed. Please try again.");
    RequestDispatcher rd = request.getRequestDispatcher("login.html");
    rd.forward(request, response);
}

    }
}


