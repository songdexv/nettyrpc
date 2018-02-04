package com.songdexv.nettyrpc.service;

import java.util.List;

/**
 * Created by songdexv on 2018/2/2.
 */
public interface PersonService {
    List<Person> generatePerson(String name, int num);
}
