public class Plot {
    private Culture culture;
    private Animal animal;


    public boolean isOccupied() {
        return culture != null || animal != null;
    }

    public void plant(Culture culture) {
        this.culture = culture;
    }

    public Culture harvest() {
        Culture harvestedCulture = this.culture;
        this.culture = null;
        return harvestedCulture;
    }


    public void placeAnimal(Animal animal) {
        this.animal = animal;
    }

    public Animal removeAnimal() {
        Animal removedAnimal = this.animal;
        this.animal = null;
        return removedAnimal;
    }

    public Culture getCulture() {
        return culture;
    }

    public Animal getAnimal() {
        return animal;
    }
}