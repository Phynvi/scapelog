package jagex.jaclib;

import simpleclient.client.hook.Proxy;

/**
 * Proxy for a native high-resolution timer.
 */
@Proxy("jaclib/nanotime/QueryPerformanceCounter")
public interface QueryPerformanceCounter {
    /**
     * Fetches the system's current nanotime since the epoch.
     * @return The current nanotime
     */
    long nanoTime();
}
