package com.mikael.wallpaper;

import android.graphics.Canvas;
import android.graphics.Color;
import android.view.SurfaceHolder;
import android.service.wallpaper.WallpaperService;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.view.MotionEvent;
import android.view.View;
import android.os.Handler;
import android.os.Looper;

public class WebWallpaperService extends WallpaperService {

    @Override
    public Engine onCreateEngine() {
        return new WebWallpaperEngine();
    }

    private class WebWallpaperEngine extends Engine {
        private WebView mWebView;
        private final Handler mHandler = new Handler(Looper.getMainLooper());
        private boolean mVisible = false;

        private final Runnable mDrawRunner = new Runnable() {
            @Override
            public void run() {
                drawFrame();
                if (mVisible) {
                    mHandler.postDelayed(this, 16);
                }
            }
        };

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            setTouchEventsEnabled(true);

            mWebView = new WebView(getApplicationContext());
            mWebView.getSettings().setJavaScriptEnabled(true);
            mWebView.getSettings().setDomStorageEnabled(true);
            mWebView.getSettings().setLoadsImagesAutomatically(true);
            
            // Configuration de l'affichage et du focus immédiat
            mWebView.setBackgroundColor(Color.TRANSPARENT);
            mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            mWebView.setFocusable(true);
            mWebView.setFocusableInTouchMode(true);
            mWebView.requestFocus();
            
            mWebView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    updateViewLayout(getSurfaceHolder());
                }
            });
            
            mWebView.loadUrl("https://mikaelguegan-stack.github.io/wallpaper/");
        }

        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);
            updateViewLayout(holder);
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
            if (width > 0 && height > 0) {
                mWebView.measure(
                    View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY)
                );
                mWebView.layout(0, 0, width, height);
            }
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
            drawFrame();
        }

        private void drawFrame() {
            SurfaceHolder holder = getSurfaceHolder();
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
            mVisible = visible;
            super.onVisibilityChanged(visible);
            if (visible) {
                mWebView.onResume();
                mWebView.requestFocus(); // Réattribue le focus dès que l'écran s'affiche
                updateViewLayout(getSurfaceHolder());
                mHandler.post(mDrawRunner);
            } else {
                mWebView.onPause();
                mHandler.removeCallbacks(mDrawRunner);
            }
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            mHandler.removeCallbacks(mDrawRunner);
            if (mWebView != null) {
                mWebView.destroy();
            }
        }

        @Override
        public void onTouchEvent(MotionEvent event) {
            super.onTouchEvent(event);
            mWebView.dispatchTouchEvent(event);
            drawFrame();
        }
    }
}
