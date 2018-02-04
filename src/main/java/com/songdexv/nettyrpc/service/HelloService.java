package com.songdexv.nettyrpc.service;

/**
 * Created by songdexv on 2018/2/2.
 */
public interface HelloService {
    public String hello(String name);

    public String hello(Person person);
}
