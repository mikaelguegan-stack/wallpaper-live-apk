package com.mikael.wallpaper;

import android.service.wallpaper.WallpaperService;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebWallpaperService extends WallpaperService {

    @Override
    public Engine onCreateEngine() {
        return new WebEngine();
    }

    private class WebEngine extends Engine {
        private WebView webView;

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            setTouchEventsEnabled(true);

            webView = new WebView(WebWallpaperService.this);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setDomStorageEnabled(true);
            webView.setWebViewClient(new WebViewClient());
            webView.loadUrl("https://mikaelguegan-stack.github.io/wallpaper/");
        }

        @Override
        public void onTouchEvent(MotionEvent event) {
            if (webView != null) {
                webView.dispatchTouchEvent(event);
            }
            super.onTouchEvent(event);
        }

        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);
            draw();
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
            if (webView != null) {
                webView.layout(0, 0, width, height);
            }
            draw();
        }

        private void draw() {
            SurfaceHolder holder = getSurfaceHolder();
            if (holder != null && webView != null) {
                try {
                    Canvas canvas = holder.lockCanvas();
                    if (canvas != null) {
                        webView.draw(canvas);
                        holder.unlockCanvasAndPost(canvas);
                    }
                } catch (Exception ignored) {}
            }
        }
    }
}
