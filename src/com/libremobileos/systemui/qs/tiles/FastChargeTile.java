/*
 * Copyright (C) 2020 The LineageOS Project
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

package com.libremobileos.systemui.qs.tiles;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.service.quicksettings.Tile;

import androidx.annotation.Nullable;

import com.android.internal.logging.MetricsLogger;
import com.android.internal.logging.nano.MetricsProto.MetricsEvent;

import com.android.systemui.animation.Expandable;
import com.android.systemui.dagger.qualifiers.Background;
import com.android.systemui.dagger.qualifiers.Main;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.plugins.qs.QSTile.BooleanState;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.qs.logging.QSLogger;
import com.android.systemui.qs.QsEventLogger;
import com.android.systemui.qs.QSHost;
import com.android.systemui.qs.tileimpl.QSTileImpl;
import com.android.systemui.res.R;

import vendor.lineage.fastcharge.V1_0.IFastCharge;

import java.util.NoSuchElementException;

import javax.inject.Inject;

public class FastChargeTile extends QSTileImpl<BooleanState> {

    public static final String TILE_SPEC = "fastcharge";

    private IFastCharge mFastCharge;

    @Inject
    public FastChargeTile(
            QSHost host,
            QsEventLogger uiEventLogger,
            @Background Looper backgroundLooper,
            @Main Handler mainHandler,
            FalsingManager falsingManager,
            MetricsLogger metricsLogger,
            StatusBarStateController statusBarStateController,
            ActivityStarter activityStarter,
            QSLogger qsLogger
    ) {
        super(host, uiEventLogger, backgroundLooper, mainHandler, falsingManager, metricsLogger,
                statusBarStateController, activityStarter, qsLogger);
        mFastCharge = getFastCharge();
        if (mFastCharge == null) {
            return;
        }
    }

    @Override
    public boolean isAvailable() {
        return mFastCharge != null;
    }

    @Override
    public BooleanState newTileState() {
        BooleanState state = new BooleanState();
        state.handlesLongClick = false;
        return state;
    }

    @Override
    public void handleClick(@Nullable Expandable expandable) {
        try {
            boolean fastChargeEnabled = mFastCharge.isEnabled();

            if (mFastCharge.setEnabled(!fastChargeEnabled) != fastChargeEnabled) {
                refreshState();
            }
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public Intent getLongClickIntent() {
        return null;
    }

    @Override
    public CharSequence getTileLabel() {
        return mContext.getString(R.string.quick_settings_fastcharge_label);
    }

    @Override
    protected void handleUpdateState(BooleanState state, Object arg) {
        if (!isAvailable()) {
            return;
        }

        state.icon = ResourceIcon.get(R.drawable.ic_qs_fastcharge);
        try {
            state.value = mFastCharge.isEnabled();
        } catch (RemoteException ex) {
            state.value = false;
            ex.printStackTrace();
        }
        state.label = mContext.getString(R.string.quick_settings_fastcharge_label);

        state.state = state.value ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE;

    }

    @Override
    public int getMetricsCategory() {
        return MetricsEvent.LMODROID;
    }

    @Override
    public void handleSetListening(boolean listening) {
    }

    private synchronized IFastCharge getFastCharge() {
        try {
            return IFastCharge.getService();
        } catch (RemoteException ex) {
            ex.printStackTrace();
        } catch (NoSuchElementException ex) {
            // service not available
        }

        return null;
    }
}
