package com.aurachaos.rccron;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobDataMap;
import com.velocitypowered.api.proxy.ProxyServer;

public class CommandJob implements Job {
    @Override
    public void execute(JobExecutionContext ctx) {
        JobDataMap map = ctx.getMergedJobDataMap();
        ProxyServer server = (ProxyServer) map.get("proxy");
        String cmd = map.getString("command");
        server.getCommandManager().executeAsync(server.getConsoleCommandSource(), cmd);
    }
}