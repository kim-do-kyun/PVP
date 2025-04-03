package org.desp.pVP.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.desp.pVP.utils.MatchManager;
import org.jetbrains.annotations.NotNull;

public class CancelMatchCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s,
                             @NotNull String[] strings) {
        if (!(commandSender instanceof Player player)) return false;
        String uuid = player.getUniqueId().toString();

        boolean removed = MatchManager.getInstance().cancelQueue(uuid);
        if (removed) {
            player.sendMessage("§c매칭 대기열에서 나갔습니다.");
        } else {
            player.sendMessage("§e당신은 현재 매칭 대기 중이 아닙니다.");
        }
        return true;
    }
}