public class Product extends Item {
    private String brand;
    private String productType;
    private int stock;

    public Product(int id, String name, String brand, String productType, int stock, double cost) {
        super(id, name, cost);
        this.brand = brand;
        this.productType =  productType;
        this.stock = stock;
    }

    public String getBrand() {
        return brand;
    }

    public String getProductType() {
        return productType;
    }

    public int getStock() {
        return stock;
    }

    @Override
    public void printDetails() {
        System.out.printf("%-8d %-20s %-15s %-12s %-8d %-10.2f%n", id, name, brand, productType, stock, cost);
    }
}
