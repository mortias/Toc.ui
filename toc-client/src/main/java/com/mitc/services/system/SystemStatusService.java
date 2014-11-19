package com.mitc.services.system;

import com.mitc.services.vertx.VertxService;
import com.mitc.services.vertx.resources.Channel;
import com.mitc.toc.Settings;
import com.sun.management.OperatingSystemMXBean;
import org.springframework.stereotype.Component;
import org.vertx.java.core.json.JsonObject;

import java.lang.management.ManagementFactory;
import java.util.concurrent.Executor;

@Component("SystemStatusService")
public class SystemStatusService implements Executor {

    private SystemStatusServer systemStatusServer;

    public SystemStatusService() {
    }

    public void init(Settings settings) {
        systemStatusServer = new SystemStatusServer(settings);
        execute(systemStatusServer);
    }

    @Override
    public void execute(Runnable r) {
        new Thread(r).start();
    }

    // embedded server class
    private class SystemStatusServer implements Runnable {


        private OperatingSystemMXBean operatingSystemMXBean;
        private long frequency = 2000;

        private Settings settings;

        public SystemStatusServer(Settings settings) {
            this.settings = settings;
            this.operatingSystemMXBean =
                    (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        }

        private void getCpuUsage() {
            try {

                JsonObject newMsg = new JsonObject();
                newMsg.putString("action", "showSystemStatus");
                newMsg.putValue("processCpuTime", operatingSystemMXBean.getProcessCpuTime() / 1000000000.0);
                newMsg.putValue("processCpuLoad", operatingSystemMXBean.getProcessCpuLoad());
                newMsg.putValue("systemCpuLoad", operatingSystemMXBean.getSystemCpuLoad());

                sendMessage(newMsg);

            } catch (Exception err) {
                err.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                while (true) {
                    getCpuUsage();
                    Thread.sleep(1 * frequency);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void sendMessage(JsonObject replyMsg) {
            VertxService.sendMessage(Channel.BO_READ_CHANNEL.getName(), replyMsg);
        }
    }
}
