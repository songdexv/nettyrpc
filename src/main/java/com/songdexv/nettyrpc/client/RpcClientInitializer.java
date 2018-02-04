package com.songdexv.nettyrpc.client;

import com.songdexv.nettyrpc.protocol.RpcDecoder;
import com.songdexv.nettyrpc.protocol.RpcEncoder;
import com.songdexv.nettyrpc.protocol.RpcRequest;
import com.songdexv.nettyrpc.protocol.RpcResponse;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldPrepender;

/**
 * Created by songdexv on 2018/2/2.
 */
public class RpcClientInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new RpcDecoder(65536, 0, 4, 0, 4, RpcResponse.class));
        pipeline.addLast(new LengthFieldPrepender(4));
        pipeline.addLast(new RpcEncoder(RpcRequest.class));
        pipeline.addLast(new RpcClientHandler());
    }
}
