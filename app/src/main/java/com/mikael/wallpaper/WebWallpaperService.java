package com.mikael.wallpaper;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.service.wallpaper.WallpaperService;

public class WebWallpaperService extends WallpaperService {

    @Override
    public Engine onCreateEngine() {
        return new WebWallpaperEngine();
    }

    private class WebWallpaperEngine extends Engine {
        private final Paint paint = new Paint();

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            paint.setColor(Color.BLUE);
            paint.setTextSize(50);
        }

        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);
            drawWallpaper(holder);
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            if (visible) {
                drawWallpaper(getSurfaceHolder());
            }
        }

        private void drawWallpaper(SurfaceHolder holder) {
            Canvas canvas = holder.lockCanvas();
            if (canvas != null) {
                try {
                    // Fond de couleur pour valider l'affichage instantané
                    canvas.drawColor(Color.BLACK);
                    canvas.drawText("WebWallpaper actif", 50, 150, paint);
                } finally {
                    holder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }
}
