package com.android.settings.nfc;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.UserManager;
import android.text.TextUtils;
import android.util.Log;

import androidx.preference.Preference;

import com.android.settings.Utils;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.system.ResetDashboardFragment;

import com.android.settings.R;

public class FelicaResetPreferenceController extends BasePreferenceController {
    private ResetDashboardFragment mHostFragment;
    private final UserManager mUm;
    private final Context mContext;

    private static final String TAG = FelicaResetPreferenceController.class.getSimpleName();

    private static final String FELICA_ACTIVITY = "com.felicanetworks.mfm.memory_clear.MemoryClearActivity";
    private static final String FELICA_PACKAGE = "com.felicanetworks.mfm.main";

    public FelicaResetPreferenceController(Context context, String str) {
        super(context, str);
        mContext = context;
        mUm = context.getSystemService(UserManager.class);
    }

    public void setFragment(ResetDashboardFragment hostFragment) {
        mHostFragment = hostFragment;
    }

    @Override
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (!TextUtils.equals(preference.getKey(), getPreferenceKey())) {
            return false;
        }
        launchFelicaResetActivity();
        return true;
    }

    @Override
    public int getAvailabilityStatus() {
         return isFelicaResetSupported() ? AVAILABLE : UNSUPPORTED_ON_DEVICE;
    }

    private boolean isFelicaResetSupported() {
        try {
            PackageInfo pi = mContext.getPackageManager().getPackageInfo(FELICA_PACKAGE, 0);
            if (!pi.applicationInfo.enabled) {
                return false;
            }
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
        return mContext.getResources().getBoolean(R.bool.config_felica_reset_supported);
    }

    private void launchFelicaResetActivity() {
        try {
            final Intent intent = new Intent();
            intent.setClassName(FELICA_PACKAGE, FELICA_ACTIVITY);
            mHostFragment.startActivityForResult(intent, 1000);
        } catch (ActivityNotFoundException e) {
            Log.e(TAG, "Felica reset activity not found", e);
        }
    }
}
