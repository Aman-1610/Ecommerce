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

public class GetCartServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int userId = Integer.parseInt(request.getParameter("user_id"));

        String jdbcUrl = "jdbc:mysql://localhost:3306/ecommerce_db";
        String jdbcUser = "root";
        String jdbcPassword = "root";

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPassword);
            String sql = "SELECT p.id, p.name, p.description, p.price, p.category, p.stock FROM carts c JOIN products p ON c.product_id = p.id WHERE c.user_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);

            ResultSet resultSet = statement.executeQuery();
            PrintWriter out = response.getWriter();
            out.println("<html><body><h2>Cart Details</h2><ul>");
            while (resultSet.next()) {
                out.println("<li>" + 
                    "ID: " + resultSet.getInt("id") + ", " +
                    "Name: " + resultSet.getString("name") + ", " +
                    "Description: " + resultSet.getString("description") + ", " +
                    "Price: " + resultSet.getDouble("price") + ", " +
                    "Category: " + resultSet.getString("category") + ", " +
                    "Stock: " + resultSet.getInt("stock") +
                    "</li>"
                );
            }
            out.println("</ul></body></html>");

            resultSet.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
