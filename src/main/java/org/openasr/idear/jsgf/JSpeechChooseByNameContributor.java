package org.openasr.idear.jsgf;

import com.intellij.navigation.*;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.openasr.idear.jsgf.psi.JSpeechRuleDefinition;

import java.util.*;

/**
 * Created by breandan on 11/15/2015.
 */

public class JSpeechChooseByNameContributor implements ChooseByNameContributor {
    @NotNull
    @Override
    public String[] getNames(Project project, boolean includeNonProjectItems) {
        List<JSpeechRuleDefinition> properties = JSpeechUtil.findProperties(project);
        List<String> names = new ArrayList<String>(properties.size());
        for (JSpeechRuleDefinition property : properties) {
            if (property.getRulename().getName() != null && property.getRulename().getName().length() > 0) {
                names.add(property.getRulename().getName());
            }
        }
        return names.toArray(new String[names.size()]);
    }

    @NotNull
    @Override
    public NavigationItem[] getItemsByName(String name, String pattern, Project project, boolean includeNonProjectItems) {
        List<JSpeechRuleDefinition> properties = JSpeechUtil.findProperties(project, name);
        return properties.toArray(new NavigationItem[properties.size()]);
    }
}