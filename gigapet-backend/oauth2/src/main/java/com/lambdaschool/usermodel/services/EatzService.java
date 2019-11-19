package com.lambdaschool.usermodel.services;

import com.lambdaschool.usermodel.models.Eatz;

import java.util.List;

public interface EatzService {
    List<Eatz> findAll();

    Eatz findEatzById(long id);

    Eatz findEatztByTitle(String name);

    void delete(long id);

    Eatz save(Eatz eatz,long id);

    Eatz update(Eatz eatz, long id);
}
