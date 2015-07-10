package jagex.jaclib;

import simpleclient.client.hook.Proxy;

/**
 * Proxy for hardware info data.
 */
@Proxy("jaclib/hardware_info/HardwareInfo")
public interface HardwareInfo {
    int[] getCPUInfo();

    int[] getRawCPUInfo();

    /**
     * Fetches the DirectX properties.
     * @return The DirectX properties
     */
    String[] getDXDiagSystemProps();

    /**
     * Fetches the DirectX device properties.
     * @return The DirectX device properties
     */
    String[][] getDXDiagDisplayDevicesProps();

    /**
     * Fetches the OpenGL context properties.
     * </p>
     * Should only be called if an OpenGL context is currently active. Otherwise, behaviour is
     * undefined and may lead to an EXCEPTION_ACCESS_VIOLATION in native code.
     * @return The OpenGL context properties
     */
    String[] getOpenGLProps();
}
