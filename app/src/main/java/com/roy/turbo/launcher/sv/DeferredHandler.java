package com.roy.turbo.launcher.sv;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.MessageQueue;
import android.util.Pair;

import java.util.LinkedList;
import java.util.ListIterator;

public class DeferredHandler {
    private final LinkedList<Pair<Runnable, Integer>> mQueue = new LinkedList<>();
    private final MessageQueue mMessageQueue = Looper.myQueue();
    private final Impl mHandler = new Impl();

    private class Impl extends Handler implements MessageQueue.IdleHandler {
        public void handleMessage(Message msg) {
            Pair<Runnable, Integer> p;
            Runnable r;
            synchronized (mQueue) {
                if (mQueue.size() == 0) {
                    return;
                }
                p = mQueue.removeFirst();
                r = p.first;
            }
            r.run();
            synchronized (mQueue) {
                scheduleNextLocked();
            }
        }

        public boolean queueIdle() {
            handleMessage(null);
            return false;
        }
    }

    private static class IdleRunnable implements Runnable {
        Runnable mRunnable;

        IdleRunnable(Runnable r) {
            mRunnable = r;
        }

        public void run() {
            mRunnable.run();
        }
    }

    public DeferredHandler() {
    }

    /** Schedule runnable to run after everything that's on the queue right now. */
    public void post(Runnable runnable) {
        post(runnable, 0);
    }
    public void post(Runnable runnable, int type) {
        synchronized (mQueue) {
            mQueue.add(new Pair<>(runnable, type));
            if (mQueue.size() == 1) {
                scheduleNextLocked();
            }
        }
    }

    /** Schedule runnable to run when the queue goes idle. */
    public void postIdle(final Runnable runnable) {
        postIdle(runnable, 0);
    }
    public void postIdle(final Runnable runnable, int type) {
        post(new IdleRunnable(runnable), type);
    }

    public void cancelRunnable(Runnable runnable) {
        synchronized (mQueue) {
            while (mQueue.remove(runnable)) { }
        }
    }
    public void cancelAllRunnablesOfType(int type) {
        synchronized (mQueue) {
            ListIterator<Pair<Runnable, Integer>> iter = mQueue.listIterator();
            Pair<Runnable, Integer> p;
            while (iter.hasNext()) {
                p = iter.next();
                if (p.second == type) {
                    iter.remove();
                }
            }
        }
    }

    public void cancel() {
        synchronized (mQueue) {
            mQueue.clear();
        }
    }

    /** Runs all queued Runnables from the calling thread. */
    public void flush() {
        LinkedList<Pair<Runnable, Integer>> queue = new LinkedList<>();
        synchronized (mQueue) {
            queue.addAll(mQueue);
            mQueue.clear();
        }
        for (Pair<Runnable, Integer> p : queue) {
            p.first.run();
        }
    }

    void scheduleNextLocked() {
        if (mQueue.size() > 0) {
            Pair<Runnable, Integer> p = mQueue.getFirst();
            Runnable peek = p.first;
            if (peek instanceof IdleRunnable) {
                mMessageQueue.addIdleHandler(mHandler);
            } else {
                mHandler.sendEmptyMessage(1);
            }
        }
    }
}
