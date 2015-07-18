package jagex.jagdx.proxy;

import simpleclient.client.hook.Proxy;

@Proxy("jagdx/IDirect3D")
public interface Direct3DProxy {
    long Direct3DCreate();
}