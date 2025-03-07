import java.util.ArrayList;
import java.util.List;

public class GameState {
    private int money;
    private List<Culture> crops;
    private List<Animal> animals;

    public GameState() {
        this.money = 500;
        this.crops = new ArrayList<>();
        this.animals = new ArrayList<>();
    }

    public GameState(int money, List<Culture> crops, List<Animal> animals) {
        this.money = money;
        this.crops = (crops != null) ? crops : new ArrayList<>();
        this.animals = (animals != null) ? animals : new ArrayList<>();
    }




}
