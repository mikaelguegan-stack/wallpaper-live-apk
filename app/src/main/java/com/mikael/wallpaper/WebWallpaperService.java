package com.mikael.wallpaper;

import android.graphics.Canvas;
import android.graphics.Color;
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

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            setTouchEventsEnabled(true);

            mWebView = new WebView(getApplicationContext());
            mWebView.getSettings().setJavaScriptEnabled(true);
            mWebView.getSettings().setDomStorageEnabled(true);
            mWebView.setWebViewClient(new WebViewClient());
            
            // Remplace "https://example.com" par ton URL personnelle
            mWebView.loadUrl("https://example.com");
        }

        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);
            updateViewLayout(holder);
        }

        private void updateViewLayout(SurfaceHolder holder) {
            int width = holder.getSurfaceFrame().width();
            int height = holder.getSurfaceFrame().height();
            if (width > 0 && height > 0) {
                mWebView.measure(
                    View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY)
                );
                mWebView.layout(0, 0, width, height);
            }
            Canvas canvas = holder.lockCanvas();
            if (canvas != null) {
                try {
                    canvas.drawColor(Color.BLACK);
                    mWebView.draw(canvas);
                } finally {
                    holder.unlockCanvasAndPost(canvas);
                }
            }
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            if (visible) {
                mWebView.onResume();
                updateViewLayout(getSurfaceHolder());
            } else {
                mWebView.onPause();
            }
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            if (mWebView != null) {
                mWebView.destroy();
            }
        }

        @Override
        public void onTouchEvent(MotionEvent event) {
            super.onTouchEvent(event);
            mWebView.onTouchEvent(event);
        }
    }
}
