package jagex.jaclib;

import simpleclient.client.hook.HookManager;

/**
 * Container for low-getLevel system hooks.
 */
public class Hardware {
    private static HardwareInfo hardwareInfo;
    private static QueryPerformanceCounter counter;

    /**
     * Creates a proxy for {@link HardwareInfo}.
     *
     * @return The proxy instance
     */
    public static HardwareInfo getHardwareInfo() {
        return hardwareInfo != null ? hardwareInfo : (hardwareInfo = HookManager.generateProxyFor(HardwareInfo.class));
    }

    /**
     * Creates a proxy for {@link QueryPerformanceCounter}.
     *
     * @return The proxy instance.
     */
    public static QueryPerformanceCounter getCounter() {
        return counter != null ? counter : (counter = HookManager.generateProxyFor(QueryPerformanceCounter.class));
    }
}
