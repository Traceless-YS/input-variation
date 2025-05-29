package com.chenying.inputvariation;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.event.CaretEvent;
import com.intellij.openapi.editor.event.CaretListener;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiUtilBase;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 输入法切换器，用于在注释和代码区域自动切换输入法和光标颜色
 */
public class InputMethodSwitcher implements StartupActivity {

    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private boolean inCommentZone = false;
    private boolean inputMethodChanged = false;

    @Override
    public void runActivity(@NotNull Project project) {
        // 注册编辑器事件监听器
        EditorFactory.getInstance().getEventMulticaster().addCaretListener(new CaretListener() {
            @Override
            public void caretPositionChanged(@NotNull CaretEvent event) {
                checkCommentZone(event.getEditor(), event.getEditor().getCaretModel().getOffset());
            }
        }, project);

        EditorFactory.getInstance().getEventMulticaster().addDocumentListener(new DocumentListener() {
            @Override
            public void documentChanged(@NotNull DocumentEvent event) {
                Editor[] editors = EditorFactory.getInstance().getEditors(event.getDocument(), project);
                if (editors.length > 0) {
                    Editor editor = editors[0];
                    checkCommentZone(editor, editor.getCaretModel().getOffset());
                }
            }
        }, project);
    }

    private void checkCommentZone(Editor editor, int offset) {
        PsiFile psiFile = PsiUtilBase.getPsiFileInEditor(editor, editor.getProject());
        if (psiFile == null) return;

        // 延迟执行，避免频繁检查和切换
        executorService.schedule(() -> {
            // 判断当前位置是否在注释中
            boolean isInComment = CommentDetector.isInComment(psiFile, offset);
            
            if (isInComment != inCommentZone) {
                inCommentZone = isInComment;
                if (isInComment) {
                    switchToChineseInputMethod();
                    // 使用CustomCaret来设置红色光标
                    CustomCaret.applyRedCaret(editor);
                } else {
                    switchToEnglishInputMethod();
                    // 使用CustomCaret来恢复默认光标
                    CustomCaret.restoreDefaultCaret(editor);
                }
            }
        }, 50, TimeUnit.MILLISECONDS);
    }

    private void switchToChineseInputMethod() {
        if (!inputMethodChanged) {
            InputMethodUtil.switchToChinese();
            inputMethodChanged = true;
        }
    }

    private void switchToEnglishInputMethod() {
        if (inputMethodChanged) {
            InputMethodUtil.switchToEnglish();
            inputMethodChanged = false;
        }
    }
} 