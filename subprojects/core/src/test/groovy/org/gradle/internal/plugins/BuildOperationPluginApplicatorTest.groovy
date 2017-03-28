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

package org.gradle.internal.plugins

import org.gradle.api.Plugin
import org.gradle.api.internal.plugins.BuildOperationPluginApplicator
import org.gradle.api.internal.plugins.PluginApplicator
import org.gradle.internal.progress.TestBuildOperationExecutor
import spock.lang.Specification

class BuildOperationPluginApplicatorTest extends Specification {

    def "delegates to decorated plugin applicator via build operation"() {
        given:
        def buildOperationExecutor = new TestBuildOperationExecutor()
        def plugin = Mock(Plugin)
        def decoratedPluginApplicator = Mock(PluginApplicator)
        def buildOperationScriptPlugin = new BuildOperationPluginApplicator(decoratedPluginApplicator, buildOperationExecutor)

        when:
        buildOperationScriptPlugin.applyImperative("my.plugin.id", plugin)
        buildOperationScriptPlugin.applyImperativeRulesHybrid("my.plugin.hybrid.id", plugin)
        buildOperationScriptPlugin.applyRules("my.plugin.rules.id", Plugin)

        then:
        1 * decoratedPluginApplicator.applyImperative("my.plugin.id", plugin)
        1 * decoratedPluginApplicator.applyImperativeRulesHybrid("my.plugin.hybrid.id", plugin)
        1 * decoratedPluginApplicator.applyRules("my.plugin.rules.id", Plugin)
        0 * decoratedPluginApplicator._
        buildOperationExecutor.operations.size() == 3
        buildOperationExecutor.operations.get(0).name == "Apply plugin 'my.plugin.id'"
        buildOperationExecutor.operations.get(1).name == "Apply plugin 'my.plugin.hybrid.id'"
        buildOperationExecutor.operations.get(2).name == "Apply plugin 'my.plugin.rules.id'"
    }
}
