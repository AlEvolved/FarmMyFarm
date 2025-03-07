import java.util.ArrayList;
import java.util.List;

public class AnimalPen {
    private List<Animal> animals;

    public AnimalPen() {
        this.animals = new ArrayList<>();
    }

    public boolean addAnimal(Animal animal) {
        return animals.add(animal);
    }

    public boolean feedAnimals(int totalFood) {
        int fedAnimals = 0;
        for (Animal animal : animals) {
            if (animal.feed(totalFood)) {
                fedAnimals++;
            }
        }
        return fedAnimals > 0;
    }

    public List<Animal> getAnimals() {
        return animals;
    }
}
