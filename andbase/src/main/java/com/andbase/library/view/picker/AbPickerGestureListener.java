
package com.andbase.library.view.picker;

import android.view.MotionEvent;

final class AbPickerGestureListener extends android.view.GestureDetector.SimpleOnGestureListener {

    final AbPickerView loopView;

    AbPickerGestureListener(AbPickerView loopview) {
        loopView = loopview;
    }

    @Override
    public final boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        loopView.scrollBy(velocityY);
        return true;
    }
}
