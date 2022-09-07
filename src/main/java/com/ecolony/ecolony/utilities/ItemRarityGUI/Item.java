package com.ecolony.ecolony.utilities.ItemRarityGUI;

import org.bukkit.inventory.ItemStack;

public class Item {
    public ItemStack itemStack;
    public int weight;

    public Item(ItemStack is, int weight) {
        this.itemStack = is;
        this.weight = weight;
    }
}
