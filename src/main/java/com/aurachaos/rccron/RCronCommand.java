package com.aurachaos.rccron;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.command.SimpleCommand.Invocation;
import com.velocitypowered.api.command.CommandSource;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.Arrays;
import java.util.UUID;

public class RCronCommand implements SimpleCommand {
    private final RCronPlugin plugin;

    public RCronCommand(RCronPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Invocation invocation) {
        String[] args = invocation.arguments();
        CommandSource src = invocation.source();
        if (args.length == 0) {
            sendUsage(src);
            return;
        }
        switch (args[0].toLowerCase()) {
            case "add" -> handleAdd(src, args);
            case "remove" -> handleRemove(src, args);
            case "list" -> handleList(src);
            case "reload" -> handleReload(src);
            default -> sendUsage(src);
        }
    }

    private void handleAdd(CommandSource src, String[] args) {
        if (args.length < 3) {
            src.sendMessage(Component.text("Usage: /rcron add [--server <name>] <cron> <command...>"));
            return;
        }
        String server = null;
        int i = 1;
        if ("--server".equalsIgnoreCase(args[i])) {
            if (args.length < 5) {
                src.sendMessage(Component.text("Usage: /rcron add [--server <name>] <cron> <command...>"));
                return;
            }
            server = args[i + 1];
            i += 2;
        }
        String cron = args[i++];
        String command = String.join(" ", Arrays.copyOfRange(args, i, args.length));
        UUID id = UUID.randomUUID();
        try {
            CronJobBuilder.schedule(plugin, cron, command, id, server);
            ConfigManager.addJob(new ConfigManager.JobEntry(id, cron, command, server));
            src.sendMessage(Component.text("Added job ")
                .append(Component.text(id.toString()).color(NamedTextColor.GREEN)));
        } catch (Exception ex) {
            src.sendMessage(Component.text("Error: " + ex.getMessage()));
        }
    }

    private void handleRemove(CommandSource src, String[] args) {
        if (args.length != 2) {
            src.sendMessage(Component.text("Usage: /rcron remove <jobId>"));
            return;
        }
        try {
            UUID id = UUID.fromString(args[1]);
            boolean unscheduled = plugin.getScheduler().deleteJob(
                org.quartz.JobKey.jobKey("rcron-" + id)
            );
            boolean removed = ConfigManager.removeJob(id);
            if (unscheduled && removed) {
                src.sendMessage(Component.text("Removed job " + id));
            } else {
                src.sendMessage(Component.text("No such job: " + id));
            }
        } catch (Exception ex) {
            src.sendMessage(Component.text("Error: " + ex.getMessage()));
        }
    }

    private void handleList(CommandSource src) {
        try {
            for (ConfigManager.JobEntry e : ConfigManager.getAllJobs()) {
                Component msg = Component.text(e.getId() + " → ")
                    .append(Component.text(e.getCron()).color(NamedTextColor.GOLD))
                    .append(Component.text(" : " + e.getCommand()));
                if (e.getServer() != null) {
                    msg = msg.append(Component.text(" @ " + e.getServer(), NamedTextColor.AQUA));
                }
                src.sendMessage(msg);
            }
        } catch (Exception ex) {
            src.sendMessage(Component.text("Error listing jobs: " + ex.getMessage()));
        }
    }

    private void handleReload(CommandSource src) {
        plugin.reloadAll();
        src.sendMessage(Component.text("RCron config reloaded."));
    }

    private void sendUsage(CommandSource src) {
        src.sendMessage(Component.text("Usage: /rcron <add|remove|list|reload>"));
        src.sendMessage(Component.text("  add [--server <name>] <cron> <command...>"));
        src.sendMessage(Component.text("  remove <jobId>"));
        src.sendMessage(Component.text("  list"));
        src.sendMessage(Component.text("  reload"));
    }
}
