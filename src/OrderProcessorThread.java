import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

public class OrderProcessorThread extends Thread {
    private final Order order;
    private final Map<String, Integer> cart;

    public OrderProcessorThread(Order order, Map<String, Integer> cart) {
        this.order = order;
        this.cart = cart;
    }

    @Override
    public void run() {
        synchronized (OrderProcessorThread.class) {
            try {
                persistOrder();
            } catch (SQLException e) {
                System.err.println("Thread Database Error: " + e.getMessage());
            }
        }
    }

    private void persistOrder() throws SQLException {
        String insertSql = "INSERT INTO orders (OrderNO, CName, `Pname:Qty`, Amt, Date) VALUES (?, ?, ?, ?, ?)";
        String updateStockSql = "UPDATE products SET Stock = Stock - ? WHERE PName = ?";
        try (Connection con = DBConnection.getConnection();
            PreparedStatement pstOrder = con.prepareStatement(insertSql);
            PreparedStatement pstStock = con.prepareStatement(updateStockSql)) {
                pstOrder.setInt(1, order.getOrderNo());
                pstOrder.setString(2, order.getCustomerName());
                pstOrder.setString(3, order.getProductDetails());
                pstOrder.setDouble(4, order.getAmt());
                pstOrder.setString(5, order.getDate());
                pstOrder.executeUpdate();

                for (Map.Entry<String, Integer> entry : cart.entrySet()) {
                    pstStock.setInt(1, entry.getValue());
                    pstStock.setString(2, entry.getKey());
                    pstStock.executeUpdate();
                }
            }
    }
}
