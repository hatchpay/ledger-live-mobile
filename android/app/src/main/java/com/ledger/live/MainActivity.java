package com.ledger.live;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.facebook.react.ReactFragmentActivity;

import org.devio.rn.splashscreen.SplashScreen;

import com.facebook.react.ReactActivityDelegate;
import com.facebook.react.ReactRootView;
import android.util.Log;
import android.os.Bundle;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.swmansion.gesturehandler.react.RNGestureHandlerEnabledRootView;
import java.util.Locale;


public class MainActivity extends ReactFragmentActivity {

    String importDataString = null;
    /**
     * Returns the name of the main component registered from JavaScript.
     * This is used to schedule rendering of the component.
     */
    @Override
    protected String getMainComponentName() {
        return "ledgerlivemobile";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (!BuildConfig.DEBUG) {
            SplashScreen.show(this, true);
        } else {
            // Allow data override for debug builds
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                this.importDataString = extras.getString("importDataString");
            }
        }
        super.onCreate(savedInstanceState);

        /**
         * Addresses an inconvenient side-effect of using `password-visible`, that allowed styled
         * texts to be pasted (receiver's address for instance) retaining the styles of the source
         * text.
         */
        final ClipboardManager clipboard = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard != null) {
            clipboard.addPrimaryClipChangedListener(new ClipboardManager.OnPrimaryClipChangedListener() {
                boolean breakLoop = false;

                public void onPrimaryClipChanged() {
                    if (breakLoop) {
                        breakLoop = false;
                        return;
                    }
                    if (clipboard.hasPrimaryClip()) {
                        ClipData clipData = clipboard.getPrimaryClip();
                        ClipData.Item item = clipData.getItemAt(0);
                        ClipData clip = ClipData.newPlainText("overriden text", item.coerceToText(MainActivity.this).toString());
                        breakLoop = true;
                        clipboard.setPrimaryClip(clip);
                    }
                }
            });
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE);

        /*
         * Override the detected language to english if it's a RTL language.
         * TODO if we ever support a RTL language we'd have to take it into account here.
         */
        Configuration config = getBaseContext().getResources().getConfiguration();
        if (config.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {
            Locale locale = new Locale("en");
            Locale.setDefault(locale);
            config.setLocale(locale);
            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        }

    }

    @Override
    protected ReactActivityDelegate createReactActivityDelegate() {
        return new ReactActivityDelegate(this, getMainComponentName()) {
            @Override
            protected ReactRootView createRootView() {
                return new RNGestureHandlerEnabledRootView(MainActivity.this);
            }

            @Override
            protected Bundle getLaunchOptions() {
                if(importDataString != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString("importDataString", importDataString);
                    return bundle;
                }else{
                    return new Bundle();
                }
            }
        };
    }
}
