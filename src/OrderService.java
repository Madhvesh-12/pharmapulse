import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

interface Billable {
    double calculateDiscount(double percentage);
    void exportInvoiceToFile(String customerName, double total);
}

public class OrderService implements Billable {
    private final Scanner sc;
    
    public OrderService(Scanner sc) {
        this.sc = sc;
    }

    public static class InsufficientStockException extends Exception {
        public InsufficientStockException(String message) {
            super(message);
        }
    }

    public void displayOrders() {
        System.out.println("-".repeat(40) + " ORDER HISTORY " + "-".repeat(40));
        String sql = "SELECT OrderNo, CName, `Pname:Qty`, Amt, Date FROM orders";

        List<Order> orderList = DBConnection.executeQuery(con-> {
            List<Order> list = new ArrayList<>();
            try (PreparedStatement pst = con.prepareStatement(sql);
                ResultSet rs = pst.executeQuery()) {
                    while (rs.next()) {
                        list.add(new Order(
                            rs.getInt("OrderNo"),
                            rs.getString("CName"),
                            rs.getString("Pname:QTY"),
                            rs.getDouble("Amt"),
                            rs.getString("Date")
                        ));
                    }
            }
            return list;
        }, new ArrayList<>());

        System.out.printf("%-10s %-20s %-30s %-10s %-15s%n", "OrderNO", "CName", "Pname & Qty", "Amt", "Date");
        System.out.println("-".repeat(100));

        for(Order o: orderList) {
            o.printOrderRow();
        }
        if (orderList.isEmpty()) System.out.println("No orders found.");
        System.out.print("\nPress 0 to Exit: ");
        sc.nextLine();
    }

    public void addOrder() {
        System.out.println("-".repeat(40) + " NEW ORDER ENTRY " + "-".repeat(40));
        String prodSql = "SELECT PName, Cost, Stock FROM products";

        while (true) {
            try {
                System.out.print("Enter Order No.: ");
                int orderNo = Integer.parseInt(sc.nextLine().trim());
                System.out.print("Enter Customer Name: ");
                String cname = sc.nextLine().trim();

                Map<String, Integer> cart = new LinkedHashMap<>();
                StringBuilder details = new StringBuilder();

                while (true) {
                    System.out.print("Enter Product Name (or '0' to finish cart): ");
                    String pname = sc.nextLine().trim();
                    if ("0".equals(pname)) break;
                    System.out.print("Enter Quantity: ");
                    int qty = Integer.parseInt(sc.nextLine().trim());

                    cart.put(pname, cart.getOrDefault(pname,0) + qty);
                    details.append(pname).append(":").append(qty).append(" | ");
                }

                Map<String, Double> prices = new HashMap<>();
                Map<String, Integer> stock = new HashMap<>();

                DBConnection.execute(con -> {
                    try (PreparedStatement pstP = con.prepareStatement(prodSql);
                        ResultSet rs = pstP.executeQuery()) {
                            while (rs.next()) {
                                prices.put(rs.getString("PName"), rs.getDouble("Cost"));
                                stock.put(rs.getString("PName"), rs.getInt("Stock"));
                            }
                        }
                });

                double total = 0.0;
                for (Map.Entry<String, Integer> entry : cart.entrySet()) {
                    String name = entry.getKey();
                    int qty = entry.getValue();

                    if (stock.containsKey(name) && stock.get(name) < qty) {
                        throw new InsufficientStockException("Insufficient inventory available for "+ name);
                    }
                    if (prices.containsKey(name)) {
                        total += prices.get(name) * qty;
                    }
                }

                System.out.print("Enter Date (YYYY-MM-DD): ");
                String date = sc.nextLine().trim();

                Order order = new Order(orderNo, cname, details.toString(), total, date);
                OrderProcessorThread worker = new OrderProcessorThread(order, cart);
                worker.start();
                worker.join();

                PharmaPulseApp.log("SUCCESS", "Order presisted concurrently. Total: INR " + total);
                System.out.println("1. AddMore\n2. Exit");
                System.out.print("Choice: ");
                if ("2".equals(sc.nextLine().trim())) break;
            } catch (InsufficientStockException | InterruptedException | NumberFormatException e) {
                System.err.println("Execution Error: " + e.getMessage());
            }
        }
    }

    public void removeOrder() {
        System.out.println("-".repeat(40) + " CANCEL ORDER " + "-".repeat(40));
        String sql = "DELETE FROM orders WHERE OrderNo = ?";

        while (true) {
            try {
                System.out.print("enter Order NO. to remove: ");
                int orderNo = Integer.parseInt(sc.nextLine().trim());

                DBConnection.execute(con -> {
                    try (PreparedStatement pst = con.prepareStatement(sql)) {
                        pst.setInt(1, orderNo);
                        int count = pst.executeUpdate();
                        System.out.println(count + " Record(s) Deleted");
                    }
                });

                System.out.println("1. Delete More\n 2. Exit");
                System.out.print("Choice: ");
                if ("2".equals(sc.nextLine().trim())) break;
            } catch (NumberFormatException e) {
                System.err.println("Input Error: " + e.getMessage());
            }
        }
    }

    public void genBill() {
        System.out.println("-".repeat(40) + "GENERATE INVOICE " + "-".repeat(40));
        System.out.print("Enter Customer Name: ");
        String cn = sc.nextLine().trim();

        System.out.println("-".repeat(40) + " PHARMAPULSE INVOICE " + "-".repeat(40));
        System.out.println("Customer: " + cn);

        String sql = "SELECT OrderNo, `Pname:Qty`, Amt, Date FROM orders WHERE CName = ?";
        double totalSum = DBConnection.executeQuery(con -> {
            double sum = 0.0;
            try (PreparedStatement pst = con.prepareStatement(sql)) {
                pst.setString(1, cn);
                try (ResultSet rs = pst.executeQuery()) {
                    System.out.printf("%-10s %-35s %-10s %-15s%n", "OrderNO.", "Product & Quantity", "Amount", "Date");
                    System.out.println("-".repeat(100));
                    boolean found = false;
                    while (rs.next()) {
                        found = true;
                        double amt = rs.getDouble("Amt");
                        sum += amt;
                        System.out.printf("%-10d %35s %-10.2f %-15s%n",
                            rs.getInt("OrderNo"),
                            rs.getString("Pname:Qty"),
                            amt,
                            rs.getString("Date")
                        );
                    }
                    
                    if (!found) {
                        System.out.println("NO matching orders found for: " + cn);
                    }
                }
            }
            return sum;
        }, 0.0);

        if (totalSum > 0.0) {
            System.out.printf("\nGrand Total: INR %.2f%n", totalSum);
            exportInvoiceToFile(cn, totalSum);
        }
        System.out.print("\nPress 0 to Exit: ");
        sc.nextLine();
    }

    @Override 
    public double calculateDiscount( double percentage) {
        return percentage /100.0;
    }

    @Override 
    public void exportInvoiceToFile(String customerName, double total) {
        String filename = "invoice_" + customerName.replaceAll("\\s+", "_") + ".txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write("=".repeat(40) + " PHARMAPULSE MANAGEMENT SYSTEM " + "=".repeat(40));
            writer.write("Customer Name: " + customerName + "\n");
            writer.write(String.format("Grand Total   : INR %.2f\n", total));
            writer.write("Payment Status: Completed / Settled\n");
            writer.write("=".repeat(60));
            PharmaPulseApp.log("I/O STREAM", "Physical receipt generated: " + filename);
        } catch (IOException e) {
            System.err.println("File Stream Error: " + e.getMessage());
        }
    }
}
