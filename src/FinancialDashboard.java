import java.util.HashMap;

public class FinancialDashboard {
    private int totalIncome;
    private int totalExpenses;
    private HashMap<String, Integer> salesHistory;
    private HashMap<String, Integer> expensesHistory;

    public FinancialDashboard() {
        this.totalIncome = 0;
        this.totalExpenses = 0;
        this.salesHistory = new HashMap<>();
        this.expensesHistory = new HashMap<>();
    }

    public void addIncome(String source, int amount) {
        totalIncome += amount;
        salesHistory.put(source, salesHistory.getOrDefault(source, 0) + amount);
    }

    public void addExpense(String source, int amount) {
        totalExpenses += amount;
        expensesHistory.put(source, expensesHistory.getOrDefault(source, 0) + amount);
    }

    public int getProfit() {
        return totalIncome - totalExpenses;
    }

    public HashMap<String, Integer> getSalesHistory() {
        return salesHistory;
    }

    public HashMap<String, Integer> getExpensesHistory() {
        return expensesHistory;
    }

    public int getTotalIncome() {
        return totalIncome;
    }

    public int getTotalExpenses() {
        return totalExpenses;
    }
}