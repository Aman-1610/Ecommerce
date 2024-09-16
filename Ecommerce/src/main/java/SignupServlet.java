import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/signup")
public class SignupServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String address = request.getParameter("address");

        // JDBC connection
        String jdbcUrl = "jdbc:mysql://localhost:3306/ecommerce_db";
        String jdbcUser = "root";  // Replace with your DB username
        String jdbcPassword = "root";  // Replace with your DB password
           
        try {
        // Load the MySQL JDBC driver
            Class.forName("com.mysql.jdbc.Driver");

            // Connect to the database
            Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPassword);


            // Check if the email already exists
            String query = "INSERT INTO Users (name, email, password, address) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, name);
            statement.setString(2, email);
            statement.setString(3, password); // In real-world apps, use bcrypt for password hashing
            statement.setString(4, address);

            int rowsInserted = statement.executeUpdate();
            PrintWriter out = response.getWriter();
            if (rowsInserted > 0) {
                out.println("Registration successful! Your customer ID is: " + statement.getGeneratedKeys());
            } else {
                out.println("Registration failed. Try again.");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
