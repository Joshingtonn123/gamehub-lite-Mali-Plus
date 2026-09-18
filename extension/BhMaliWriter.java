package com.xj.winemu.sidebar;

import android.content.Context;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class BhMaliWriter {

    public static void applyFromPrefs(Context ctx) {
        if (ctx == null) return;
        BhMaliSettings s = BhMaliSettings.load(ctx);
        if (!s.enabled) return;

        File filesDir = ctx.getFilesDir();
        writeDxvkConfig(filesDir, s);
        writeBox64Config(filesDir, s);
        writeMaliEnv(filesDir, s);
        cleanTurnipOverrides(filesDir);
    }

    private static void writeDxvkConfig(File filesDir, BhMaliSettings s) {
        StringBuilder sb = new StringBuilder();
        sb.append("# Gamehub Mali+ Optimized Configuration\n");
        sb.append("dxvk.enableAsync = ").append(s.dxvkAsync ? "True" : "False").append("\n");
        sb.append("dxvk.numCompilerThreads = ").append(s.compilerThreads).append("\n");
        sb.append("d3d11.maxTessFactor = ").append(s.maxTessFactor).append("\n");
        sb.append("dxvk.gpl = ").append(s.safeGpl ? "False" : "True").append("\n");
        sb.append("dxvk.memoryTrack = ").append(s.conservativeMemory ? "False" : "True").append("\n");
        sb.append("dxvk.hud = 0\n");

        String configContent = sb.toString();

        File[] targets = new File[] {
                new File(filesDir, "usr/share/dxvk/dxvk.conf"),
                new File(filesDir, "usr/home/steamuser/.dxvk.conf"),
                new File(filesDir, "usr/etc/dxvk.conf")
        };

        for (File target : targets) {
            writeFileSafely(target, configContent);
        }
    }

    private static void writeBox64Config(File filesDir, BhMaliSettings s) {
        StringBuilder sb = new StringBuilder();
        sb.append("[box64]\n");
        sb.append("BOX64_DYNAREC_FASTNAN=").append(s.fastMath ? "1" : "0").append("\n");
        sb.append("BOX64_DYNAREC_FASTROUND=").append(s.fastMath ? "1" : "0").append("\n");
        sb.append("BOX64_DYNAREC_SAFEFLAGS=1\n");
        sb.append("BOX64_DYNAREC_STRONG_MEM=").append(s.relaxedMemory ? "0" : "1").append("\n");
        sb.append("BOX64_DYNAREC_BIGBLOCK=").append(s.bigBlockLevel).append("\n");
        sb.append("BOX64_DYNAREC_WAIT=1\n");

        String configContent = sb.toString();

        File[] targets = new File[] {
                new File(filesDir, "usr/etc/box64.box64rc"),
                new File(filesDir, "usr/home/steamuser/.box64rc")
        };

        for (File target : targets) {
            writeFileSafely(target, configContent);
        }
    }

    private static void writeMaliEnv(File filesDir, BhMaliSettings s) {
        StringBuilder sb = new StringBuilder();
        sb.append("#!/bin/sh\n");
        sb.append("export MESA_VK_WSI_PRESENT_MODE=").append(s.wsiPresentMode == 0 ? "mailbox" : "fifo").append("\n");
        sb.append("export PAN_MESA_DEBUG=gl3\n");
        sb.append("export MESA_GL_VERSION_OVERRIDE=4.6\n");
        sb.append("export MESA_GLSL_VERSION_OVERRIDE=460\n");

        writeFileSafely(new File(filesDir, "usr/etc/mali.env"), sb.toString());
    }

    private static void cleanTurnipOverrides(File filesDir) {
        // Mali devices crash when Turnip/Qualcomm ICD drivers are requested
        File icdDir = new File(filesDir, "usr/home/steamuser/.config/vulkan/icd.d");
        if (icdDir.exists() && icdDir.isDirectory()) {
            File[] list = icdDir.listFiles();
            if (list != null) {
                for (File f : list) {
                    String name = f.getName().toLowerCase();
                    if (name.contains("turnip") || name.contains("freedreno") || name.contains("adreno")) {
                        try { f.delete(); } catch (Exception ignored) {}
                    }
                }
            }
        }
    }

    private static void writeFileSafely(File file, String content) {
        try {
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            try (FileWriter writer = new FileWriter(file, false)) {
                writer.write(content);
            }
        } catch (IOException ignored) {}
    }
}
