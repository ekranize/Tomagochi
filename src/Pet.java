public class Pet {
    private String name;
    private String favEat;
    private String favDrink;
    private int id;
    public Pet (int id, String name, String favEat, String favDrink) {
        this.id = id;
        this.name = name;
        this.favEat = favEat;
        this.favDrink = favDrink;
    }
    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
}
