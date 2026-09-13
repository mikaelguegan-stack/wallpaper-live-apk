package com.mikael.wallpaper;

import android.graphics.Canvas;
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
            
            // Remplace par ton URL ou charge un fichier local
            mWebView.loadUrl("https://example.com");
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            if (visible) {
                mWebView.onResume();
            } else {
                mWebView.onPause();
            }
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            mWebView.destroy();
        }

        @Override
        public void onTouchEvent(MotionEvent event) {
            super.onTouchEvent(event);
            mWebView.onTouchEvent(event);
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            super.surfaceCreated(holder);
            mWebView.layout(0, 0, getSurfaceHolder().getSurfaceFrame().width(), getSurfaceHolder().getSurfaceFrame().height());
            Canvas canvas = holder.lockCanvas();
            if (canvas != null) {
                mWebView.draw(canvas);
                holder.unlockCanvasAndPost(canvas);
            }
        }
    }
}
