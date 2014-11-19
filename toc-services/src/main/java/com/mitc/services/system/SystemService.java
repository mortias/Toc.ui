package com.mitc.services.system;

import com.mitc.services.vertx.VertxService;
import com.mitc.services.vertx.resources.Channel;
import com.mitc.config.Settings;
import com.sun.management.OperatingSystemMXBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.vertx.java.core.json.JsonObject;

import java.lang.management.ManagementFactory;
import java.util.concurrent.Executor;

@Component("SystemService")
public class SystemService implements Executor {

    @Autowired
    Settings settings;

    @Autowired
    VertxService vertxService;

    public SystemService() {
    }

    public void init() {
        execute(new SystemServer());
    }

    @Override
    public void execute(Runnable r) {
        new Thread(r).start();
    }

    // embedded server class
    private class SystemServer implements Runnable {

        private long frequency = 2000;
        private OperatingSystemMXBean operatingSystemMXBean;

        public SystemServer() {
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
            vertxService.sendMessage(Channel.BO_READ_CHANNEL.getName(), replyMsg);
        }
    }
}
