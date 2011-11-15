package com.netease.xmpp.master.client;

import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

import com.netease.xmpp.master.common.MessageFlag;
import com.netease.xmpp.master.common.Message;
import com.netease.xmpp.master.event.EventDispatcher;
import com.netease.xmpp.master.event.EventType;

public class ServerChannelHandler extends SimpleChannelHandler {
    private static Logger logger = Logger.getLogger(ServerChannelHandler.class);
    private EventDispatcher dispatcher = null;

    public ServerChannelHandler(EventDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
        Channel channel = e.getChannel();
        Message message = (Message) e.getMessage();
        if (message == null) {
            return;
        }
        byte flag = message.getFlag();
        if (flag == MessageFlag.FLAG_HEATBEAT) {
            dispatcher.dispatchEvent(channel, null, EventType.CLIENT_SERVER_HEARTBEAT);
        } else if (flag == MessageFlag.FLAG_SERVER_INFO_ACCEPTED) {
            dispatcher.dispatchEvent(channel, message, EventType.CLIENT_SERVER_INFO_ACCEPTED);
        }
    }

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
        Channel channel = e.getChannel();

        logger.debug("CLIENT - CONNECTED: " + channel.getRemoteAddress());

        dispatcher.dispatchEvent(channel, null, EventType.CLIENT_SERVER_CONNECTED);
    }

    @Override
    public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
        Channel channel = e.getChannel();

        logger.debug("CLIENT - DISCONNECTED: " + channel.getRemoteAddress());

        dispatcher.dispatchEvent(null, null, EventType.CLIENT_SERVER_DISCONNECTED);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
        Channel channel = e.getChannel();

        logger.debug("CLIENT - EXCEPTION: " + channel.getRemoteAddress());
    }

    @Override
    public void writeRequested(ChannelHandlerContext ctx, MessageEvent e) {
        Message message = (Message) e.getMessage();
        int bufSize = message.getMessageSize();
        ChannelBuffer buffer = ChannelBuffers.directBuffer(bufSize);
        buffer.writeByte(message.getFlag());
        buffer.writeInt(message.getVersion());
        buffer.writeInt(message.getDataLength());
        if (message.getDataLength() > 0) {
            buffer.writeBytes(message.getData());
        }

        Channels.write(ctx, e.getFuture(), buffer);
    }
}
