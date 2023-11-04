package com.weylan.protostuff.tag.generator;

import com.intellij.codeInspection.*;
import com.intellij.psi.*;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class TagCheckInspection extends AbstractBaseJavaLocalInspectionTool {


    @Override
    public ProblemDescriptor @Nullable [] checkField(@NotNull PsiField field, @NotNull InspectionManager manager, boolean isOnTheFly) {

        // Initialize a ProblemsHolder to collect problems
        ProblemsHolder problemsHolder = new ProblemsHolder(manager, field.getContainingFile(), isOnTheFly);

        PsiAnnotation annotation = field.getAnnotation(GenerateAction.TAG_PACKAGE);
        if (annotation != null) {
            int tagValue = getTagAnnotationValue(annotation);
            if (tagValue != -1) {
                // Check for duplicate @Tag values
                PsiClass containingClass = field.getContainingClass();
                if (containingClass != null) {
                    int maxIndex = tagValue;

                    ArrayList<PsiAnnotation> list = new ArrayList<>();
                    for (PsiField otherField : containingClass.getAllFields()) {
                        if (otherField != field) {
                            PsiAnnotation otherTagAnnotation = otherField.getAnnotation(GenerateAction.TAG_PACKAGE);
                            if (otherTagAnnotation != null) {

                                int otherTagValue = getTagAnnotationValue(otherTagAnnotation);
                                if (otherTagValue == tagValue) {
                                    list.add(otherTagAnnotation);
                                }
                                if (maxIndex < otherTagValue) {
                                    maxIndex = otherTagValue;
                                }
                            }
                        }
                    }
                    if (CollectionUtils.isNotEmpty(list)) {
                        list.add(annotation);
                        Integer fixValue = ++maxIndex;
                        TagQuickFix[] fix = list.stream().map(an -> new TagQuickFix(an, fixValue)).toList().toArray(new TagQuickFix[0]);
                        // Register a problem and provide a quick fix to modify the duplicate annotation
                        problemsHolder.registerProblem(annotation, "Duplicate @Tag value: " + tagValue,  new TagQuickFix(annotation, fixValue));

                    }
                }
            }
        }
        return problemsHolder.getResultsArray();
    }

    private int getTagAnnotationValue(@NotNull PsiAnnotation tagAnnotation) {
        PsiElement valueElement = tagAnnotation.findAttributeValue("value");
        if (valueElement != null && valueElement.getText() != null) {
            try {
                return Integer.parseInt(valueElement.getText());
            } catch (NumberFormatException e) {
                // Ignore, return -1 for invalid values
            }
        }
        return -1;
    }



    @Override
    public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly, @NotNull LocalInspectionToolSession session) {


        return super.buildVisitor(holder, isOnTheFly, session);
    }
}
