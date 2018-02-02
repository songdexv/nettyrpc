package com.songdexv.nettyrpc.client;

import java.lang.reflect.Proxy;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.songdexv.nettyrpc.client.proxy.IAsyncObjectProxy;
import com.songdexv.nettyrpc.client.proxy.ObjectProxy;
import com.songdexv.nettyrpc.registry.ServiceDiscovery;

/**
 * Created by songdexv on 2018/2/2.
 */
public class RPCClient {
    private ServiceDiscovery serviceDiscovery;

    private static ThreadPoolExecutor threadPoolExecutor;

    static {
        threadPoolExecutor =
                new ThreadPoolExecutor(16, 16, 600L, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(65536));
    }

    public RPCClient(ServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }

    public static <T> T createProxy(Class<T> interfaceClass) {
        return (T) Proxy
                .newProxyInstance(interfaceClass.getClassLoader(), new Class[] {interfaceClass}, new ObjectProxy<>
                        (interfaceClass));
    }

    public static <T> IAsyncObjectProxy createAsyncProxy(Class<T> interfaceClass) {
        return new ObjectProxy<>(interfaceClass);
    }

    public static void submit(Runnable task) {
        threadPoolExecutor.submit(task);
    }

    public void stop(){
        threadPoolExecutor.shutdown();
        serviceDiscovery.stop();
        ConnectionManager.getInstance().stop();
    }

}
