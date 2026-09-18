import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ProductService {
    private final Scanner sc;

    public ProductService(Scanner sc) {
        this. sc = sc;
    }

    public void displayProducts() {
        System.out.println("-".repeat(40) +" PHARMAPULSE INVENTORY " +"-".repeat(40));
        String sql = "SELECT PID, PName, Brand, PType, Stock, Cost FROM products";
        
        List<Product> productList = DBConnection.executeQuery(con -> {
            List<Product> list = new ArrayList<>();
            try (PreparedStatement pst = con.prepareStatement(sql);
                ResultSet rs = pst.executeQuery()) {
                    while (rs.next()) {
                        list.add(new Product(
                            rs.getInt("PID"),
                            rs.getString("PName"),
                            rs.getString("Brand"),
                            rs.getString("PType"),
                            rs.getInt("Stock"),
                            rs.getDouble("Cost")
                        ));
                    }
                }
                return list;
        }, new ArrayList<>());

        System.out.printf("%-8s %-20s %-15s %-12s %-8s %-10s%n", "PID", "PName", "Brand", "PType", "Stock", "Cost");
        System.out.println("-".repeat(100));

        for(Product p : productList) {
            p.printDetails();
        }
        System.out.print("\nPress 0 to exit: ");
        sc.nextLine();
    }

    public void addProduct() {
        System.out.println("-".repeat(40) + " ADD PRODUCT RECORD " + "-".repeat(40));
        String sql = "INSERT INTO products (PID, PName, Brand, PType, Stock, Cost) VALUES (?, ?, ?, ?, ?, ?)";

        while(true) {
            try {
                System.out.print("Enter Product ID: ");
                int pid = Integer.parseInt(sc.nextLine().trim());
                System.out.print("Enter Product Name: ");
                String pname = sc.nextLine().trim();
                System.out.print("Enter Brand Name: ");
                String brand = sc.nextLine().trim();
                System.out.print("Enter Product Type: ");
                String ptype = sc.nextLine().trim();
                System.out.print("Enter Stock: ");
                int stock = Integer.parseInt(sc.nextLine().trim());
                System.out.print("Enter Cost: ");
                double cost = Double.parseDouble(sc.nextLine().trim());

                DBConnection.execute(con -> {
                    try (PreparedStatement pst = con.prepareStatement(sql)){
                        pst.setInt(1, pid);
                        pst.setString(2, pname);
                        pst.setString(3, brand);
                        pst.setString(4, ptype);
                        pst.setInt(5, stock);
                        pst.setDouble(6, cost);
                        pst.executeUpdate();
                        PharmaPulseApp.log("SUCCESS", "Medicine Entry Saved Successfully");
                    }
                });

                System.out.println("1. Add More\n2. Exit");
                System.out.print("Choice: ");
                if("2".equals(sc.nextLine().trim())) break;
            } catch (NumberFormatException e) {
                System.err.println("Input Error: " + e.getMessage());
            }
        }
    }

    public void removeProduct() {
        System.out.println("-".repeat(40) + " REMOVE PRODUCT ENTRY " + "-".repeat(40));
        String sql = "DELETE FROM products WHERE PID = ?";

        while (true) {
            try {
                System.out.print("Enter Product ID to delete: ");
                int pid = Integer.parseInt(sc.nextLine().trim());

                DBConnection.execute(con -> {
                    try (PreparedStatement pst = con.prepareStatement(sql)) {
                        pst.setInt(1, pid);
                        int count = pst.executeUpdate();
                        System.out.println(count + " Record(s) deleted");
                    }
                });

                System.out.println("1. Delete More\n2. Exit");
                System.out.print("Choice: ");
                if ("2".equals(sc.nextLine().trim())) break;
            } catch (NumberFormatException e) {
                System.err.println("Input Error: " + e.getMessage());
            }
        }
    }

    public void updateProduct() {
        System.out.println("-".repeat(40) + " UPDATE PRODUCT ENTRY " + "-".repeat(40));
        String sql = "UPDATE products SET PName = ?, Brand=?, PType=?, Stock=?,Cost=? WHERE PID=?";

        while (true) {
            try {
                System.out.print("Enter Product ID: ");
                int pid = Integer.parseInt(sc.nextLine().trim());
                System.out.print("Enter Product Name: ");
                String pname = sc.nextLine().trim();
                System.out.print("Enter Brand Name: ");
                String brand = sc.nextLine().trim();
                System.out.print("Enter Product Type: ");
                String ptype = sc.nextLine().trim();
                System.out.print("Enter Stock: ");
                int stock = Integer.parseInt(sc.nextLine().trim());
                System.out.print("Enter Cost: ");
                double cost = Double.parseDouble(sc.nextLine().trim());

                DBConnection.execute(con -> {
                    try (PreparedStatement pst = con.prepareStatement(sql)) {
                        pst.setString(1, pname);
                        pst.setString(2, brand);
                        pst.setString(3, ptype);
                        pst.setInt(4, stock);
                        pst.setDouble(5, cost);
                        pst.setInt(6, pid);
                        int count = pst.executeUpdate();
                        System.out.println(count + " Record(s) Updated");
                    }
                });

                System.out.println("1. Update More\n2. Exit");
                System.out.print("Choice: ");
                if ("2".equals(sc.nextLine().trim())) break;
            } catch (NumberFormatException e) {
                System.err.println("Input Error: " + e.getMessage());
            }
        }
    }
}
