public class Player {
    private int money;

    public Player (int startingMoney){
        this.money = startingMoney;
    }

    public int getMoney(){
        return this.money;
    }

    public boolean spendMoney(int amount){
        if(money >= amount){
            this.money -= amount;
            return true;
        }
        return false;
    }
    public void earnMoney(int amount){
        this.money += amount;
    }
}
