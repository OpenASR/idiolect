package com.jetbrains.idear.jsgf;

import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.Project;
import com.jetbrains.idear.jsgf.psi.JSpeechRuleDefinition;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

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