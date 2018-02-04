package com.songdexv.nettyrpc.service.impl;

import com.songdexv.nettyrpc.server.RpcService;
import com.songdexv.nettyrpc.service.PersonService;
import com.songdexv.nettyrpc.service.Person;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/2/3.
 */
@RpcService(PersonService.class)
public class PersonServiceImpl implements PersonService {
    @Override
    public List<Person> generatePerson(String name, int num) {
        List<Person> list = new ArrayList<>(num);
        for (int i = 0; i < num; i++) {
            list.add(new Person(Integer.toString(i), name));
        }
        return list;
    }
}
