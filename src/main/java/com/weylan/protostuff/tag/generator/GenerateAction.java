package com.weylan.protostuff.tag.generator;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class GenerateAction extends AnAction {

    public static final String TAG_PACKAGE = "io.protostuff.Tag";

    @Override
    public void actionPerformed(AnActionEvent e) {
        PsiFile psiFile = e.getData(LangDataKeys.PSI_FILE);
        Editor editor = e.getData(PlatformDataKeys.EDITOR);
        if (Objects.isNull(psiFile) || Objects.isNull(editor)) {
            return;
        }
        int offset = editor.getCaretModel().getOffset();
        PsiElement elementAt = psiFile.findElementAt(offset);
        PsiClass psiClass = PsiTreeUtil.getParentOfType(elementAt, PsiClass.class);
        if (Objects.isNull(psiClass)) {
            return;
        }

        GenerateDialog dialog = new GenerateDialog();
        dialog.pack();
        dialog.setVisible(true);
        WriteCommandAction.writeCommandAction(psiClass.getContainingFile().getProject(), psiClass.getContainingFile()).run(() -> {
           if(!Boolean.TRUE.equals(dialog.isOk())){
               return;
           }
            if (dialog.isDelOnlyDelete()) {
                delTags(psiClass);
                return;
            }
            importTagPackage(e);
            generateTags(psiClass, dialog);
        });

    }

    private void importTagPackage(AnActionEvent e) {
        Project project = e.getProject();
        if (Objects.isNull(project)) {
            return;
        }
        PsiFile psiFile = e.getData(LangDataKeys.PSI_FILE);
        if (Objects.isNull(psiFile)) {
            return;
        }
        PsiJavaFile javaFile = (PsiJavaFile) psiFile;
        JavaPsiFacade psiFacade = JavaPsiFacade.getInstance(project);
        PsiClass tagClass = psiFacade.findClass(TAG_PACKAGE, GlobalSearchScope.allScope(project));
        if (Objects.nonNull(tagClass)) {
            javaFile.importClass(tagClass);
        }
    }

    private void generateTags(PsiClass psiClass, GenerateDialog dialog) {
        int staringNum = dialog.getStaringNum();

        if (Objects.isNull(psiClass) || staringNum < 0) {
            return;
        }
        PsiField[] allFields = psiClass.getAllFields();
        int existMaxIndex = findExistMaxIndex(allFields);
        if (existMaxIndex != 0) {
            staringNum = ++existMaxIndex;
        }
        for (PsiField field : allFields) {
            PsiModifierList modifierList = field.getModifierList();
            if (field.hasModifierProperty(PsiModifier.STATIC) && !dialog.isTagStaticFields()) {
                continue;
            }
            if (field.hasModifierProperty(PsiModifier.TRANSIENT) && !dialog.isTagTransientFields()) {
                continue;
            }
            if (Objects.nonNull(field.getContainingClass()) && !field.getContainingClass().equals(psiClass)) {
                staringNum++;
                continue;
            }
            if (Objects.nonNull(modifierList)) {
                PsiAnnotation[] annotations = modifierList.getAnnotations();
                Optional<PsiAnnotation> first = Arrays.stream(annotations).filter(ann -> TAG_PACKAGE.equals(ann.getQualifiedName())).findFirst();
                if (first.isPresent()) {
                    continue;
                }
                modifierList.addAnnotation(String.format("Tag(%s)", staringNum++));
            }
        }
    }

    private int findExistMaxIndex(PsiField[] allFields) {
        int maxIndex = 0;
        for (PsiField field : allFields) {
            PsiAnnotation annotation = field.getAnnotation(TAG_PACKAGE);
            if (Objects.isNull(annotation)) {
                continue;
            }
            PsiAnnotationParameterList parameterList = annotation.getParameterList();
            PsiElement elementAt = parameterList.findElementAt(1);
            if (Objects.isNull(elementAt)) {
                continue;
            }
            try {
                int index = Integer.parseInt(elementAt.getText());
                if (maxIndex < index) {
                    maxIndex = index;
                }
            } catch (NumberFormatException e) {
                throw new RuntimeException(e);
            }

        }
        return maxIndex;
    }

    private void delTags(PsiClass psiClass) {
        PsiField[] selFields = psiClass.getFields();
        for (PsiField field : selFields) {
            PsiAnnotation annotation = field.getAnnotation(TAG_PACKAGE);
            if (Objects.isNull(annotation)) {
                continue;
            }
            annotation.delete();
        }
    }


}
