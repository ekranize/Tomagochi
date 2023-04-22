import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

public class User {
    private final String name;
    private final int id;
    private HashMap<Integer, Pet> pets = new HashMap<>();
    private HashMap<Good, Integer> goods = new HashMap<>();
    private Pet currentPet;
    //TODO Сделать пользователю пароль
    private HashMap<Good,Integer> inventory;
    public User (int id, String name) {
        this.name=name;
        this.id = id;
    }
    public void setPets(HashMap<Integer, Pet> pets) {
        this.pets = pets;
    }
    public void setInventoryGoods(HashMap<Good, Integer> goods) {
        this.goods = goods;
    }
    public String getName() {
        return name;
    }
    public HashMap<Integer, Pet> getPets() {
        return pets;
    }
    public Pet getCurrentPet() {
        return currentPet;
    }
    public void setCurrentPet(Pet currentPet) {
        this.currentPet = currentPet;
    }
    public void addPet(int id, Pet newPet) {
        this.pets.put(id, newPet);
    }
    public void addGood(Good newGood, int count) {
        this.goods.put(newGood, count);
    }
    public HashMap<Good, Integer> getGoods() {
        return goods;
    }
    public int getId() {
        return id;
    }
}
