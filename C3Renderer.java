package com.TQFramework;

public class C3Renderer implements C3SurfaceView.Renderer {
	private String sResourcePath;
	private static boolean s_bFlag = true;
	private static int s_nW=0;
	private static int s_nH=0;

	// @Override
	public void SetResPath(String sResPath) {
		sResourcePath = sResPath;
	}

	public void onSurfaceCreated(int w, int h) {
		if (s_bFlag)
		{
			if(w<h)
			{
				int nTmp = w;
				w = h;
				h = nTmp;
			}
			s_nW = w;
			s_nH = h;
			s_bFlag = false;
		}
		InitC3Engine(s_nW, s_nH, 0x8034, false, sResourcePath);

	}

	public void onSurfaceChanged(int w, int h) {
	//	C3SurfaceResize(w, h);
	}

	public void onFinish() {
		DestroyC3Engine();
	}

	public void onDrawFrame() {
		C3SurfaceRender();
	}

	public void SetEditTextResult(String text) {
		nativeSetEditTextResult(text);
	}

	public native static int InitC3Engine(int nBackBufferWidth,
			int nBackBufferHeight, int nPixelFormat, boolean bHasStencilBuffer,
			String strResourcePath);

	public native static void C3SurfaceResize(int w, int h);

	public native static void C3SurfaceRender();

	public native static void DestroyC3Engine();

	public native static void nativeSetEditTextResult(String text);

}
