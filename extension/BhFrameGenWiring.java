package com.xj.winemu.sidebar;

import android.content.Context;
import android.view.View;

import java.lang.reflect.Method;

public class BhFrameGenWiring {

    private static View viewById(View root, String idName) {
        Context ctx = root.getContext();
        int id = ctx.getResources().getIdentifier(idName, "id", ctx.getPackageName());
        if (id == 0) return null;
        return root.findViewById(id);
    }

    /** Resolves Fragment.getView() reflectively so this extension does not need
     *  androidx on its compile classpath. */
    public static void bindFromFragment(Object frag) {
        if (frag == null) return;
        try {
            Method getView = frag.getClass().getMethod("getView");
            Object v = getView.invoke(frag);
            if (v instanceof View) bind((View) v);
        } catch (Throwable ignored) {}
    }

    public static void bind(final View root) {
        if (root == null) return;
        final Context ctx = root.getContext();

        final View container = viewById(root, "frame_gen_container");
        if (!BhVulkanIcdWriter.isVkAvailable(ctx)) {
            if (container != null) container.setVisibility(View.GONE);
            return;
        }
        if (container != null) container.setVisibility(View.VISIBLE);

        final View gearButton = viewById(root, "btn_frame_gen_settings");
        final View switchView = viewById(root, "switch_frame_gen");

        if (switchView != null) {
            BhFrameGenSettings settings = BhFrameGenSettings.load(ctx);

            invokeSetSwitch(switchView, settings.enabled);

            if (gearButton != null) {
                gearButton.setVisibility(settings.enabled ? View.VISIBLE : View.GONE);
                gearButton.setOnClickListener(v -> BhFrameGenDialog.show(ctx));
            }

            switchView.setOnClickListener(v -> {
                BhFrameGenSettings s = BhFrameGenSettings.load(ctx);
                boolean newState = !s.enabled;
                s.enabled = newState;
                invokeSetSwitch(v, newState);
                BhFrameGenWriter.writeEnabled(BhFrameGenWriter.resolveControlPath(ctx), newState);
                s.save(ctx);
                if (gearButton != null) {
                    gearButton.setVisibility(newState ? View.VISIBLE : View.GONE);
                }
            });
        }

        final View maliContainer = viewById(root, "mali_settings_container");
        final View maliGearButton = viewById(root, "btn_mali_settings");
        final View maliSwitchView = viewById(root, "switch_mali_optimizations");

        if (maliContainer != null) {
            maliContainer.setVisibility(View.VISIBLE);
        }

        if (maliSwitchView != null) {
            BhMaliSettings maliSettings = BhMaliSettings.load(ctx);
            invokeSetSwitch(maliSwitchView, maliSettings.enabled);

            if (maliGearButton != null) {
                maliGearButton.setVisibility(maliSettings.enabled ? View.VISIBLE : View.GONE);
                maliGearButton.setOnClickListener(v -> BhMaliDialog.show(ctx));
            }

            maliSwitchView.setOnClickListener(v -> {
                BhMaliSettings ms = BhMaliSettings.load(ctx);
                boolean newState = !ms.enabled;
                ms.enabled = newState;
                invokeSetSwitch(v, newState);
                ms.save(ctx);
                BhMaliWriter.applyFromPrefs(ctx);
                if (maliGearButton != null) {
                    maliGearButton.setVisibility(newState ? View.VISIBLE : View.GONE);
                }
            });
        }
    }

    /** SidebarSwitchItemView is part of the patched app, not on this extension's classpath. */
    private static void invokeSetSwitch(View view, boolean value) {
        try {
            Method m = view.getClass().getMethod("setSwitch", boolean.class);
            m.invoke(view, value);
        } catch (Throwable ignored) {}
    }
}
