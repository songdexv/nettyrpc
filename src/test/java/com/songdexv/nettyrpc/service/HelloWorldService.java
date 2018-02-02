package com.songdexv.nettyrpc.service;

import com.songdexv.nettyrpc.service.model.Person;

/**
 * Created by songdexv on 2018/2/2.
 */
public interface HelloWorldService {
    public String hello(String name);

    public String hello(Person person);
}
