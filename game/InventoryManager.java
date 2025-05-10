package game;

import java.util.*;

public class InventoryManager {
    private Map<String, Integer> inventory;

    public InventoryManager() {
        inventory = new HashMap<>();
        inventory.put("cheese", 3);
        inventory.put("sauce", 2);
        inventory.put("pepperoni", 5);
        inventory.put("mushroom", 10);
    }

    // Adds one unit of the specified ingredient
    public void addItem(String item) {
        if (inventory.containsKey(item)) {
            inventory.put(item, inventory.get(item) + 1);
        }
    }

    // Removes one unit of the specified ingredient
    public boolean removeItem(String item) {
        if (inventory.containsKey(item) && inventory.get(item) > 0) {
            inventory.put(item, inventory.get(item) - 1);
            return true;
        }
        return false;
    }

    // Checks if all required items are available
    public boolean hasIngredients(List<String> required) {
        for (String item : required) {
            if (!inventory.containsKey(item) || inventory.get(item) <= 0)
                return false;
        }
        return true;
    }

    // Consumes required ingredients (only if available)
    public boolean useIngredients(List<String> required) {
        if (!hasIngredients(required)) return false;
        for (String item : required) removeItem(item);
        return true;
    }

    // Returns current inventory state
    public Map<String, Integer> getInventory() {
        return new HashMap<>(inventory);
    }

    // Debug print
    public void printInventory() {
        System.out.println("Current Inventory:");
        for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
}
