package com.songdexv.nettyrpc.client.proxy;

import com.songdexv.nettyrpc.client.RPCFuture;

/**
 * Created by songdexv on 2018/2/2.
 */
public interface IAsyncObjectProxy {
    public RPCFuture call(String functionName, Object ... args);
}
