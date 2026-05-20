package com.example.backup;

import java.io.*;
import java.sql.*;
import java.util.Properties;
import jakarta.servlet.*;
import jakarta.servlet.http.*;


public class SignupServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        RequestDispatcher rd = request.getRequestDispatcher("signup.html");
        rd.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirm_password");

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        if (!password.equals(confirmPassword)) {
            out.println("<h3>Passwords do not match!</h3>");
            out.println("<a href='signup.html'>Try again</a>");
            return;
        }
        Properties prop = new Properties();
InputStream input = getServletContext().getResourceAsStream("/WEB-INF/db.properties");
prop.load(input);

String jdbcURL = prop.getProperty("jdbc.url");
String dbUser = prop.getProperty("jdbc.username");
String dbPass = prop.getProperty("jdbc.password");


        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(jdbcURL, dbUser, dbPass);

            // Check if username or email already exists
            String checkSql = "SELECT * FROM Backup_users  WHERE username = ? OR email = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, username);
            checkStmt.setString(2, email);

            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                out.println("<h3>Username or Email already exists!</h3>");
                out.println("<a href='signup.html'>Try again</a>");
                conn.close();
                return;
            }

            String sql = "INSERT INTO Backup_users  (username, email, password) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, email);
            stmt.setString(3, password);  

            int rowsInserted = stmt.executeUpdate();
            conn.close();

            if (rowsInserted > 0) {
                out.println("<h3>Registration successful!</h3>");
                out.println("<a href='login.html'>Login here</a>");
            } else {
                out.println("<h3>Registration failed. Please try again.</h3>");
                out.println("<a href='signup.html'>Try again</a>");
            }

        } catch (Exception e) {
            e.printStackTrace(out);
        }
    }
}
