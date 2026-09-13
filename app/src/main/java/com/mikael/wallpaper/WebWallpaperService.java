package com.mikael.wallpaper;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.WindowManager;
import android.service.wallpaper.WallpaperService;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebWallpaperService extends WallpaperService {

    @Override
    public Engine onCreateEngine() {
        return new WebWallpaperEngine();
    }

    private class WebWallpaperEngine extends Engine {
        private WebView mWebView;
        private WindowManager mWindowManager;
        private WindowManager.LayoutParams mParams;
        private final Handler mHandler = new Handler(Looper.getMainLooper());
        private boolean mIsVisible = false;

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            setTouchEventsEnabled(true);
            mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

            mHandler.post(() -> {
                mWebView = new WebView(getApplicationContext());
                mWebView.getSettings().setJavaScriptEnabled(true);
                mWebView.getSettings().setDomStorageEnabled(true);
                mWebView.setWebViewClient(new WebViewClient());
                mWebView.loadUrl("https://example.com");

                mParams = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.TYPE_WALLPAPER,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    PixelFormat.TRANSLUCENT
                );
                mParams.gravity = Gravity.TOP | Gravity.START;
            });
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            mIsVisible = visible;
            mHandler.post(() -> {
                if (mWebView != null) {
                    if (visible) {
                        try {
                            mWindowManager.addView(mWebView, mParams);
                        } catch (Exception ignored) {}
                        mWebView.onResume();
                    } else {
                        mWebView.onPause();
                        try {
                            mWindowManager.removeView(mWebView);
                        } catch (Exception ignored) {}
                    }
                }
            });
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            mHandler.post(() -> {
                if (mWebView != null) {
                    if (mIsVisible) {
                        try {
                            mWindowManager.removeView(mWebView);
                        } catch (Exception ignored) {}
                    }
                    mWebView.destroy();
                    mWebView = null;
                }
            });
        }

        @Override
        public void onTouchEvent(MotionEvent event) {
            super.onTouchEvent(event);
            // Si tu veux que les clics passent à travers, garde FLAG_NOT_TOUCHABLE dans les params,
            // sinon tu peux les transmettre au WebView.
        }
    }
}
