package runnable;

import com.sun.management.OperatingSystemMXBean;

import java.lang.management.ManagementFactory;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * SystemStatusReader is a collection of methods to read system status (cpu and memory)
 *
 * @author Andreu Correa Casablanca
 */
public class SystemStatusReader implements Runnable {


    public SystemStatusReader() {

    }

    private void cpuUsage() {

        try {

            OperatingSystemMXBean operatingSystemMXBean = (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

            System.out.println("getProcessCpuTime()" + " = " + operatingSystemMXBean.getProcessCpuTime());
            System.out.println("getProcessCpuLoad()" + " = " + operatingSystemMXBean.getProcessCpuLoad());
            System.out.println("getSystemCpuLoad()" + " = " + operatingSystemMXBean.getSystemCpuLoad());


            for (Method method : operatingSystemMXBean.getClass().getDeclaredMethods()) {
                method.setAccessible(true);
                if (method.getName().startsWith("get")
                        && Modifier.isPublic(method.getModifiers())) {
                    Object value;
                    try {
                        value = method.invoke(operatingSystemMXBean);
                    } catch (Exception e) {
                        value = e;
                    }
                    System.out.println(method.getName() + " = " + value);
                }
            }

        } catch (Exception err) {
            err.printStackTrace();
        }

    }

    @Override
    public void run() {
        try {
            while (true) {
                cpuUsage();
                Thread.sleep(1 * 1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}