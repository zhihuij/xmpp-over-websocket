package com.netease.xmpp.master.event;

import java.io.IOException;

/**
 * Handler interface for event.
 * 
 * @author jiaozhihui@corp.netease.com
 */
public interface EventHandler {
    /**
     * Handler the event.
     * 
     * @param ctx
     *            the event context object
     * @throws IOException
     */
    public void handle(EventContext ctx) throws IOException;
}
