package org.silvius.ethosmoebel;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.silvius.ethosmoebel.EthosMoebel.moebelList;

public class MoebelCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender instanceof Player player) {
            if (args.length > 1) {
                if(args[0].equals("give")){
                    if(moebelList.containsKey(args[1])){
                        moebelList.get(args[1]).giveItem(player);
                        return true;
                    }
                }

            }
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if (args.length == 1){
            return complete(args, List.of("give"));
        }
        if(args[0].equals("give")){
            return complete(args, moebelList.keySet().stream().toList());
        }

        return new ArrayList<>();

    }

    List<String> complete(String @NotNull [] args, List<String> toComplete){
        if (toComplete == null || toComplete.size() == 0) return null;
        final String lastArg = args[args.length - 1];
        List<String> out = new ArrayList<>(List.of());
        for (String completion : toComplete){
            if (lastArg.matches(" *") || completion.toLowerCase(Locale.ROOT).startsWith(lastArg.toLowerCase(Locale.ROOT))) {
                out.add(completion);
            }
        }
        return out;
    }
}
