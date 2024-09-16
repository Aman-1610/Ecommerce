import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UpdateProductServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        String name = request.getParameter("name");
        String description = request.getParameter("description");
        double price = Double.parseDouble(request.getParameter("price"));
        String category = request.getParameter("category");
        int stock = Integer.parseInt(request.getParameter("stock"));

        String jdbcUrl = "jdbc:mysql://localhost:3306/ecommerce_db";
        String jdbcUser = "root";
        String jdbcPassword = "root";

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPassword);
            String sql = "UPDATE products SET name = ?, description = ?, price = ?, category = ?, stock = ? WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, name);
            statement.setString(2, description);
            statement.setDouble(3, price);
            statement.setString(4, category);
            statement.setInt(5, stock);
            statement.setInt(6, id);

            int rowsUpdated = statement.executeUpdate();
            PrintWriter out = response.getWriter();
            if (rowsUpdated > 0) {
                out.println("<html><body><h3>Product updated successfully!</h3></body></html>");
            } else {
                out.println("<html><body><h3>Failed to update product.</h3></body></html>");
            }

            statement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
