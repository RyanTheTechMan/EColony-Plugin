package com.ecolony.ecolony.utilities.ItemRarityGUI;

import com.ecolony.ecolony.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ItemRarityGUI implements Listener {
    private List<Item> items;
    private Inventory displayedInventory;

    /**
     * Should only be called for listener.
     */
    public ItemRarityGUI() {}

    public ItemRarityGUI(/List<Item> items) {
        this.items = items;
        build();
    }
    
    @EventHandler
    public void onClick(InventoryClickEvent e){
        if (e.getWhoClicked() instanceof Player p) {
            e.setCancelled(true);

            ItemStack clickedItem = e.getCurrentItem();
            int pos = e.getSlot();
            p.sendMessage("Clicked Slot " + pos);
            Item item = items.get(pos);
            p.sendMessage("Clicked Slot " + pos + item.itemStack.getType());
            if (e.isLeftClick()){
                item.weight += 1;
            } else if (e.isRightClick()) {
                item.weight -= 1;
            }
            p.sendMessage("Weight is now " + item.weight);

        }
    }

    private void build() {
        displayedInventory = Main.instance.getServer().createInventory(null, InventoryType.CHEST, "Item Config");
        items.forEach(i -> displayedInventory.addItem(i.itemStack));
    }

    /**
     * @param player player to show the GUI to
     */
    public void display(Player player) {
        player.openInventory(displayedInventory);
    }

    /**
     * @param players players to show the GUI to
     */
    public void display(List<Player> players) {
        players.forEach(this::display);
    }



    /**
     * @param configurationSection Pass in a {@link ConfigurationSection} that contains ids with weight and itemStack
     * @return a list of {@link com.ecolony.ecolony.utilities.ItemRarityGUI.Item}
     */
    public static List<Item> readConfig(@NotNull ConfigurationSection configurationSection) {
        List<Item> items = new ArrayList<>();
        String[] ids = configurationSection.getKeys(false).toArray(new String[0]);
        for (String id : ids) {
            items.add(new Item(configurationSection.getItemStack(id + ".ItemStack"), configurationSection.getInt(id + ".Weight")));
        }

        return items;
    }

    public static void writeConfig(@NotNull ConfigurationSection configurationSection) {
    }
}
