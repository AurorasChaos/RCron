package com.aurachaos.rccron;

import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import net.kyori.adventure.text.Component;
import org.quartz.Scheduler;
import org.quartz.impl.StdSchedulerFactory;

import javax.inject.Inject;
import java.nio.file.Path;

@Plugin(id = "rccron", name = "RCron", version = "1.0.0")
public class RCronPlugin {
    private final ProxyServer server;
    private final Path configDir;
    private Scheduler scheduler;

    @Inject
    public RCronPlugin(ProxyServer server, @DataDirectory Path configDir) {
        this.server = server;
        this.configDir = configDir;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent ev) {
        try {
            StdSchedulerFactory factory = new StdSchedulerFactory();
            scheduler = factory.getScheduler();
            scheduler.start();

            ConfigManager.init(this);
            for (ConfigManager.JobEntry entry : ConfigManager.getAllJobs()) {
                CronJobBuilder.schedule(this, entry.getCron(), entry.getCommand(), entry.getId());
            }

            server.getCommandManager().register("rcron", new RCronCommand(this));
        } catch (Exception ex) {
            server.getConsoleCommandSource().sendMessage(
                Component.text("RCron failed: " + ex.getMessage())
            );
        }
    }

    public Scheduler getScheduler() {
        return scheduler;
    }
    public ProxyServer getServer() {
        return server;
    }
    public Path getConfigDir() {
        return configDir;
    }

    public void reloadAll() {
        try {
            scheduler.clear();
            ConfigManager.init(this);
            for (ConfigManager.JobEntry entry : ConfigManager.getAllJobs()) {
                CronJobBuilder.schedule(this, entry.getCron(), entry.getCommand(), entry.getId());
            }
            server.getConsoleCommandSource()
                  .sendMessage(Component.text("RCron: Reloaded config and jobs."));
        } catch (Exception ex) {
            server.getConsoleCommandSource()
                  .sendMessage(Component.text("Failed to reload RCron: " + ex.getMessage()));
        }
    }
}