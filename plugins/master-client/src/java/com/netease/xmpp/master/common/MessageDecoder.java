package com.netease.xmpp.master.common;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

/**
 * Message decoder for server and client.
 * 
 * @author jiaozhihui@corp.netease.com
 */
public class MessageDecoder extends FrameDecoder {
    /**
     * Minimal message size, without actual data.
     */
    private static final int MIN_MESSAGE_SIZE = 9;

    @Override
    protected Object decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer)
            throws Exception {
        if (buffer.readableBytes() >= MIN_MESSAGE_SIZE) {
            buffer.markReaderIndex();

            byte flag = buffer.readByte();
            int version = buffer.readInt();
            int length = buffer.readInt();

            if (buffer.readableBytes() >= length) {
                ChannelBuffer data = buffer.readBytes(length);
                return new Message(flag, version, length, data.array());
            } else {
                buffer.resetReaderIndex();
                return null;
            }
        } else {
            return null;
        }
    }
}
