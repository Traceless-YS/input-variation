package com.chenying.inputvariation;

import com.intellij.lang.Language;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileTypes.FileType;
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
        
        // 检查注释的文本表示
        String text = element.getText();
        if (text != null) {
            // Java风格注释: //, /*, */, /**
            if (text.contains("//") || text.contains("/*") || text.contains("*/") || text.contains("/**")) {
                return true;
            }
            
            // XML风格注释: <!-- -->
            if (text.contains("<!--") || text.contains("-->")) {
                return true;
            }
            
            // Shell, Python, YAML等使用#的注释
            if (text.contains("#")) {
                FileType fileType = psiFile.getFileType();
                String extension = fileType.getDefaultExtension().toLowerCase();
                return HASH_COMMENT_EXTENSIONS.contains(extension);
            }
        }
        
        // 判断文档中的行是否是注释
        Document document = psiFile.getViewProvider().getDocument();
        if (document != null) {
            int lineStartOffset = document.getLineStartOffset(document.getLineNumber(offset));
            String lineText = document.getText().substring(lineStartOffset, 
                Math.min(lineStartOffset + 50, document.getTextLength())).trim();
            
            if (lineText.startsWith("//") || lineText.startsWith("/*") || lineText.startsWith("*") || 
                lineText.startsWith("*/") || lineText.startsWith("/**")) {
                return true;
            }
            
            if (lineText.startsWith("<!--") || lineText.endsWith("-->")) {
                return true;
            }
            
            if (lineText.startsWith("#")) {
                String extension = psiFile.getFileType().getDefaultExtension().toLowerCase();
                return HASH_COMMENT_EXTENSIONS.contains(extension);
            }
        }
        
        return false;
    }
} 