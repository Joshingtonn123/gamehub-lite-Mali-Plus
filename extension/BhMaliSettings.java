package com.xj.winemu.sidebar;

import android.content.Context;
import android.content.SharedPreferences;

public class BhMaliSettings {

    public static final String PREFS = "bh_mali_settings";

    public boolean enabled = true;
    public boolean dxvkAsync = true;
    public int compilerThreads = 4;
    public int maxTessFactor = 8;
    public int wsiPresentMode = 0; // 0=Mailbox (Triple-buffer/Lowest lag), 1=FIFO (VSync)
    public boolean fastMath = true; // BOX64_DYNAREC_FASTNAN & FASTROUND
    public boolean relaxedMemory = true; // BOX64_DYNAREC_STRONG_MEM=0
    public int bigBlockLevel = 2; // 1..3
    public boolean safeGpl = true; // dxvk.gpl=False (prevents crashes on buggy Mali drivers)
    public boolean conservativeMemory = true;

    public static BhMaliSettings load(Context ctx) {
        SharedPreferences sp = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        BhMaliSettings s = new BhMaliSettings();
        s.enabled = sp.getBoolean("enabled", true);
        s.dxvkAsync = sp.getBoolean("dxvkAsync", true);
        s.compilerThreads = Math.max(1, Math.min(8, sp.getInt("compilerThreads", 4)));
        s.maxTessFactor = sp.getInt("maxTessFactor", 8);
        s.wsiPresentMode = sp.getInt("wsiPresentMode", 0);
        s.fastMath = sp.getBoolean("fastMath", true);
        s.relaxedMemory = sp.getBoolean("relaxedMemory", true);
        s.bigBlockLevel = Math.max(1, Math.min(3, sp.getInt("bigBlockLevel", 2)));
        s.safeGpl = sp.getBoolean("safeGpl", true);
        s.conservativeMemory = sp.getBoolean("conservativeMemory", true);
        return s;
    }

    public void save(Context ctx) {
        SharedPreferences.Editor ed = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit();
        ed.putBoolean("enabled", enabled);
        ed.putBoolean("dxvkAsync", dxvkAsync);
        ed.putInt("compilerThreads", compilerThreads);
        ed.putInt("maxTessFactor", maxTessFactor);
        ed.putInt("wsiPresentMode", wsiPresentMode);
        ed.putBoolean("fastMath", fastMath);
        ed.putBoolean("relaxedMemory", relaxedMemory);
        ed.putInt("bigBlockLevel", bigBlockLevel);
        ed.putBoolean("safeGpl", safeGpl);
        ed.putBoolean("conservativeMemory", conservativeMemory);
        ed.apply();
    }

    public void resetToDefaults() {
        this.enabled = true;
        this.dxvkAsync = true;
        this.compilerThreads = 4;
        this.maxTessFactor = 8;
        this.wsiPresentMode = 0;
        this.fastMath = true;
        this.relaxedMemory = true;
        this.bigBlockLevel = 2;
        this.safeGpl = true;
        this.conservativeMemory = true;
    }
}
