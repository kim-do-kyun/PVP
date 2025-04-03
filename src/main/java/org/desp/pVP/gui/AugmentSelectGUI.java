package org.desp.pVP.gui;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.desp.pVP.utils.MatchSession;
import org.jetbrains.annotations.NotNull;

public class AugmentSelectGUI implements InventoryHolder {

    public Inventory inventory;
    @Getter
    public static MatchSession session;

    public AugmentSelectGUI(MatchSession session) {
        AugmentSelectGUI.session = session;
    }

    @Override
    public @NotNull Inventory getInventory() {
        if(this.inventory == null) {
            this.inventory = Bukkit.createInventory(this, 9, "PvP증강 선택");
        }

        ItemStack item1 = new ItemStack(Material.BLUE_STAINED_GLASS_PANE);
        inventory.setItem(1, item1);

        ItemStack item2 = new ItemStack(Material.BLUE_STAINED_GLASS_PANE);
        inventory.setItem(3, item2);

        ItemStack item3 = new ItemStack(Material.BLUE_STAINED_GLASS_PANE);
        inventory.setItem(5, item3);
        return inventory;
    }
}
