/*
 * Copyright (C) 2021 The LineageOS Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.fuelgauge;

import android.content.Context;
import android.util.Log;

import androidx.preference.Preference;
import androidx.preference.SwitchPreferenceCompat;

import com.android.settings.core.BasePreferenceController;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Controller to change and update the fast charging toggle
 */
public class FastChargingPreferenceController extends BasePreferenceController
        implements Preference.OnPreferenceChangeListener {

    private static final String KEY_FAST_CHARGING = "fast_charging";
    private static final String TAG = "FastChargingPreferenceController";
    private static final String FILE_RESTRICT_CHG = "/sys/class/qcom-battery/restrict_chg";

    public FastChargingPreferenceController(Context context) {
        super(context, KEY_FAST_CHARGING);
    }

    @Override
    public int getAvailabilityStatus() {
        return new File(FILE_RESTRICT_CHG).exists() ? AVAILABLE : UNSUPPORTED_ON_DEVICE;
    }

    @Override
    public void updateState(Preference preference) {
        super.updateState(preference);
        ((SwitchPreferenceCompat) preference).setChecked(isFastChargingEnabled());
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        final boolean shouldEnableFastCharging = (Boolean) newValue;
        writeValue(shouldEnableFastCharging ? "0" : "1");
        updateState(preference);
        return false;
    }

    private boolean isFastChargingEnabled() {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_RESTRICT_CHG))) {
            String value = br.readLine();
            // restrict_chg=0 means unrestricted (fast charge ON)
            return "0".equals(value != null ? value.trim() : null);
        } catch (IOException e) {
            Log.e(TAG, "Failed to read restrict_chg", e);
            return false;
        }
    }

    private void writeValue(String value) {
        try (FileWriter fw = new FileWriter(FILE_RESTRICT_CHG)) {
            fw.write(value);
        } catch (IOException e) {
            Log.e(TAG, "Failed to write restrict_chg", e);
        }
    }
}
