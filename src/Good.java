public class Good {
    private String object;
    private int id;
    private int price;
    private int incrCount;
    private String type;
    public Good (int id, String object, int price, int incrCount, String type) {
        this.id = id;
        this.object = object;
        this.price = price;
        this.incrCount = incrCount;
        this.type = type;
    }
    public String getObject() {
        return object;
    }
    public int getIncrCount() {
        return incrCount;
    }
    public int getPrice() {
        return price;
    }
    public int getId() {
        return id;
    }
    public String getType() {
        return type;
    }
}
