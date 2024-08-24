package me.thepond;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;

import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;

public class Scheduler implements ServerTickEvents.EndTick {
    private static final PriorityBlockingQueue<Entry> TASKS = new PriorityBlockingQueue<Entry>(10, Comparator.comparingInt(a -> a.nextExecuteTick));
    private static int ticks = 0;

    public Scheduler() {
    }

    public static void schedule(int afterTicks, Runnable tasque) {
        Entry e = new Entry();
        e.nextExecuteTick = ticks + afterTicks;
        e.interval = afterTicks;
        e.timesToExecute = 1;
        e.tasque = tasque;
        TASKS.add(e);
    }

    public static void scheduleRepeatAfterInterval(int interval, int times, Runnable tasque) {
        Entry e = new Entry();
        e.nextExecuteTick = ticks + interval;
        e.interval = interval;
        e.timesToExecute = times;
        e.tasque = tasque;
        TASKS.add(e);
    }

    public static void scheduleRepeatNow(int interval, int times, Runnable tasque) {
        Entry e = new Entry();
        e.nextExecuteTick = ticks + 1;
        e.interval = interval;
        e.timesToExecute = times;
        e.tasque = tasque;
        TASKS.add(e);
    }

    @Override
    public void onEndTick(MinecraftServer server) {
        ++ticks;
        int initialSize = TASKS.size();

        for(int i = 0; i < initialSize; ++i) {

            Entry e = (Entry)TASKS.peek();

            if (e.nextExecuteTick > ticks) {
                continue;
            }

            e.tasque.run();

            try {
                TASKS.take();
            } catch (Exception var5) {
                throw new RuntimeException(var5);
            }

            --e.timesToExecute;

            if (e.timesToExecute > 0) {
                e.nextExecuteTick += e.interval;
                TASKS.add(e);
            }

        }
    }

    private static class Entry {
        int nextExecuteTick;
        int timesToExecute;
        int interval;
        Runnable tasque;

        private Entry() {
        }
    }
}