/*
 * Copyright (C) 2023 The Android Open Source Project
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

package org.luminedroid.systemui;

import com.android.systemui.dagger.GlobalModule;
import com.android.systemui.dagger.GlobalRootComponent;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Root component for Dagger injection used in LibreMobileOS.
 */
@Singleton
@Component(modules = {GlobalModule.class})
public interface LumineGlobalRootComponent extends GlobalRootComponent {

    /**
     * Builder for a LumineGlobalRootComponent.
     */
    @Component.Builder
    interface Builder extends GlobalRootComponent.Builder {
        LumineGlobalRootComponent build();
    }

    /**
     * Builder for a {@link LumineSysUIComponent}, which makes it a subcomponent of this class.
     */
    @Override
    LumineSystemUIComponent.Builder getSysUIComponent();
}
