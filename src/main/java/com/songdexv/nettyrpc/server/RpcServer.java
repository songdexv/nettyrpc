package com.songdexv.nettyrpc.server;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.songdexv.nettyrpc.protocol.RpcDecoder;
import com.songdexv.nettyrpc.protocol.RpcEncoder;
import com.songdexv.nettyrpc.protocol.RpcRequest;
import com.songdexv.nettyrpc.protocol.RpcResponse;
import com.songdexv.nettyrpc.server.registry.ServiceRegistry;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldPrepender;

/**
 * Created by songdexv on 2018/2/1.
 */
@Component
public class RpcServer implements ApplicationRunner, ApplicationContextAware {
    private static final Logger logger = LoggerFactory.getLogger(RpcServer.class);
    private String rpcServerAddress;
    @Autowired
    private ServiceRegistry serviceRegistry;

    private Map<String, Object> handlerMap = new HashMap<>();

    private static ThreadPoolExecutor threadPoolExecutor;

    private EventLoopGroup bossGroup = null;
    private EventLoopGroup workerGroup = null;

    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
        if (bossGroup == null && workerGroup == null) {
            bossGroup = new NioEventLoopGroup();
            workerGroup = new NioEventLoopGroup();
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).childHandler(
                    new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new RpcDecoder(65536, 0, 4, 0, 4, RpcRequest.class))
                                    .addLast(new LengthFieldPrepender(4)).addLast(new
                                    RpcEncoder(RpcResponse.class)).addLast(new RpcHandler(handlerMap));
                        }
                    }).option(ChannelOption.SO_BACKLOG, 128).childOption(ChannelOption.SO_KEEPALIVE, true);
            String[] array = rpcServerAddress.split(":");
            if (array.length == 2) {
                String host = array[0];
                int port = Integer.parseInt(array[1]);
                ChannelFuture future = bootstrap.bind(host, port).sync();
                logger.info("Server started on port {}", port);
                if (serviceRegistry != null) {
                    serviceRegistry.register(rpcServerAddress);
                }
                future.channel().closeFuture().sync();
            }
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, Object> serviceBeanMap = applicationContext.getBeansWithAnnotation(RpcService.class);
        if (serviceBeanMap != null) {
            for (Object serviceBean : serviceBeanMap.values()) {
                String interfaceName = serviceBean.getClass().getAnnotation(RpcService.class).value().getName();
                logger.info("------------loading service: {}, {}", interfaceName, serviceBean);
                handlerMap.put(interfaceName, serviceBean);
            }
        }
    }

    public static void submit(Runnable task) {
        if (threadPoolExecutor == null) {
            synchronized (RpcServer.class) {
                if (threadPoolExecutor == null) {
                    threadPoolExecutor = new ThreadPoolExecutor(16, 16, 600L, TimeUnit.SECONDS, new
                            ArrayBlockingQueue<Runnable>(65536), new ThreadPoolExecutor.AbortPolicy());
                }
            }
        }
        threadPoolExecutor.submit(task);
    }

    public RpcServer addService(String interfaceName, Object serviceBean) {
        if (!handlerMap.containsKey(interfaceName)) {
            logger.info("-------------Loading service: {}", interfaceName);
            handlerMap.put(interfaceName, serviceBean);
        }
        return this;
    }

    public void stop() {
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
    }

    @Value("${rpc.server.address}")
    public void setRpcServerAddress(String rpcServerAddress) {
        logger.debug("--------rpcServerAddress: " + rpcServerAddress + " --------");
        this.rpcServerAddress = rpcServerAddress;
    }
}
