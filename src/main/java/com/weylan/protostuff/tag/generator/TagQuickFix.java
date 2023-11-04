package com.weylan.protostuff.tag.generator;

import com.intellij.codeInspection.LocalQuickFixOnPsiElement;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class TagQuickFix extends LocalQuickFixOnPsiElement {

    private final Integer fixValue;


    public TagQuickFix(PsiAnnotation annotation, Integer fixValue) {
        super(annotation);
        this.fixValue = fixValue;
    }


    @Override
    public @IntentionName @NotNull String getText() {
        return "Fix @Tag annotation value";
    }

    @Override
    public void invoke(@NotNull Project project, @NotNull PsiFile file, @NotNull PsiElement startElement, @NotNull PsiElement endElement) {
        PsiAnnotation annotation = (PsiAnnotation) startElement;
        PsiAnnotationMemberValue valueElement = annotation.findAttributeValue("value");
        if (valueElement != null) {
            // Set the new value for the @Tag annotation
            valueElement.replace(PsiElementFactory.getInstance(project).createExpressionFromText(String.valueOf(fixValue), annotation));
        } else {
            // If valueElement is null, add a new annotation with the new value
            PsiElementFactory factory = PsiElementFactory.getInstance(project);
            String annotationText = "@Tag(" + fixValue + ")";
            PsiAnnotation newAnnotation = factory.createAnnotationFromText(annotationText, annotation.getParent());
            annotation.getParent().addBefore(newAnnotation, annotation);
            annotation.delete();
        }
    }


    @Override
    public @IntentionFamilyName @NotNull String getFamilyName() {
        return getName();
    }

    @Override
    public boolean isAvailable(@NotNull Project project, @NotNull PsiFile file, @NotNull PsiElement startElement, @NotNull PsiElement endElement) {
        // Check if the problem descriptor's psiElement matches the startElement
        return startElement instanceof PsiAnnotation;
    }
}
