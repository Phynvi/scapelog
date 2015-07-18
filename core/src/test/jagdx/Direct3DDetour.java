package jagex.jagdx;

import simpleclient.client.hook.Detour;
import simpleclient.client.hook.HookManager;
import simpleclient.client.hook.Interceptor;
import simpleclient.client.hook.jaclib.Hardware;
import simpleclient.client.hook.jagdx.proxy.Direct3DDeviceProxy;
import simpleclient.client.hook.jagdx.proxy.Direct3DProxy;

import java.util.Arrays;

@Interceptor("jagdx/IDirect3D")
public class Direct3DDetour {
    @Detour
    public static long Direct3DCreate() {
        try {
            if (Direct3DHookProvider.d3dDevice == null) Direct3DHookProvider.d3dDevice = HookManager.generateProxyFor(Direct3DDeviceProxy.class);
            if (Direct3DHookProvider.d3d == null) Direct3DHookProvider.d3d = HookManager.generateProxyFor(Direct3DProxy.class);

            Direct3DHookProvider.executor.hookEnabled(Direct3DHookProvider.instance);
            Direct3DHookProvider.isActive = true;
            long ptr = Direct3DHookProvider.d3d.Direct3DCreate();
            System.out.println("DX: " + Arrays.toString(Hardware.getHardwareInfo().getDXDiagSystemProps().clone()));

            return ptr;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0;
    }
}
