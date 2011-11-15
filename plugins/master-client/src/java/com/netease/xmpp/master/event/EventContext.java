package com.netease.xmpp.master.event;

import org.jboss.netty.channel.Channel;

/**
 * Context associate with the event. Including the dispatcher object, event source(Channel, null if
 * the event is not triggered by channel), event data, event type.
 * 
 * @author jiaozhihui@corp.netease.com
 */
public class EventContext {
    /**
     * Event dispatcher object.
     */
    private EventDispatcher dispatcher = null;
    /**
     * Event source(null if the event is not triggered by channel).
     */
    private Channel channel = null;
    /**
     * Event type, defined in {@link EventType}.
     */
    private EventType event = null;
    /**
     * Event data.
     */
    private Object data = null;

    public EventContext(EventDispatcher dispatcher, Channel channel, Object data, EventType event) {
        this.dispatcher = dispatcher;
        this.channel = channel;
        this.data = data;
        this.event = event;
    }

    public Channel getChannel() {
        return channel;
    }

    public EventType getEvent() {
        return event;
    }

    public Object getData() {
        return data;
    }

    public EventDispatcher getDispatcher() {
        return dispatcher;
    }
}
