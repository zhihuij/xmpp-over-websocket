package com.netease.xmpp.master.event;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jboss.netty.channel.Channel;

/**
 * Event dispatcher.
 * 
 * @author jiaozhihui@corp.netease.com
 */
public class EventDispatcher {
    private static EventDispatcher instance = null;

    public static EventDispatcher getInstance() {
        if (instance == null) {
            instance = new EventDispatcher();
        }

        return instance;
    }

    /**
     * Event, event handler mapping.
     */
    private Map<EventType, EventHandler> handlerMap = new ConcurrentHashMap<EventType, EventHandler>();

    private EventDispatcher() {
        // Do nothing
    }

    /**
     * Register event and its handler.
     * 
     * @param event
     *            event type, {@link EventType}
     * @param handler
     *            event handler
     */
    public void registerEvent(EventHandler handler, EventType... eventList) {
        for (EventType event : eventList) {
            handlerMap.put(event, handler);
        }
    }

    /**
     * Delete event.
     * 
     * @param event
     *            event type
     */
    public void deleteEvent(EventType event) {
        handlerMap.remove(event);
    }

    /**
     * Dispatch the specific event with event source and data.
     * 
     * @param channel
     *            the event source, null if this event is not triggered by Channel
     * @param data
     *            event data
     * @param event
     *            event type
     */
    public void dispatchEvent(Channel channel, Object data, EventType event) {
        EventHandler handler = handlerMap.get(event);
        if (handler != null) {
            try {
                handler.handle(new EventContext(this, channel, data, event));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
