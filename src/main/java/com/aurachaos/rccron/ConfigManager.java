package com.aurachaos.rccron;

import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

public final class ConfigManager {
    private static final String FILENAME = "config.yml";
    private static YamlConfigurationLoader loader;
    private static ConfigurationNode root;

    public static void init(RCronPlugin plugin) throws IOException, ConfigurateException {
        Path file = plugin.getConfigDir().resolve(FILENAME);
        loader = YamlConfigurationLoader.builder()
                .path(file)
                .build();

        if (Files.notExists(file)) {
            Files.createDirectories(file.getParent());
            root = loader.createNode();
            // bootstrap empty list
            root.node("jobs").setList(JobEntry.class, List.of());
            loader.save(root);
        } else {
            root = loader.load();
        }
    }

    public static void save() throws IOException {
        loader.save(root);
    }

    public static List<JobEntry> getAllJobs() throws ConfigurateException {
        return root.node("jobs").getList(JobEntry.class);
    }

    public static void addJob(JobEntry entry) throws IOException {
        try {
            List<JobEntry> jobs = getAllJobs();
            jobs.add(entry);
            root.node("jobs").setList(JobEntry.class, jobs);
            save();
        } catch (ConfigurateException e) {
            throw new IOException("Could not read existing jobs", e);
        }
    }

    public static boolean removeJob(UUID id) throws IOException {
        try {
            List<JobEntry> jobs = getAllJobs();
            boolean removed = jobs.removeIf(e -> e.getId().equals(id));
            if (removed) {
                root.node("jobs").setList(JobEntry.class, jobs);
                save();
            }
            return removed;
        } catch (ConfigurateException e) {
            throw new IOException("Could not read existing jobs", e);
        }
    }

    @ConfigSerializable
    public static class JobEntry {
        @Setting("id")
        private UUID id;

        @Setting("cron")
        private String cron;

        @Setting("command")
        private String command;

        /** Optional: if null or missing, runs on proxy console */
        @Setting("server")
        private String server;

        public JobEntry() { }

        public JobEntry(UUID id, String cron, String command, String server) {
            this.id = id;
            this.cron = cron;
            this.command = command;
            this.server = server;
        }

        public UUID getId() { return id; }
        public void setId(UUID id) { this.id = id; }

        public String getCron() { return cron; }
        public void setCron(String cron) { this.cron = cron; }

        public String getCommand() { return command; }
        public void setCommand(String command) { this.command = command; }

        public String getServer() { return server; }
        public void setServer(String server) { this.server = server; }
    }
}
