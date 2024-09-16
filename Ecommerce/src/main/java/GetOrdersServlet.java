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

public class GetOrdersServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int userId = Integer.parseInt(request.getParameter("user_id"));

        String jdbcUrl = "jdbc:mysql://localhost:3306/ecommerce_db";
        String jdbcUser = "root";
        String jdbcPassword = "root";

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPassword);
            String sql = "SELECT o.id, o.order_date, o.shipping_address, o.status, p.name, p.price, oi.quantity " +
                         "FROM orders o JOIN order_items oi ON o.id = oi.order_id " +
                         "JOIN products p ON oi.product_id = p.id " +
                         "WHERE o.user_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);

            ResultSet resultSet = statement.executeQuery();
            PrintWriter out = response.getWriter();
            out.println("<html><body><h2>Order Details</h2><ul>");
            while (resultSet.next()) {
                out.println("<li>" +
                    "Order ID: " + resultSet.getInt("id") + ", " +
                    "Order Date: " + resultSet.getTimestamp("order_date") + ", " +
                    "Shipping Address: " + resultSet.getString("shipping_address") + ", " +
                    "Status: " + resultSet.getString("status") + ", " +
                    "Product Name: " + resultSet.getString("name") + ", " +
                    "Price: " + resultSet.getDouble("price") + ", " +
                    "Quantity: " + resultSet.getInt("quantity") +
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
