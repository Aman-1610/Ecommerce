import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class SignInServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Retrieve email and password from the form
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        // JDBC connection
        String jdbcUrl = "jdbc:mysql://localhost:3306/ecommerce_db";
        String jdbcUser = "root";  // Replace with your DB username
        String jdbcPassword = "root";  // Replace with your DB password

        try {
            // Load the MySQL JDBC driver
            Class.forName("com.mysql.jdbc.Driver");

            // Connect to the database
            Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPassword);

            // Prepare SQL query to fetch user by email
            String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, email);
            statement.setString(2, password);

            // Execute the query
            ResultSet resultSet = statement.executeQuery();

            // Check if a user exists with the provided credentials
            if (resultSet.next()) {
                // Credentials are valid, create a session for the user
                HttpSession session = request.getSession();
                session.setAttribute("userId", resultSet.getInt("id"));
                session.setAttribute("userName", resultSet.getString("name"));

                // Redirect to the user's dashboard or a welcome page
                response.sendRedirect("welcome.html");
            } else {
                // Invalid credentials, show an error message
                PrintWriter out = response.getWriter();
                out.println("<html><body><h3>Invalid email or password. Please try again.</h3></body></html>");
            }

            // Close the connection
            resultSet.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
