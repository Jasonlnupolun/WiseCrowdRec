package com.feiyu.service;

import com.feiyu.model.Person;

public interface PersonService {
	public Person getRandom();
	public Person getById(Long id);
	public void save(Person person);
}
