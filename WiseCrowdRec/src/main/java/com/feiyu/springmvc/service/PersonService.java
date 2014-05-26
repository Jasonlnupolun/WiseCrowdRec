package com.feiyu.springmvc.service;

import com.feiyu.springmvc.model.Person;

public interface PersonService {
	public Person getRandom();
	public Person getById(Long id);
	public void save(Person person);
}
