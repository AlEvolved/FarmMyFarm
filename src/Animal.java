public class Animal {
    private String name;
    private int buyPrice;
    private int sellPrice;
    private int foodNeeded;
    private String resourceType;
    private int resourceQuantity;
    private boolean hasProduced;

    public Animal(String name, int buyPrice, int sellPrice, int foodNeeded, String resourceType, int resourceQuantity) {
        this.name = name;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.foodNeeded = foodNeeded;
        this.resourceType = resourceType;
        this.resourceQuantity = resourceQuantity;
        this.hasProduced = false;
    }

    public boolean feed(int foodAmount) {
        if (foodAmount >= foodNeeded && !hasProduced) {
            hasProduced = true;
            return true;
        }
        return false;
    }

    public int collectResources() {
        if (hasProduced) {
            hasProduced = false;
            return resourceQuantity;
        }
        return 0;
    }

    public String getName() {
        return name;
    }

    public int getBuyPrice() {
        return buyPrice;
    }

    public int getSellPrice() {
        return sellPrice;
    }

    public String getResourceType() {
        return resourceType;
    }

    public boolean hasProduced() {
        return hasProduced;
    }
}
