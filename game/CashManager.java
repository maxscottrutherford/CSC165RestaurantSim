package game;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Manages the player’s cash balance (pizza sales, expenses, etc.).
 */
public class CashManager {
    private double balance;
    private final List<CashListener> listeners = new CopyOnWriteArrayList<>();

    /** 
     * Start with an initial balance (e.g. 0.0 or a float-down payment). 
     */
    public CashManager(double startingBalance) {
        this.balance = startingBalance;
    }

    /** Returns the current cash balance. */
    public double getBalance() {
        return balance;
    }

    /**
     * Adds a sale (or any positive income) to the balance.
     * @param amount must be ≥ 0
     */
    public void addIncome(double amount) {
        if (amount < 0) throw new IllegalArgumentException("Income must be non-negative");
        balance += amount;
        notifyListeners();
    }

    /**
     * Deducts an expense (e.g. costs) from the balance.
     * @param amount must be ≥ 0
     */
    public void deductExpense(double amount) {
        if (amount < 0) throw new IllegalArgumentException("Expense must be non-negative");
        balance -= amount;
        notifyListeners();
    }

    /** Zeroes out the balance (e.g. at shift start/end). */
    public void reset() {
        balance = 0.0;
        notifyListeners();
    }

    /** Register a listener to be notified whenever the balance changes. */
    public void addListener(CashListener listener) {
        listeners.add(listener);
    }

    /** Stop notifying this listener. */
    public void removeListener(CashListener listener) {
        listeners.remove(listener);
    }

    /** Internal: call after any change */
    private void notifyListeners() {
        for (CashListener l: listeners) {
            l.onCashChanged(balance);
        }
    }

    /** Implement this to react to cash changes (e.g. update HUD). */
    public interface CashListener {
        void onCashChanged(double newBalance);
    }
}

