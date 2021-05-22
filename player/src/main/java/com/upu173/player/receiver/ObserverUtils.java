package com.upu173.player.receiver;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by netease on 17/9/25.
 */

public class ObserverUtils {

    public  static <T> void notifyObservers(List<Observer<T>> observers, T result) {
        if (observers == null || observers.isEmpty()) {
            return;
        }

        // 创建副本，为了使得回调到app后，app如果立即注销观察者，会造成List异常。
        List<Observer<T>> copy = new ArrayList<>(observers.size());
        copy.addAll(observers);

        for (Observer<T> o : copy) {
            o.onEvent(result);
        }
    }

    public static  <T> void registerObservers(List<Observer<T>> observers, final Observer<T> observer, boolean register) {
        if (observers == null || observer == null) {
            return;
        }

        if (register) {
            observers.add(observer);
        } else {
            observers.remove(observer);
        }
    }
}
