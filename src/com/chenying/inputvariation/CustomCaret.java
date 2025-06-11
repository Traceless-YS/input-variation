package com.chenying.inputvariation;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.colors.EditorColors;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 自定义光标实现，用于在注释区域显示红色光标
 */
public class CustomCaret {
    private static final Map<Editor, Color> editorToOriginalColor = new HashMap<>();
    private static final Color RED_COLOR = Color.RED;
    private static final int CUSTOM_CARET_WIDTH = 2; // 自定义光标宽度
    
    /**
     * 应用红色光标到编辑器
     * @param editor 目标编辑器
     */
    public static void applyRedCaret(Editor editor) {
        if (editor == null) return;
        
        try {
            // 直接绘制在编辑器上的自定义光标
            drawCustomCursor(editor, RED_COLOR, CUSTOM_CARET_WIDTH);
        } catch (Exception e) {
            // 忽略异常
        }
    }
    
    /**
     * 恢复编辑器的默认光标
     * @param editor 目标编辑器
     */
    public static void restoreDefaultCaret(Editor editor) {
        if (editor == null) return;
        
        try {
            // 获取原始颜色
            Color originalColor = editorToOriginalColor.getOrDefault(editor, Color.BLACK);
            
            // 重绘光标
            drawCustomCursor(editor, originalColor, 1);
        } catch (Exception e) {
            // 忽略异常
        }
    }
    
    /**
     * 绘制自定义光标
     * @param editor 目标编辑器
     * @param color 光标颜色
     * @param width 光标宽度
     */
    private static void drawCustomCursor(Editor editor, Color color, int width) {
        if (editor == null) return;
        
        // 在Application线程中执行
        SwingUtilities.invokeLater(() -> {
            try {
//                // 存储原始颜色
                if (!editorToOriginalColor.containsKey(editor)) {
                    Color originalColor =  editor.getColorsScheme().getColor(EditorColors.CARET_COLOR);
                    editorToOriginalColor.put(editor,originalColor);
                }
                editor.getColorsScheme().setColor(EditorColors.CARET_COLOR,color);
                // 直接修改光标属性
                editor.getSettings().setCaretBlinkPeriod(500); // 设置光标闪烁速度
                editor.getSettings().setBlockCursor(false);    // 使用垂直光标
//                editor.getSettings().setCaretWidth(width);
                editor.getSettings().setLineCursorWidth(width); // 设置光标宽度

                // 强制重绘
                editor.getComponent().repaint();
            } catch (Exception e) {
                // 忽略异常
            }
        });
    }
} 