
package com.andbase.library.view.picker;

import android.os.Handler;
import android.os.Message;


final class AbPickerMessageHandler extends Handler {
    public static final int WHAT_INVALIDATE_LOOP_VIEW = 1000;
    public static final int WHAT_SMOOTH_SCROLL = 2000;
    public static final int WHAT_ITEM_SELECTED = 3000;

    final AbPickerView loopview;

    AbPickerMessageHandler(AbPickerView loopview) {
        this.loopview = loopview;
    }

    @Override
    public final void handleMessage(Message msg) {
        switch (msg.what) {
            case WHAT_INVALIDATE_LOOP_VIEW:
                loopview.invalidate();
                break;

            case WHAT_SMOOTH_SCROLL:
                loopview.smoothScroll(AbPickerView.ACTION.FLING);
                break;

            case WHAT_ITEM_SELECTED:
                loopview.onItemSelected();
                break;
        }
    }

}
