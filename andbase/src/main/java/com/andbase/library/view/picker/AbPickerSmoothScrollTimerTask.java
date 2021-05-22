package com.andbase.library.view.picker;

final class AbPickerSmoothScrollTimerTask implements Runnable {

    int realTotalOffset;
    int realOffset;
    int offset;
    final AbPickerView loopView;

    AbPickerSmoothScrollTimerTask(AbPickerView loopview, int offset) {
        this.loopView = loopview;
        this.offset = offset;
        realTotalOffset = Integer.MAX_VALUE;
        realOffset = 0;
    }

    @Override
    public final void run() {
        if (realTotalOffset == Integer.MAX_VALUE) {
            realTotalOffset = offset;
        }
        realOffset = (int) ((float) realTotalOffset * 0.1F);

        if (realOffset == 0) {
            if (realTotalOffset < 0) {
                realOffset = -1;
            } else {
                realOffset = 1;
            }
        }
        if (Math.abs(realTotalOffset) <= 0) {
            loopView.cancelFuture();
            loopView.handler.sendEmptyMessage(AbPickerMessageHandler.WHAT_ITEM_SELECTED);
        } else {
            loopView.totalScrollY = loopView.totalScrollY + realOffset;
            loopView.handler.sendEmptyMessage(AbPickerMessageHandler.WHAT_INVALIDATE_LOOP_VIEW);
            realTotalOffset = realTotalOffset - realOffset;
        }
    }
}
