package com.xj.winemu.sidebar;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class BhMaliDialog extends Dialog {

    private final BhMaliSettings settings;

    private TextView tvThreadsValue;
    private TextView tvTessValue;
    private TextView tvBigBlockValue;
    private CheckBox cbAsync;
    private CheckBox cbFastMath;
    private CheckBox cbRelaxedMem;
    private CheckBox cbSafeGpl;
    private SeekBar sbThreads;
    private SeekBar sbTess;
    private SeekBar sbBigBlock;
    private TextView tvPresentModeValue;

    private static final int[] TESS_VALUES = new int[]{8, 16, 32, 64};

    public BhMaliDialog(Context context) {
        super(context);
        this.settings = BhMaliSettings.load(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window w = getWindow();
        if (w != null) {
            w.requestFeature(Window.FEATURE_NO_TITLE);
            WindowManager.LayoutParams lp = w.getAttributes();
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
            lp.dimAmount = 0.6f;
            lp.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            w.setAttributes(lp);
            w.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        setContentView(buildContentView());
    }

    private View buildContentView() {
        Context ctx = getContext();

        FrameLayout root = new FrameLayout(ctx);
        root.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        LinearLayout panel = new LinearLayout(ctx);
        panel.setOrientation(LinearLayout.VERTICAL);
        FrameLayout.LayoutParams panelLp = new FrameLayout.LayoutParams(
                dp(340), ViewGroup.LayoutParams.WRAP_CONTENT);
        panelLp.gravity = Gravity.END | Gravity.CENTER_VERTICAL;
        panelLp.rightMargin = dp(24);
        panelLp.bottomMargin = dp(16);
        panelLp.topMargin = dp(16);
        panel.setLayoutParams(panelLp);
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.parseColor("#ff1f1f24"));
        bg.setCornerRadius(dp(10));
        panel.setBackground(bg);
        panel.setPadding(0, dp(8), 0, dp(8));
        root.addView(panel);

        // Header Title
        TextView title = new TextView(ctx);
        title.setText(getStringByName("mali_tab_title", "Mali GPU Optimizations"));
        title.setTextColor(Color.WHITE);
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f);
        title.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams titleLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        titleLp.bottomMargin = dp(4);
        title.setLayoutParams(titleLp);
        panel.addView(title);

        TextView subtitle = new TextView(ctx);
        subtitle.setText("Tuned for Mali-G/Immortalis architectures");
        subtitle.setTextColor(Color.parseColor("#ff888e99"));
        subtitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f);
        subtitle.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams subLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        subLp.bottomMargin = dp(8);
        subtitle.setLayoutParams(subLp);
        panel.addView(subtitle);

        ScrollView scroll = new ScrollView(ctx);
        LinearLayout.LayoutParams scrollLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(380));
        scrollLp.leftMargin = dp(16);
        scrollLp.rightMargin = dp(16);
        scroll.setLayoutParams(scrollLp);
        panel.addView(scroll);

        LinearLayout body = new LinearLayout(ctx);
        body.setOrientation(LinearLayout.VERTICAL);
        body.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        scroll.addView(body);

        // 1. DXVK Async Pipeline Compilation
        cbAsync = createCheckBox("DXVK Async Shader Compilation",
                "Compiles Vulkan pipelines asynchronously to eliminate shader stutters on Mali.",
                settings.dxvkAsync, (buttonView, isChecked) -> {
                    settings.dxvkAsync = isChecked;
                    saveAndApply();
                });
        body.addView(cbAsync);

        // 2. Compiler Worker Threads
        LinearLayout threadsRow = new LinearLayout(ctx);
        threadsRow.setOrientation(LinearLayout.HORIZONTAL);
        threadsRow.setLayoutParams(rowLp());
        TextView tvThreadsHeader = new TextView(ctx);
        tvThreadsHeader.setText("Shader Compiler Threads");
        tvThreadsHeader.setTextColor(Color.WHITE);
        tvThreadsHeader.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f);
        LinearLayout.LayoutParams thLp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        tvThreadsHeader.setLayoutParams(thLp);
        threadsRow.addView(tvThreadsHeader);

        tvThreadsValue = new TextView(ctx);
        tvThreadsValue.setText(String.valueOf(settings.compilerThreads));
        tvThreadsValue.setTextColor(Color.parseColor("#ffaaaaaa"));
        tvThreadsValue.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f);
        threadsRow.addView(tvThreadsValue);
        body.addView(threadsRow);

        sbThreads = new SeekBar(ctx);
        sbThreads.setMax(7); // 1..8
        sbThreads.setProgress(settings.compilerThreads - 1);
        sbThreads.setLayoutParams(seekBarLp());
        sbThreads.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int val = progress + 1;
                tvThreadsValue.setText(String.valueOf(val));
                if (fromUser) {
                    settings.compilerThreads = val;
                    saveAndApply();
                }
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        body.addView(sbThreads);

        body.addView(divider());

        // 3. Tessellation Throttle (Tess Factor)
        LinearLayout tessRow = new LinearLayout(ctx);
        tessRow.setOrientation(LinearLayout.HORIZONTAL);
        tessRow.setLayoutParams(rowLp());
        TextView tvTessHeader = new TextView(ctx);
        tvTessHeader.setText("Max Tessellation Factor");
        tvTessHeader.setTextColor(Color.WHITE);
        tvTessHeader.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f);
        tvTessHeader.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        tessRow.addView(tvTessHeader);

        tvTessValue = new TextView(ctx);
        tvTessValue.setText(settings.maxTessFactor + "x (Mali Boost)");
        tvTessValue.setTextColor(Color.parseColor("#ff38bdf8"));
        tvTessValue.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f);
        tessRow.addView(tvTessValue);
        body.addView(tessRow);

        TextView tvTessDesc = new TextView(ctx);
        tvTessDesc.setText("Limits geometry bottleneck on Mali GPUs without visual loss. 8x is optimal.");
        tvTessDesc.setTextColor(Color.parseColor("#ff888e99"));
        tvTessDesc.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f);
        tvTessDesc.setLayoutParams(descLp());
        body.addView(tvTessDesc);

        sbTess = new SeekBar(ctx);
        sbTess.setMax(3); // 8, 16, 32, 64
        sbTess.setProgress(tessToIndex(settings.maxTessFactor));
        sbTess.setLayoutParams(seekBarLp());
        sbTess.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int val = TESS_VALUES[progress];
                tvTessValue.setText(val + "x" + (val <= 16 ? " (Mali Boost)" : ""));
                if (fromUser) {
                    settings.maxTessFactor = val;
                    saveAndApply();
                }
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        body.addView(sbTess);

        body.addView(divider());

        // 4. WSI Present Mode (Mailbox vs FIFO)
        LinearLayout presentRow = new LinearLayout(ctx);
        presentRow.setOrientation(LinearLayout.HORIZONTAL);
        presentRow.setLayoutParams(rowLp());
        TextView tvPresentHeader = new TextView(ctx);
        tvPresentHeader.setText("Vulkan Present Mode");
        tvPresentHeader.setTextColor(Color.WHITE);
        tvPresentHeader.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f);
        tvPresentHeader.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        presentRow.addView(tvPresentHeader);

        tvPresentModeValue = new TextView(ctx);
        tvPresentModeValue.setText(settings.wsiPresentMode == 0 ? "Mailbox (Low Lag)" : "FIFO (VSync)");
        tvPresentModeValue.setTextColor(Color.parseColor("#ff38bdf8"));
        tvPresentModeValue.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f);
        presentRow.addView(tvPresentModeValue);
        body.addView(presentRow);

        tvPresentModeValue.setOnClickListener(v -> {
            settings.wsiPresentMode = (settings.wsiPresentMode == 0) ? 1 : 0;
            tvPresentModeValue.setText(settings.wsiPresentMode == 0 ? "Mailbox (Low Lag)" : "FIFO (VSync)");
            saveAndApply();
        });

        // 5. Box64 Fast NAN & Rounding
        cbFastMath = createCheckBox("Fast Math & Rounding (Box64)",
                "Uses fast hardware float conversions on ARM Cortex cores.",
                settings.fastMath, (buttonView, isChecked) -> {
                    settings.fastMath = isChecked;
                    saveAndApply();
                });
        body.addView(cbFastMath);

        // 6. Relaxed Strong Memory Barriers
        cbRelaxedMem = createCheckBox("Relaxed Memory Barriers",
                "Relaxes x86 memory barriers for up to 15% CPU speedup.",
                settings.relaxedMemory, (buttonView, isChecked) -> {
                    settings.relaxedMemory = isChecked;
                    saveAndApply();
                });
        body.addView(cbRelaxedMem);

        // 7. Box64 BigBlock JIT Level
        LinearLayout bbRow = new LinearLayout(ctx);
        bbRow.setOrientation(LinearLayout.HORIZONTAL);
        bbRow.setLayoutParams(rowLp());
        TextView tvBbHeader = new TextView(ctx);
        tvBbHeader.setText("Dynarec BigBlock Level");
        tvBbHeader.setTextColor(Color.WHITE);
        tvBbHeader.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f);
        tvBbHeader.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        bbRow.addView(tvBbHeader);

        tvBigBlockValue = new TextView(ctx);
        tvBigBlockValue.setText("Level " + settings.bigBlockLevel);
        tvBigBlockValue.setTextColor(Color.parseColor("#ffaaaaaa"));
        tvBigBlockValue.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f);
        bbRow.addView(tvBigBlockValue);
        body.addView(bbRow);

        sbBigBlock = new SeekBar(ctx);
        sbBigBlock.setMax(2); // 1..3
        sbBigBlock.setProgress(settings.bigBlockLevel - 1);
        sbBigBlock.setLayoutParams(seekBarLp());
        sbBigBlock.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int val = progress + 1;
                tvBigBlockValue.setText("Level " + val);
                if (fromUser) {
                    settings.bigBlockLevel = val;
                    saveAndApply();
                }
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        body.addView(sbBigBlock);

        // 8. Safe GPL (Graphics Pipeline Library)
        cbSafeGpl = createCheckBox("Disable GPL (Prevent Mali Crashes)",
                "Bypasses buggy Vulkan GPL drivers on MediaTek/Exynos chips.",
                settings.safeGpl, (buttonView, isChecked) -> {
                    settings.safeGpl = isChecked;
                    saveAndApply();
                });
        body.addView(cbSafeGpl);

        body.addView(divider());

        // Reset to Defaults Button
        TextView btnReset = new TextView(ctx);
        btnReset.setText("Reset to Optimal Mali Defaults");
        btnReset.setTextColor(Color.parseColor("#ffef4444"));
        btnReset.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f);
        btnReset.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams resetLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(32));
        resetLp.topMargin = dp(8);
        btnReset.setLayoutParams(resetLp);
        btnReset.setOnClickListener(v -> resetDefaults());
        body.addView(btnReset);

        // Close Button
        TextView btnClose = new TextView(ctx);
        btnClose.setText(getStringByName("rts_gesture_settings_close", "Close"));
        btnClose.setTextColor(Color.parseColor("#fff0f0f0"));
        btnClose.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f);
        btnClose.setGravity(Gravity.CENTER);
        btnClose.setBackgroundColor(Color.parseColor("#ff3b82f6"));
        LinearLayout.LayoutParams btnLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(32));
        btnLp.topMargin = dp(10);
        btnLp.leftMargin = dp(32);
        btnLp.rightMargin = dp(32);
        btnClose.setLayoutParams(btnLp);
        btnClose.setOnClickListener(v -> dismiss());
        panel.addView(btnClose);

        return root;
    }

    private CheckBox createCheckBox(String label, String desc, boolean checked,
                                     android.widget.CompoundButton.OnCheckedChangeListener listener) {
        Context ctx = getContext();
        CheckBox cb = new CheckBox(ctx);
        cb.setText(label + "\n" + desc);
        cb.setTextColor(Color.WHITE);
        cb.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f);
        cb.setChecked(checked);
        cb.setOnCheckedChangeListener(listener);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.topMargin = dp(6);
        lp.bottomMargin = dp(4);
        cb.setLayoutParams(lp);
        return cb;
    }

    private void resetDefaults() {
        settings.resetToDefaults();
        saveAndApply();
        cbAsync.setChecked(settings.dxvkAsync);
        cbFastMath.setChecked(settings.fastMath);
        cbRelaxedMem.setChecked(settings.relaxedMemory);
        cbSafeGpl.setChecked(settings.safeGpl);
        sbThreads.setProgress(settings.compilerThreads - 1);
        sbTess.setProgress(tessToIndex(settings.maxTessFactor));
        sbBigBlock.setProgress(settings.bigBlockLevel - 1);
        tvThreadsValue.setText(String.valueOf(settings.compilerThreads));
        tvTessValue.setText(settings.maxTessFactor + "x (Mali Boost)");
        tvBigBlockValue.setText("Level " + settings.bigBlockLevel);
        tvPresentModeValue.setText(settings.wsiPresentMode == 0 ? "Mailbox (Low Lag)" : "FIFO (VSync)");
        Toast.makeText(getContext(), "Mali optimal defaults restored", Toast.LENGTH_SHORT).show();
    }

    private void saveAndApply() {
        Context ctx = getContext();
        settings.save(ctx);
        BhMaliWriter.applyFromPrefs(ctx);
    }

    private int tessToIndex(int factor) {
        for (int i = 0; i < TESS_VALUES.length; i++) {
            if (TESS_VALUES[i] == factor) return i;
        }
        return 0;
    }

    private LinearLayout.LayoutParams rowLp() {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.topMargin = dp(6);
        lp.bottomMargin = dp(2);
        return lp;
    }

    private LinearLayout.LayoutParams seekBarLp() {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.topMargin = dp(2);
        lp.bottomMargin = dp(4);
        return lp;
    }

    private LinearLayout.LayoutParams descLp() {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.topMargin = dp(1);
        lp.bottomMargin = dp(4);
        return lp;
    }

    private View divider() {
        View v = new View(getContext());
        v.setBackgroundColor(Color.parseColor("#22ffffff"));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(1));
        lp.topMargin = dp(8);
        lp.bottomMargin = dp(6);
        v.setLayoutParams(lp);
        return v;
    }

    private int dp(int v) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int) (v * density + 0.5f);
    }

    private String getStringByName(String name, String fallback) {
        Context c = getContext();
        int id = c.getResources().getIdentifier(name, "string", c.getPackageName());
        return id != 0 ? c.getString(id) : fallback;
    }

    public static void show(Context ctx) {
        try {
            BhMaliDialog d = new BhMaliDialog(ctx);
            d.setCancelable(false);
            d.show();
        } catch (Exception ignored) {}
    }
}
