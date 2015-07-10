package jagex.jagdx;

import simpleclient.client.hook.HookManager;
import simpleclient.client.hook.IHookProvider;
import simpleclient.client.hook.jagdx.proxy.Direct3DDeviceProxy;
import simpleclient.client.hook.jagdx.proxy.Direct3DProxy;

public class Direct3DHookProvider implements IHookProvider {
    static {
        HookManager.registerDetour(Direct3DDetour.class);
        HookManager.registerDetour(Direct3DDeviceDetour.class);
    }

    static Direct3DProxy d3d;
    static Direct3DDeviceProxy d3dDevice;
    static HookManager.IHookExecutor executor;
    static Direct3DHookProvider instance;
    static boolean isActive;

    public Direct3DHookProvider() {
        assert instance != null;
        Direct3DHookProvider.instance = this;
    }

    @Override
    public boolean isActive() {
        return isActive;
    }

    @Override
    public void setHookExecutor(HookManager.IHookExecutor executor) {
        Direct3DHookProvider.executor = executor;
    }
}
