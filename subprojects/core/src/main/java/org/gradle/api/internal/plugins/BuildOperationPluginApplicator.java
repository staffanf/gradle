/*
 * Copyright 2017 the original author or authors.
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

package org.gradle.api.internal.plugins;

import org.gradle.api.Action;
import org.gradle.api.Nullable;
import org.gradle.api.Plugin;
import org.gradle.internal.operations.BuildOperationContext;
import org.gradle.internal.progress.BuildOperationExecutor;

/**
 * A decorating {@link PluginApplicator} implementation that delegates to a given
 * decorated implementation, but wraps the apply() execution in a
 * {@link org.gradle.internal.operations.BuildOperation}.
 */
public class BuildOperationPluginApplicator implements PluginApplicator {

    private PluginApplicator decorated;
    private BuildOperationExecutor buildOperationExecutor;

    public BuildOperationPluginApplicator(PluginApplicator decorated, BuildOperationExecutor buildOperationExecutor) {
        this.decorated = decorated;
        this.buildOperationExecutor = buildOperationExecutor;
    }

    public void applyImperative(@Nullable final String pluginId, final Plugin<?> plugin) {
        buildOperationExecutor.run(toDisplayName(pluginId, plugin.getClass()), new Action<BuildOperationContext>() {
            @Override
            public void execute(BuildOperationContext buildOperationContext) {
                decorated.applyImperative(pluginId, plugin);
            }
        });
    }

    public void applyRules(@Nullable final String pluginId, final Class<?> clazz) {
        buildOperationExecutor.run(toDisplayName(pluginId, clazz), new Action<BuildOperationContext>() {
            @Override
            public void execute(BuildOperationContext buildOperationContext) {
                decorated.applyRules(pluginId, clazz);
            }
        });
    }

    public void applyImperativeRulesHybrid(@Nullable final String pluginId, final Plugin<?> plugin) {
        buildOperationExecutor.run(toDisplayName(pluginId, plugin.getClass()), new Action<BuildOperationContext>() {
            @Override
            public void execute(BuildOperationContext buildOperationContext) {
                decorated.applyImperativeRulesHybrid(pluginId, plugin);
            }
        });
    }

    private String toDisplayName(@Nullable String pluginId, Class<?> pluginClass) {
        if (pluginId != null) {
            return "Apply plugin '" + pluginId + "'";
        } else {
            return "Apply plugin '" + pluginClass.getName() + "'";
        }
    }

}
