package com.songdexv.nettyrpc.service.impl;

import com.songdexv.nettyrpc.server.RpcService;
import com.songdexv.nettyrpc.service.HelloService;
import com.songdexv.nettyrpc.service.Person;

/**
 * Created by Administrator on 2018/2/3.
 */
@RpcService(HelloService.class)
public class HelloSerivceImpl implements HelloService {
    @Override
    public String hello(String name) {
        return "hello! " + name;
    }

    @Override
    public String hello(Person person) {
        return "hello! " + person.getFirstName() + " " + person.getLastName();
    }
}
