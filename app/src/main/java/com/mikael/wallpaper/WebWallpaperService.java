package com.mikael.wallpaper;

import android.graphics.Canvas;
import android.os.Handler;
import android.os.Looper;
import android.view.SurfaceHolder;
import android.service.wallpaper.WallpaperService;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.view.MotionEvent;

public class WebWallpaperService extends WallpaperService {

    @Override
    public Engine onCreateEngine() {
        return new WebWallpaperEngine();
    }

    private class WebWallpaperEngine extends Engine {
        private WebView mWebView;
        private final Handler mHandler = new Handler(Looper.getMainLooper());

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            setTouchEventsEnabled(true);

            mHandler.post(() -> {
                mWebView = new WebView(getApplicationContext());
                mWebView.getSettings().setJavaScriptEnabled(true);
                mWebView.getSettings().setDomStorageEnabled(true);
                mWebView.setWebViewClient(new WebViewClient());
                mWebView.loadUrl("https://example.com");
            });
        }

        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);
            mHandler.post(() -> {
                if (mWebView != null) {
                    mWebView.layout(0, 0, getSurfaceHolder().getSurfaceFrame().width(), getSurfaceHolder().getSurfaceFrame().height());
                    Canvas canvas = holder.lockCanvas();
                    if (canvas != null) {
                        mWebView.draw(canvas);
                        holder.unlockCanvasAndPost(canvas);
                    }
                }
            });
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            mHandler.post(() -> {
                if (mWebView != null) {
                    if (visible) {
                        mWebView.onResume();
                    } else {
                        mWebView.onPause();
                    }
                }
            });
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            mHandler.post(() -> {
                if (mWebView != null) {
                    mWebView.destroy();
                    mWebView = null;
                }
            });
        }

        @Override
        public void onTouchEvent(MotionEvent event) {
            super.onTouchEvent(event);
            mHandler.post(() -> {
                if (mWebView != null) {
                    mWebView.onTouchEvent(event);
                }
            });
        }
    }
}
