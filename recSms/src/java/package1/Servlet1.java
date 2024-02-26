/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package package1;

//import java.io.IOException;
//import java.io.PrintWriter;
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.sql.*;
//import com.twilio.twiml.MessagingResponse;
//import com.twilio.twiml.messaging.Body;
//import com.twilio.twiml.messaging.Message;
//import com.twilio.Twilio;
//import javax.servlet.annotation.WebServlet;
//
///**
// *
// * @author sara
// */
//public class Servlet1 extends HttpServlet {
//
//    private static final String ACCOUNT_SID = "AC00a5a55bf7bce79117818a1dc21f24b9";
//    private static final String AUTH_TOKEN = "dfcb0b7eca957ad7a0a60e19cf5ba896";
//
//    static {
//        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
//    }
//
//    
//    
//    
//   protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        String fromNumber = request.getParameter("From");
//        String toNumber = request.getParameter("To");
//        String messageBody = request.getParameter("Body");
//        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
//
//        // JDBC connection parameters
//        String jdbcUrl = "jdbc:postgresql://127.0.0.1:5432/webdev";
//        String dbUsername = "postgres";
//        String dbPassword = "2801";
//
//        try (Connection conn = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword)) {
//            conn.setAutoCommit(false); // Disable autocommit mode
//
//            // Insert SMS information into the database
//            String sql = "INSERT INTO sms_records (to_number, from_number, message, timestamp) VALUES (?, ?, ?, ?)";
//            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
//                pstmt.setString(1, fromNumber);
//                pstmt.setString(2, toNumber);
//                pstmt.setString(3, messageBody);
//                pstmt.setTimestamp(4, timestamp);
//                int rowsInserted = pstmt.executeUpdate();
//
//                if (rowsInserted > 0) {
//                    System.out.println("A new message was inserted successfully.");
//                } else {
//                    System.out.println("Failed to insert the message.");
//                }
//                
//                // Commit the transaction
//                conn.commit();
//            }
//        } catch (SQLException e) {
//            // Log the exception or send an appropriate response to Twilio
//            e.printStackTrace();
//            response.setContentType("text/plain");
//            response.getWriter().write("Error processing the message.");
//            return;
//        }
//
//        // Send response to Twilio
//        response.setContentType("application/xml");
//        try (PrintWriter out = response.getWriter()) {
//            // Construct TwiML response
//            String twimlResponse = "<Response><Message>Thank you for your message!</Message></Response>";
//            out.print(twimlResponse);
//        }
//    }
//
//}
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/sms")
public class Servlet1 extends HttpServlet {

    private static final String ACCOUNT_SID = "your_account_sid";
    private static final String AUTH_TOKEN = "your_auth_token";
    private static final String TWILIO_PHONE_NUMBER = "your_twilio_phone_number";

    static {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String fromNumber = request.getParameter("From");
        String toNumber = request.getParameter("To");
        String messageBody = request.getParameter("Body");
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        // JDBC connection parameters
        String jdbcUrl = "jdbc:postgresql://127.0.0.1:5432/webdev";
        String dbUsername = "postgres";
        String dbPassword = "2801";

        try (Connection conn = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword)) {
            conn.setAutoCommit(false); 

            String sql = "INSERT INTO sms_records (to_number, from_number, message, timestamp) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, toNumber);
                pstmt.setString(2, fromNumber);
                pstmt.setString(3, messageBody);
                pstmt.setTimestamp(4, timestamp);
                int rowsInserted = pstmt.executeUpdate();

                if (rowsInserted > 0) {
                    System.out.println("A new message was inserted successfully.");
                } else {
                    System.out.println("Failed to insert the message.");
                }
                
                conn.commit();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.setContentType("text/plain");
            response.getWriter().write("Error processing the message.");
            return;
        }

        response.setContentType("application/xml");
        try (PrintWriter out = response.getWriter()) {
            String twimlResponse = "<Response><Message>Thank you for your message!</Message></Response>";
            out.print(twimlResponse);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<title>SMS Messages</title>");
        out.println("</head>");
        out.println("<body>");

        out.println("<h2>SMS Messages</h2>");
        out.println("<table border=\"1\">");
        out.println("<tr><th>Sender</th><th>Receiver</th><th>Timestamp</th><th>Body</th></tr>");

        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/webdev", "postgres", "2801");

            PreparedStatement statement = connection.prepareStatement("SELECT * FROM sms_records");
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                out.println("<tr>");
                out.println("<td>" + resultSet.getString("from_number") + "</td>");
                out.println("<td>" + resultSet.getString("to_number") + "</td>");
                out.println("<td>" + resultSet.getTimestamp("timestamp") + "</td>");
                out.println("<td>" + resultSet.getString("message") + "</td>");
                out.println("</tr>");
            }

            connection.close();
        } catch (SQLException | ClassNotFoundException e) {
            out.println("Error fetching SMS messages from database: " + e.getMessage());
            e.printStackTrace(); // Log the exception stack trace
        }

        out.println("</table>");
        out.println("</body>");
        out.println("</html>");
    }
}
