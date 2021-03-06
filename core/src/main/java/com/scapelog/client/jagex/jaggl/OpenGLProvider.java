package com.scapelog.client.jagex.jaggl;

import com.scapelog.api.ClientFeature;
import com.scapelog.api.ClientFeatureStatus;
import com.scapelog.api.event.impl.PaintEvent;
import com.scapelog.api.ui.Overlay;
import com.scapelog.api.ui.RSFont;
import com.scapelog.client.event.EventDispatcher;
import com.scapelog.client.loader.analyser.impl.detours.Detour;
import com.scapelog.client.loader.analyser.impl.detours.Interceptor;
import com.scapelog.client.loader.analyser.impl.detours.TargetType;
import com.scapelog.client.loader.util.ProxyFactory;
import com.scapelog.client.reflection.Reflection;
import com.scapelog.client.ui.UserInterface;
import com.scapelog.client.util.OperatingSystem;
import com.scapelog.util.proguard.Keep;

import java.awt.Canvas;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.lang.invoke.MethodHandle;
import java.util.Hashtable;

@Interceptor("jaggl/OpenGL")
public final class OpenGLProvider {

	public static GL gl;

	private static int overlayTexture;
    public static int width = 500, height = 500;

    private static byte[] VBLANK;
	private static BufferedImage overlayBuffer;
	static Graphics2D overlayGraphics;

	@Keep
	@Detour(type = TargetType.INSTANCE)
    public static long init(Object _this, Canvas paramCanvas, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
	    if (gl == null) {
		    gl = ProxyFactory.getProxyFor(GL.class);
	    }

		if (OperatingSystem.getOperatingSystem() == OperatingSystem.WINDOWS) {
			resizeSurface();
		}

		ClientFeature.OPENGL.setStatus(ClientFeatureStatus.ENABLED);

        try {
            bufferHook(_this);
	        return (Long) init.invoke(_this, paramCanvas, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return 0;
    }

	@Keep
    @Detour(type = TargetType.INSTANCE)
    public static void releaseSurface(Object _this, Canvas paramCanvas, long paramLong) {
	    overlayBuffer = null;
	    overlayGraphics = null;
        bufferHook(_this);
        try {
            releaseSurface.invoke(_this, paramCanvas, paramLong);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

	@Keep
    @Detour(type = TargetType.INSTANCE)
    public static void surfaceResized(Object _this, long paramLong) {
        resizeSurface();
        bufferHook(_this);
        try {
            surfaceResized.invoke(_this, paramLong);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private static void resizeSurface() {
	    int[] viewport = new int[4];
        gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);

        width = viewport[2];
        height = viewport[3];
        VBLANK = new byte[width * height * 4];
    }

    static MethodHandle swapBuffers;
    static MethodHandle surfaceResized;
    static MethodHandle init;
    static MethodHandle releaseSurface;
    static int thisCanvas;

    private static void bufferHook(Object _this) {
        if (!(_this.hashCode() != thisCanvas || swapBuffers == null || surfaceResized == null || init == null || releaseSurface==null))
            return;
        thisCanvas = _this.hashCode();
        swapBuffers = Reflection.declaredMethod("swapBuffers").in(_this).withParameters(long.class).handle();
        releaseSurface = Reflection.declaredMethod("releaseSurface").in(_this).withParameters(Canvas.class, long.class).handle();
        surfaceResized = Reflection.declaredMethod("surfaceResized").in(_this).withParameters(long.class).handle();
        init = Reflection.declaredMethod("init").in(_this).withReturnType(Long.class).withParameters(Canvas.class, int.class, int.class, int.class, int.class, int.class, int.class).handle();
    }

	@Keep
    @Detour(type = TargetType.INSTANCE)
    public static void swapBuffers(Object _this, long paramLong) {
	    gl.glEnable(GL.GL_TEXTURE_2D);
        if (overlayBuffer == null) {
            int[] t = new int[1];
            gl.glGenTextures(1, t, 0);
            overlayTexture = t[0];
            gl.glBindTexture(GL.GL_TEXTURE_2D, overlayTexture);
            gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
            gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
        } else {
	        gl.glBindTexture(GL.GL_TEXTURE_2D, overlayTexture);
        }
        if ((overlayBuffer == null || overlayBuffer.getWidth() != width || overlayBuffer.getHeight() != height) && width > 0 && height > 0) {
            WritableRaster raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, width, height, 4, null);
	        overlayBuffer = new BufferedImage(
			        new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB), new int[]{8, 8, 8, 8}, true, false, ComponentColorModel.TRANSLUCENT, DataBuffer.TYPE_BYTE),
			        raster,
			        false,
			        new Hashtable());
	        overlayGraphics = overlayBuffer.createGraphics();
	        overlayGraphics.setClip(0, 0, width, height);
	        overlayGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	        overlayGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        }
	    try {
		    byte[] buffer = ((DataBufferByte) overlayBuffer.getRaster().getDataBuffer()).getData();
		    System.arraycopy(VBLANK, 0, buffer, 0, VBLANK.length);
		    try {
			    Font font = overlayGraphics.getFont();
			    overlayGraphics.setFont(RSFont.GRAVESTONE.getFont());
			    overlayGraphics.drawString("OpenGL", 10, 15);
			    overlayGraphics.setFont(font);

			    EventDispatcher.fireEvent(new PaintEvent(overlayGraphics, width, height));
			    for (Overlay overlay : UserInterface.getOverlays()) {
				    if (!overlay.isVisible()) {
					    continue;
				    }
				    BufferedImage bufferedImage = overlay.getBufferedImage();
				    Graphics2D graphics = (Graphics2D) bufferedImage.getGraphics();
				    overlay.paint(graphics);

				    overlayGraphics.drawImage(bufferedImage, overlay.getX(), overlay.getY(), null);
			    }
		    } catch (Throwable error) {
			    System.err.println("An error occurred while painting:");
			    error.printStackTrace();
		    }
		    gl.glTexImage2Dub(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, width, height, 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, buffer, 0);
		    gl.glColor4f(1, 1, 1, 1);
		    gl.glBegin(GL.GL_QUADS);
		    gl.glTexCoord2f(0, 0);
		    gl.glVertex2f(0, 0);
		    gl.glTexCoord2f(0, 1);
		    gl.glVertex2f(0, height);
		    gl.glTexCoord2f(1, 1);
		    gl.glVertex2f(width, height);
		    gl.glTexCoord2f(1, 0);
		    gl.glVertex2f(width, 0);
		    gl.glEnd();
		    bufferHook(_this);
	    } catch (Exception e) {
		    System.err.println("An error occurred while painting:");
		    e.printStackTrace();
	    }
        try {
            swapBuffers.invoke(_this, paramLong);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}