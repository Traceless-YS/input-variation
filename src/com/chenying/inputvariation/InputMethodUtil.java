package com.chenying.inputvariation;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.WPARAM;
import com.sun.jna.platform.win32.WinDef.LPARAM;
import com.sun.jna.win32.StdCallLibrary;

/**
 * 输入法切换工具类
 */
public class InputMethodUtil {

    // 中文输入法标识码（简体中文微软拼音）
    private static final String CHINESE_IME_CODE = "00000804";
    // 英文输入法标识码（英文US键盘）
    private static final String ENGLISH_IME_CODE = "00000409";

    private static final int WM_INPUTLANGCHANGEREQUEST = 0x0050;
    private static final int INPUTLANGCHANGE_FORWARD = 0x0002;

    public interface MyUser32 extends StdCallLibrary {
        MyUser32 INSTANCE = Native.load("user32", MyUser32.class);
        
        boolean PostMessageA(HWND hWnd, int msg, WPARAM wParam, LPARAM lParam);
        HWND GetForegroundWindow();
        int LoadKeyboardLayoutA(String pwszKLID, int Flags);
        boolean SystemParametersInfoA(int uiAction, int uiParam, Pointer pvParam, int fWinIni);
    }

    /**
     * 切换到中文输入法
     */
    public static void switchToChinese() {
        try {
            int layout = MyUser32.INSTANCE.LoadKeyboardLayoutA(CHINESE_IME_CODE, 0);
            HWND hwnd = MyUser32.INSTANCE.GetForegroundWindow();
            WPARAM wParam = new WPARAM(INPUTLANGCHANGE_FORWARD);
            LPARAM lParam = new LPARAM(layout);
            MyUser32.INSTANCE.PostMessageA(hwnd, WM_INPUTLANGCHANGEREQUEST, wParam, lParam);
        } catch (Exception e) {
            e.printStackTrace();
            // 降级使用命令行方式尝试切换
            try {
                Runtime.getRuntime().exec("rundll32 shell32.dll,Control_RunDLL input.dll,,1");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * 切换到英文输入法
     */
    public static void switchToEnglish() {
        try {
            int layout = MyUser32.INSTANCE.LoadKeyboardLayoutA(ENGLISH_IME_CODE, 0);
            HWND hwnd = MyUser32.INSTANCE.GetForegroundWindow();
            WPARAM wParam = new WPARAM(INPUTLANGCHANGE_FORWARD);
            LPARAM lParam = new LPARAM(layout);
            MyUser32.INSTANCE.PostMessageA(hwnd, WM_INPUTLANGCHANGEREQUEST, wParam, lParam);
        } catch (Exception e) {
            e.printStackTrace();
            // 降级使用命令行方式尝试切换
            try {
                Runtime.getRuntime().exec("rundll32 shell32.dll,Control_RunDLL input.dll,,2");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
} 