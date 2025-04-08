package org.desp.pVP.gui;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
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
        ItemMeta itemMeta = item1.getItemMeta();
        itemMeta.setDisplayName("공격력 증가");
        item1.setItemMeta(itemMeta);
        inventory.setItem(1, item1);

        ItemStack item2 = new ItemStack(Material.BLUE_STAINED_GLASS_PANE);
        ItemMeta itemMeta2 = item2.getItemMeta();
        itemMeta2.setDisplayName("체력 증가");
        item2.setItemMeta(itemMeta2);
        inventory.setItem(3, item2);

        ItemStack item3 = new ItemStack(Material.BLUE_STAINED_GLASS_PANE);
        ItemMeta itemMeta3 = item3.getItemMeta();
        itemMeta3.setDisplayName("이속 증가");
        item3.setItemMeta(itemMeta3);
        inventory.setItem(5, item3);
        return inventory;
    }
}
