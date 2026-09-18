import java. util.Scanner;

public class PharmaPulseApp {
    private static final Scanner sc = new Scanner(System.in);
    private static final ProductService productService = new ProductService(sc);
    private static final OrderService orderService = new OrderService(sc);

    public static void main(String[] args) {
        signin();
    }

    public static void log(String message) {
        System.out.println(">> " + message);
    }

    public static void log(String level, String message) {
        System.out.println(">> [" + level + "] " + message);
    }

    private static void signin() {
        while (true) {
            System.out.print("Enter Admin Access Code: ");
            String pass = sc.nextLine().trim();
            if("pp1234".equals(pass)) {
                System.out.println("=".repeat(40) + " PHARMAPULSE SYSTEM " + "=". repeat(40));
                mainMenu();
                break;
            } else {
                System.out.println("Invalid Access Code. Access Denied.\n");
            }
        }
    }

    private static void mainMenu() {
        while (true) {
            System.out.println("-".repeat(40) + " MAIN MENU " + "-".repeat(40));
            System.out.println("1. Product Inventory");
            System.out.println("2. Orders & Billing");
            System.out.println("0. Terminate Session\n");
            System.out.print("Enter Your Choice: ");

            String input = sc.nextLine().trim();
            switch (input) {
                case "1":
                    productMenu();
                    break;
                case "2":
                    orderMenu();
                    break;
                case "0":
                    log("INFO", "Terminating Session...");
                    System.exit(0);
                    break;
                default:
                    System.out.println("\n!! Invalid Selection !!\n");
                    break;
            }
        }
    }

    private static void productMenu() {
        while (true) {
            System.out.println("-".repeat(40) + " INVENTORY MENU " + "-".repeat(40));
            System.out.println("1. View Products");
            System.out.println("2. Add Products");
            System.out.println("3. Remove Products");
            System.out.println("4. Update Products");
            System.out.println("0. Return to Main Menu\n");
            System.out.print("Enter Yout Choice: ");

            String ch = sc.nextLine().trim();
            switch (ch) {
                case "1":
                    productService.displayProducts();
                    break;
                case "2":
                    productService.addProduct();
                    break;
                case "3":
                    productService.removeProduct();
                    break;
                case "4":
                    productService.updateProduct();
                    break;
                case "0":
                    return;
                default:
                    System.out.println("\n!! Invalid Selection !!\n");
                    break;
            }
        }
    }

    private static void orderMenu() {
        while (true) {
            System.out.println("-".repeat(40) + " ORDERS & BILLING " + "-".repeat(40));
            System.out.println("1. View Orders");
            System.out.println("2. Create New Order");
            System.out.println("3. Cancel Order");
            System.out.println("4. Generate Customer Invoice: ");
            System.out.println("0. Return to Main Menu\n");
            System.out.print("Enter Your Choice: ");

            String ch = sc.nextLine().trim();
            switch(ch) {
                case "1":
                    orderService.displayOrders();
                    break;
                case "2":
                    orderService.addOrder();
                    break;
                case "3":
                    orderService.removeOrder();
                    break;
                case "4":
                    orderService.genBill();
                    break;
                case "0":
                    return;
                default:
                    System.out.println("\n!! Invalid Selectin !!\n");
            }
        }
    }
}
