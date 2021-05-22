package com.andbase.library.view.picker;

final class AbPickerOnItemSelectedRunnable implements Runnable {
    final AbPickerView loopView;

    AbPickerOnItemSelectedRunnable(AbPickerView loopview) {
        loopView = loopview;
    }

    @Override
    public final void run() {
        loopView.onItemSelectedListener.onItemSelected(loopView.getSelectedItem());
    }
}
