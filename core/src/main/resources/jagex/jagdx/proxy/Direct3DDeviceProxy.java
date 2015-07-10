package jagex.jagdx.proxy;

import simpleclient.client.hook.Proxy;

@Proxy("jagdx/IDirect3DDevice")
public interface Direct3DDeviceProxy {
    void Destroy(long ptr);

    int DrawPrimitive(long ptr, int type, int count, int buffer);
}