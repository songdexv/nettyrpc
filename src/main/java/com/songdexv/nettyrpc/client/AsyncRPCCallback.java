package com.songdexv.nettyrpc.client;

/**
 * Created by songdexv on 2018/2/2.
 */
public interface AsyncRPCCallback {
    void success(Object result);

    void fail(Exception e);
}
