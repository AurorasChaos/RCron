package com.aurachaos.rccron;

import com.velocitypowered.api.proxy.ProxyServer;
import org.quartz.*;
import java.util.UUID;

public class CronJobBuilder {
    public static void schedule(RCronPlugin plugin, String cronExpr, String command, UUID jobId) throws SchedulerException {
        JobDataMap data = new JobDataMap();
        data.put("proxy", plugin.getServer());
        data.put("command", command);

        JobDetail job = JobBuilder.newJob(CommandJob.class)
            .withIdentity("rcron-" + jobId)
            .usingJobData(data)
            .build();

        Trigger trigger = TriggerBuilder.newTrigger()
            .withIdentity("rcron-trig-" + jobId)
            .withSchedule(CronScheduleBuilder.cronSchedule(cronExpr))
            .build();

        plugin.getScheduler().scheduleJob(job, trigger);
    }
    
        /**
     * Schedule a new cron-based job. If serverName is non-null, CommandJob
     * will forward the command to that backend server; otherwise it runs on proxy.
     */
    public static void schedule(RCronPlugin plugin,
                                String cronExpr,
                                String command,
                                UUID jobId,
                                String serverName) throws SchedulerException {
        JobDataMap data = new JobDataMap();
        data.put("proxy", plugin.getServer());
        data.put("command", command);
        if (serverName != null) {
            data.put("targetServer", serverName);
        }

        JobDetail job = JobBuilder.newJob(CommandJob.class)
            .withIdentity("rcron-" + jobId)
            .usingJobData(data)
            .build();

        Trigger trigger = TriggerBuilder.newTrigger()
            .withIdentity("rcron-trig-" + jobId)
            .withSchedule(CronScheduleBuilder.cronSchedule(cronExpr))
            .build();

        plugin.getScheduler().scheduleJob(job, trigger);
    }
}