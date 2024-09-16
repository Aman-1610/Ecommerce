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

public class PlaceOrderServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int userId = Integer.parseInt(request.getParameter("user_id"));
        String shippingAddress = request.getParameter("shipping_address");

        String jdbcUrl = "jdbc:mysql://localhost:3306/ecommerce_db";
        String jdbcUser = "root";
        String jdbcPassword = "root";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPassword);

            // Begin transaction
            connection.setAutoCommit(false);

            // 1. Insert order into orders table
            String orderSql = "INSERT INTO orders (user_id, shipping_address, status) VALUES (?, ?, 'Pending')";
            PreparedStatement orderStatement = connection.prepareStatement(orderSql, PreparedStatement.RETURN_GENERATED_KEYS);
            orderStatement.setInt(1, userId);
            orderStatement.setString(2, shippingAddress);
            int rowsInserted = orderStatement.executeUpdate(); // This variable checks insertion success
            
            // Check if insertion was successful
            if (rowsInserted > 0) {
                ResultSet generatedKeys = orderStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int orderId = generatedKeys.getInt(1);

                    // 2. Retrieve cart items
                    String cartSql = "SELECT product_id FROM carts WHERE user_id = ?";
                    PreparedStatement cartStatement = connection.prepareStatement(cartSql);
                    cartStatement.setInt(1, userId);
                    ResultSet cartResultSet = cartStatement.executeQuery();

                    // 3. Insert each cart item into order_items table
                    String orderItemsSql = "INSERT INTO order_items (order_id, product_id, quantity) VALUES (?, ?, 1)";
                    PreparedStatement orderItemsStatement = connection.prepareStatement(orderItemsSql);
                    while (cartResultSet.next()) {
                        int productId = cartResultSet.getInt("product_id");
                        orderItemsStatement.setInt(1, orderId);
                        orderItemsStatement.setInt(2, productId);
                        orderItemsStatement.executeUpdate();
                    }

                    // 4. Clear the cart
                    String clearCartSql = "DELETE FROM carts WHERE user_id = ?";
                    PreparedStatement clearCartStatement = connection.prepareStatement(clearCartSql);
                    clearCartStatement.setInt(1, userId);
                    clearCartStatement.executeUpdate();

                    // Commit transaction
                    connection.commit();
                    
                    PrintWriter out = response.getWriter();
                    out.println("<html><body><h3>Order placed successfully! Order ID: " + orderId + "</h3></body></html>");
                } else {
                    connection.rollback();
                    PrintWriter out = response.getWriter();
                    out.println("<html><body><h3>Failed to generate order ID.</h3></body></html>");
                }

                orderStatement.close();
                connection.close();
            } else {
                connection.rollback();
                PrintWriter out = response.getWriter();
                out.println("<html><body><h3>Failed to place order.</h3></body></html>");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
