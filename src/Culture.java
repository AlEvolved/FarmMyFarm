public class Culture {
    private String name;
    private int pricePurchase;
    private int priceSale;
    private long growthTime;
    private long plantedTime;
    private boolean mature;

    public Culture(String name, int pricePurchase, int priceSale, long growthTime) {
        this.name = name;
        this.pricePurchase = pricePurchase;
        this.priceSale = priceSale;
        this.growthTime = growthTime;
        this.plantedTime = 0;
        this.mature = false;
    }

    public String getName() {
        return name;
    }

    public int getPricePurchase() {
        return pricePurchase;
    }

    public int getPriceSell() {
        return priceSale;
    }


    public long getGrowthTime() {
        return growthTime;
    }

    public long getPlantedTime() {
        return plantedTime;
    }


    public boolean isMature() {
        return mature;
    }

    public void setMature(boolean mature) {
        this.mature = mature;
    }

    // Optional plant method
    public void plant() {
        this.plantedTime = System.currentTimeMillis();
    }
}
