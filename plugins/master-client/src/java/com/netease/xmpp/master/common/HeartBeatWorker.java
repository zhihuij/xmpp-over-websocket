package com.netease.xmpp.master.common;

import java.util.concurrent.atomic.AtomicBoolean;

import org.jboss.netty.channel.Channel;

public class HeartBeatWorker {
    private static final Message HEATBEAT = new Message(MessageFlag.FLAG_HEATBEAT, 0, 0, null);

    private Channel channel = null;

    private Thread workerThread = null;

    private AtomicBoolean runFlag = new AtomicBoolean(true);

    public HeartBeatWorker(Channel channel) {
        this.channel = channel;
        workerThread = new Thread(new Worker());
    }

    public void start() {
        runFlag.set(true);
        workerThread.start();
    }

    public void stop() {
        try {
            runFlag.set(false);
            workerThread.join();
        } catch (InterruptedException e) {
            // Do nothing
        }
    }

    class Worker implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(ConfigConst.HEARTBEAT_INTERVAL * 1000);

                    if (!runFlag.get()) {
                        // stop the worker
                        break;
                    }

                    channel.write(HEATBEAT);
                } catch (InterruptedException e) {
                    // Do nothing
                }
            }
        }
    }
}
