package com.korotetris.game;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Insets;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.window.OnBackInvokedDispatcher;

public class MainActivity extends Activity {

    private WebView web;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        FrameLayout root = new FrameLayout(this);
        root.setBackgroundColor(Color.rgb(5, 6, 15));

        web = new WebView(this);
        web.setBackgroundColor(Color.rgb(5, 6, 15));
        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setMediaPlaybackRequiresUserGesture(false);
        s.setAllowFileAccess(false);
        s.setAllowContentAccess(false);
        s.setSupportZoom(false);
        web.setVerticalScrollBarEnabled(false);
        web.setHorizontalScrollBarEnabled(false);
        web.setOverScrollMode(WebView.OVER_SCROLL_NEVER);
        web.setWebViewClient(new WebViewClient());
        web.addJavascriptInterface(new Bridge(), "Android");

        root.addView(web, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        setContentView(root);

        // Android 15 dibuja de borde a borde: dejamos espacio para la cámara frontal (cutout)
        root.setOnApplyWindowInsetsListener((v, insets) -> {
            Insets cut = insets.getInsets(WindowInsets.Type.displayCutout());
            v.setPadding(cut.left, cut.top, cut.right, cut.bottom);
            return WindowInsets.CONSUMED;
        });

        hideSystemBars();

        // Botón/gesto "atrás" (API 33+): pausa el juego en lugar de cerrarlo
        getOnBackInvokedDispatcher().registerOnBackInvokedCallback(
                OnBackInvokedDispatcher.PRIORITY_DEFAULT,
                () -> web.evaluateJavascript("window.onAndroidBack && window.onAndroidBack()", null));

        web.loadUrl("file:///android_asset/index.html");
    }

    private void hideSystemBars() {
        getWindow().setDecorFitsSystemWindows(false);
        WindowInsetsController c = getWindow().getInsetsController();
        if (c != null) {
            c.hide(WindowInsets.Type.systemBars());
            c.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) hideSystemBars();
    }

    @Override
    protected void onPause() {
        if (web != null) {
            web.evaluateJavascript("window.onAppPause && window.onAppPause()", null);
            web.onPause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (web != null) {
            web.onResume();
            web.evaluateJavascript("window.onAppResume && window.onAppResume()", null);
        }
    }

    @Override
    protected void onDestroy() {
        if (web != null) {
            web.destroy();
            web = null;
        }
        super.onDestroy();
    }

    /** Funciones disponibles para el juego desde JavaScript como window.Android */
    private class Bridge {
        private SharedPreferences prefs() {
            return getSharedPreferences("koro_tetris", Context.MODE_PRIVATE);
        }

        @JavascriptInterface
        public void vibrate(String pattern) {
            try {
                String[] parts = pattern.split(",");
                long[] timings = new long[parts.length];
                int[] amps = new int[parts.length];
                for (int i = 0; i < parts.length; i++) {
                    timings[i] = Math.max(0, Long.parseLong(parts[i].trim()));
                    // posiciones pares = pausa, impares = vibración (más fuerte al final)
                    amps[i] = (i % 2 == 0) ? 0 : (i == parts.length - 1 ? 255 : 200);
                }
                VibratorManager vm = (VibratorManager) getSystemService(Context.VIBRATOR_MANAGER_SERVICE);
                if (vm == null) return;
                Vibrator v = vm.getDefaultVibrator();
                if (!v.hasVibrator()) return;
                if (v.hasAmplitudeControl()) {
                    v.vibrate(VibrationEffect.createWaveform(timings, amps, -1));
                } else {
                    v.vibrate(VibrationEffect.createWaveform(timings, -1));
                }
            } catch (Exception ignored) {
            }
        }

        @JavascriptInterface
        public String getPref(String key, String def) {
            return prefs().getString(key, def);
        }

        @JavascriptInterface
        public void setPref(String key, String value) {
            prefs().edit().putString(key, value).apply();
        }

        @JavascriptInterface
        public void exitApp() {
            runOnUiThread(() -> MainActivity.this.finish());
        }
    }
}
