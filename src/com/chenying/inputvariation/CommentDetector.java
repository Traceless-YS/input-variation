package com.chenying.inputvariation;

import com.intellij.openapi.editor.Document;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlComment;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 注释检测工具类，用于检测当前位置是否在注释区域内
 */
public class CommentDetector {

    private static final Set<String> HASH_COMMENT_EXTENSIONS = new HashSet<>(
            Arrays.asList("yml", "yaml", "py", "properties", "conf", "sh", "bash", "zsh")
    );

    /**
     * 检测当前位置是否在注释区域内
     * @param psiFile 当前文件的PSI表示
     * @param offset 当前光标位置
     * @return 是否在注释区域内
     */
    public static boolean isInComment(PsiFile psiFile, int offset) {
        if (psiFile == null) return false;
        
        PsiElement element = psiFile.findElementAt(offset);
        if (element == null) return false;
        
        // 检查各种类型的注释元素
        if (element instanceof PsiComment || element.getParent() instanceof PsiComment) {
            return true;
        }
        
        // 检查XML注释
        if (element instanceof XmlComment || element.getParent() instanceof XmlComment) {
            return true;
        }

        // 判断文档中的行是否是注释，同时确保光标在注释符号之后
        Document document = psiFile.getViewProvider().getDocument();
        if (document != null) {
            int lineNumber = document.getLineNumber(offset);
            int lineStartOffset = document.getLineStartOffset(lineNumber);
            String lineText = document.getText().substring(lineStartOffset, 
                Math.min(lineStartOffset + 50, document.getTextLength()));
            String trimmedLine = lineText.trim();
            
            // 获取光标在当前行的相对位置
            int relativeOffset = offset - lineStartOffset;
            
            // 处理单行注释，考虑前置空格
            if (trimmedLine.startsWith("//")) {
                // 找到实际的//在原始行中的位置
                int actualCommentPos = lineText.indexOf("//");
                // 确保找到的//是有效的（不在字符串内）
                if (actualCommentPos != -1 && isValidCommentPosition(lineText, actualCommentPos)) {
                    return relativeOffset > actualCommentPos;
                }
            }
            
            // 检查是否在多行注释内
            int commentStart = -1;
            int commentEnd = -1;
            
            // 检查当前行的注释范围
            if (lineText.contains("/*")) {
                int pos = lineText.indexOf("/*");
                if (isValidCommentPosition(lineText, pos)) {
                    commentStart = pos;
                }
            }
            if (lineText.contains("*/")) {
                commentEnd = lineText.indexOf("*/") + 2;
            }
            
            // 检查XML注释
            if (lineText.contains("<!--")) {
                int pos = lineText.indexOf("<!--");
                if (isValidCommentPosition(lineText, pos)) {
                    commentStart = pos;
                }
            }
            if (lineText.contains("-->")) {
                commentEnd = lineText.indexOf("-->") + 3;
            }
            
            // 如果在同一行找到注释的开始和结束
            if (commentStart != -1 && commentEnd != -1) {
                return relativeOffset > commentStart && relativeOffset <= commentEnd;
            }
            
            // 处理#注释
            if (trimmedLine.startsWith("#")) {
                String extension = psiFile.getFileType().getDefaultExtension().toLowerCase();
                if (HASH_COMMENT_EXTENSIONS.contains(extension)) {
                    int actualCommentPos = lineText.indexOf("#");
                    if (isValidCommentPosition(lineText, actualCommentPos)) {
                        return relativeOffset > actualCommentPos;
                    }
                }
            }
            
            // 处理多行注释的中间行（以*开头的行）
            if (trimmedLine.startsWith("*")) {
                int actualStarPos = lineText.indexOf("*");
                if (isValidCommentPosition(lineText, actualStarPos)) {
                    return relativeOffset > actualStarPos;
                }
            }
        }
        
        return false;
    }
    
    /**
     * 检查注释符号的位置是否有效（不在字符串内）
     */
    private static boolean isValidCommentPosition(String lineText, int position) {
        if (position == -1) return false;
        
        boolean inString = false;
        boolean escaped = false;
        
        for (int i = 0; i < position; i++) {
            char c = lineText.charAt(i);
            
            if (c == '\\') {
                escaped = !escaped;
            } else if (c == '"' && !escaped) {
                inString = !inString;
            } else {
                escaped = false;
            }
        }
        
        return !inString;
    }
} 