public class Order {
    private int orderNo;
    private String customerName;
    private String productDetails;
    private double amt;
    private String date;

    public Order(int orderNo, String customerName, String productDetails, double amt, String date) {
        this.orderNo = orderNo;
        this.customerName =customerName;
        this.productDetails = productDetails;
        this.amt = amt;
        this.date = date;
    }

    public int getOrderNo() {
        return orderNo;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getProductDetails() {
        return productDetails;
    }

    public double getAmt() {
        return amt;
    }

    public String getDate() {
        return date;
    }

    public void printOrderRow() {
        System.out.printf("%-10d %-20s %-30s %-10.2f %-15s%n", orderNo, customerName, productDetails, amt, date);
    }
}
