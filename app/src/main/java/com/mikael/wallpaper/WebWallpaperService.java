
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.SurfaceHolder;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.service.wallpaper.WallpaperService;

public class WebWallpapersService extends WallpaperService {

    @Override
    public Engine onCreateEngine() {
        return new WebWallpaperEngine();
    }

    private class WebWallpaperEngine extends Engine {
        private WebView mWebView;
        private final Handler mHandler = new Handler(Looper.getMainLooper());
        private ValueCallback<Uri[]> mUploadMessage;

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            
            mHandler.post(() -> {
                mWebView = new WebView(getApplicationContext());
                
                WebSettings settings = mWebView.getSettings();
                settings.setJavaScriptEnabled(true);
                settings.setDomStorageEnabled(true);
                settings.setAllowFileAccess(true);
                settings.setLoadsImagesAutomatically(true);
                settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

                // Gestion de l'affichage du sélecteur de fichiers (Galerie)
                mWebView.setWebChromeClient(new WebChromeClient() {
                    @Override
                    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                        if (mUploadMessage != null) {
                            mUploadMessage.onReceiveValue(null);
                        }
                        mUploadMessage = filePathCallback;
                        
                        // Note : Sur un Live Wallpaper pur, l'appel natif d'une Activity externe pour l'intent 
                        // nécessite de passer par une Activity intermédiaire si la galerie refuse de s'ouvrir directement.
                        return true;
                    }
                });

                mWebView.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        view.loadUrl(url);
                        return true;
                    }
                });

                // Chargement direct de ta page GitHub Pages dynamique
                mWebView.loadUrl("https://mikaelguegan-stack.github.io/wallpaper/");
            });
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            if (mWebView != null) {
                if (visible) {
                    mWebView.onResume();
                } else {
                    mWebView.onPause();
                }
            }
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            if (mWebView != null) {
                mHandler.post(() -> {
                    mWebView.stopLoading();
                    mWebView.destroy();
                    mWebView = null;
                });
            }
        }
    }
}
