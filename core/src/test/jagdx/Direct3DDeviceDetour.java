package jagex.jagdx;

import simpleclient.client.hook.Detour;
import simpleclient.client.hook.Interceptor;

@Interceptor("jagdx/IDirect3DDevice")
public class Direct3DDeviceDetour {
    @Detour
    public static void Destroy(long ptr) {
        Direct3DHookProvider.d3dDevice.Destroy(ptr);
        Direct3DHookProvider.executor.hookDisabled(Direct3DHookProvider.instance);
        Direct3DHookProvider.isActive = false;
    }

    @Detour
    public static int DrawPrimitive(long ptr, int type, int count, int buffer) {
        System.out.println("Draw primitives!");
        int ret = Direct3DHookProvider.d3dDevice.DrawPrimitive(ptr, type, count, buffer);
        return ret;
    }
}
