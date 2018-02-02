package com.songdexv.nettyrpc.protocol;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * Created by songdexv on 2018/2/1.
 */
public class RpcDecoder extends LengthFieldBasedFrameDecoder {
    private Class<?> genericClass;

    public RpcDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int
            initialBytesToStrip, Class<?> genericClass) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
        this.genericClass = genericClass;
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf frame = (ByteBuf) super.decode(ctx, in);
        if (frame == null) {
            return null;
        }
        byte[] data = new byte[frame.readableBytes()];
        frame.readBytes(data);
        return ProtostuffSerializationUtil.deserialize(data, genericClass);
    }
}
