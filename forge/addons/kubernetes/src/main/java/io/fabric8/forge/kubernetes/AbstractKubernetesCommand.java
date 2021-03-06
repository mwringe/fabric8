/**
 *  Copyright 2005-2015 Red Hat, Inc.
 *
 *  Red Hat licenses this file to you under the Apache License, version
 *  2.0 (the "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied.  See the License for the specific language governing
 *  permissions and limitations under the License.
 */
package io.fabric8.forge.kubernetes;

import io.fabric8.kubernetes.api.KubernetesHelper;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.utils.Objects;
import io.fabric8.utils.Strings;
import io.fabric8.utils.TablePrinter;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.ui.AbstractProjectCommand;
import org.jboss.forge.addon.ui.UIProvider;
import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.output.UIOutput;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;

import javax.inject.Inject;
import java.io.PrintStream;

/**
 * An abstract base class for kubernetes related commands
 */
public abstract class AbstractKubernetesCommand extends AbstractProjectCommand implements UICommand {
    public static String CATEGORY = "Kubernetes";

    private static String namespace = KubernetesHelper.defaultNamespace();
    private KubernetesClient kubernetes;

    @Inject
    private ProjectFactory projectFactory;

    /*
        @Inject
    */
    UIProvider uiProvider;

    @Inject
    @WithAttributes(name = "kubernetesUrl", label = "The URL where the kubernetes master is running")
    UIInput<String> kubernetesUrl;

    @Override
    protected boolean isProjectRequired() {
        return false;
    }

    @Override
    protected ProjectFactory getProjectFactory() {
        return projectFactory;
    }

    public KubernetesClient getKubernetes() {
        if (kubernetes == null) {
            String kubernetesAddress = kubernetesUrl.getValue();
            if (Strings.isNotBlank(kubernetesAddress)) {
                kubernetes = new DefaultKubernetesClient(new DefaultKubernetesClient.ConfigBuilder().masterUrl(kubernetesAddress).build());
            } else {
                kubernetes = new DefaultKubernetesClient();
            }
        }
        Objects.notNull(kubernetes, "kubernetes");
        return kubernetes;
    }

    public void setKubernetes(KubernetesClient kubernetes) {
        this.kubernetes = kubernetes;
    }

    public boolean isGUI() {
        return getUiProvider().isGUI();
    }

    public UIOutput getOutput() {
        UIProvider provider = getUiProvider();
        return provider != null ? provider.getOutput() : null;
    }

    public UIProvider getUiProvider() {
        return uiProvider;
    }

    public void setUiProvider(UIProvider uiProvider) {
        this.uiProvider = uiProvider;
    }

    @Override
    public void initializeUI(UIBuilder uiBuilder) throws Exception {
    }

    /**
     * Prints the given table and returns success
     */
    protected Result tableResults(TablePrinter table) {
        table.print(getOut());
        return Results.success();
    }

    public PrintStream getOut() {
        UIOutput output = getOutput();
        if (output != null) {
            return output.out();
        } else {
            return System.out;
        }
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        AbstractKubernetesCommand.namespace = namespace;
    }
}
