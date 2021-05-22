package com.netease.neliveplayer.playerkit.common.log;

import android.content.Context;
import android.util.Log;

/**
 * @author netease
 */

public class LogUtil {

    private static boolean inited = false;

    public static void init(Context context) {
        if (context == null) {
            return;
        }
        inited = true;
    }

    public static void info(String msg) {
        if (!inited) {
            return;
        }

        i("player_info", msg);
    }

    public static void debug(String msg) {
        if (!inited) {
            return;
        }

        d("player_info", msg);
    }


    public static void ui(String msg) {
        if (!inited) {
            return;
        }

        i("player_ui", msg);
    }

    public static void app(String msg) {
        if (!inited) {
            return;
        }

        i("player_app", msg);
    }

    public static void error(String msg) {
        if (!inited) {
            return;
        }

        e("player_error", msg);
    }

    public static void error(String msg, Throwable e) {
        if (!inited) {
            return;
        }

        e("player_error", msg, e);
    }


    public static final void v(String tag, String msg) {
        Log.v(tag, buildMessage(msg));
    }

    public static final void v(String tag, String msg, Throwable thr) {
        Log.v(tag, buildMessage(msg), thr);
    }

    public static final void d(String tag, String msg) {
        Log.d(tag, buildMessage(msg));
    }

    public static final void d(String tag, String msg, Throwable thr) {
        Log.d(tag, buildMessage(msg), thr);
    }

    public static final void i(String tag, String msg) {
        Log.i(tag, buildMessage(msg));
    }

    public static final void i(String tag, String msg, Throwable thr) {
        Log.i(tag, buildMessage(msg), thr);
    }

    public static final void w(String tag, String msg) {
        Log.w(tag, buildMessage(msg));
    }

    public static final void w(String tag, String msg, Throwable thr) {
        Log.w(tag, buildMessage(msg), thr);
    }

    public static final void w(String tag, Throwable thr) {
        Log.w(tag, buildMessage(""), thr);
    }

    public static final void e(String tag, String msg) {
        Log.e(tag, buildMessage(msg));
    }

    public static final void e(String tag, String msg, Throwable thr) {
        Log.e(tag, buildMessage(msg), thr);
    }
    private static String buildMessage(String msg) {
        return msg;
    }

}
