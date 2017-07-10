package com.jetbrains.idear.jsgf;

/**
 * Created by breandan on 11/15/2015.
 */

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.jetbrains.idear.jsgf.psi.JSpeechRuleDefinition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class JSpeechReference extends PsiReferenceBase<PsiElement> implements PsiPolyVariantReference {
    private String key;

    public JSpeechReference(@NotNull PsiElement element, TextRange textRange) {
        super(element, textRange);
        key = element.getText().substring(textRange.getStartOffset(), textRange.getEndOffset());
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        Project project = myElement.getProject();
        final List<JSpeechRuleDefinition> properties = JSpeechUtil.findProperties(project, key);

        List<ResolveResult> results = new ArrayList<ResolveResult>();
        for (JSpeechRuleDefinition property : properties) {
            results.add(new PsiElementResolveResult(property));
        }
        return results.toArray(new ResolveResult[results.size()]);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        ResolveResult[] resolveResults = multiResolve(false);
        return resolveResults.length == 1 ? resolveResults[0].getElement() : null;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        Project project = myElement.getProject();
        List<JSpeechRuleDefinition> properties = JSpeechUtil.findProperties(project);
        List<LookupElement> variants = new ArrayList<>();
        for (final JSpeechRuleDefinition property : properties) {
            if (property.getRulename().getString().getText().length() > 0) {
                variants.add(LookupElementBuilder.create(property).
                        withIcon(JSpeechIcons.FILE).
                        withTypeText(property.getContainingFile().getName())
                );
            }
        }
        return variants.toArray();
    }
}
