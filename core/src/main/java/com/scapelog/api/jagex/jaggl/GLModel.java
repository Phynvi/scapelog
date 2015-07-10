package com.scapelog.api.jagex.jaggl;

public class GLModel  {
    public GLModel() {
    }

    public float x;
    public float y;
    public float z;
    public long stride;
    public long pointer;
    public int screenX;
    public int screenY;
    public long id;
    public int triangleCount;

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    public int getScreenX() {
        return screenX;
    }

    public int getScreenY() {
        return screenY;
    }

    public int getTriangleCount() {
        return triangleCount;
    }

    public long id() {
        return id;
    }
}
