public abstract class Item {
    protected int id;
    protected String name;
    protected double cost;

    public Item(int id, String name, double cost) {
        this.id = id;
        this.name = name;
        this.cost = cost;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getCost() {
        return cost;
    }

    public abstract void printDetails();
}

