import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DeleteProductFromCartServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int userId = Integer.parseInt(request.getParameter("user_id"));
        int productId = Integer.parseInt(request.getParameter("product_id"));

        String jdbcUrl = "jdbc:mysql://localhost:3306/ecommerce_db";
        String jdbcUser = "root";
        String jdbcPassword = "root";

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPassword);
            String sql = "DELETE FROM carts WHERE user_id = ? AND product_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);
            statement.setInt(2, productId);

            int rowsDeleted = statement.executeUpdate();
            PrintWriter out = response.getWriter();
            if (rowsDeleted > 0) {
                out.println("<html><body><h3>Product removed from cart successfully!</h3></body></html>");
            } else {
                out.println("<html><body><h3>Failed to remove product from cart.</h3></body></html>");
            }

            statement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
