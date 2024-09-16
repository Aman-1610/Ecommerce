import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/addproduct")
public class AddProductServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Get product details from the request
        String name = request.getParameter("name");
        String description = request.getParameter("description");
        String priceStr = request.getParameter("price");
        String category = request.getParameter("category");
        String stockStr = request.getParameter("stock_quantity");
        // Log the parameters to check if they are received
        System.out.println("name: " + name);
        System.out.println("description: " + description);
        System.out.println("price: " + priceStr);
        System.out.println("category: " + category);
        System.out.println("stock_quantity: " + stockStr);
        // Check for missing parameters
        if (name == null || description == null || priceStr == null || category == null || stockStr == null ||
            name.trim().isEmpty() || description.trim().isEmpty() || priceStr.trim().isEmpty() || 
            category.trim().isEmpty() || stockStr.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"One or more required parameters are missing.\"}");
            return;
        }

        try {
            // Convert price and stock_quantity to their proper data types
            double price = Double.parseDouble(priceStr);
            int stockQuantity = Integer.parseInt(stockStr);

            // Establish database connection (replace with your credentials)
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/ecommerce_platform", "root", "root");

            // SQL query to insert product
            String query = "INSERT INTO Products (name, description, price, category, stock_quantity) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, name);
            stmt.setString(2, description);
            stmt.setDouble(3, price);
            stmt.setString(4, category);
            stmt.setInt(5, stockQuantity);

            // Execute the update
            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("{\"message\":\"Product added successfully.\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("{\"error\":\"Failed to add product.\"}");
            }

            // Close resources
            stmt.close();
            conn.close();

        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"Invalid data format for price or stock quantity.\"}");
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"An internal error occurred.\"}");
        }
    }
}
