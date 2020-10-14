package com.example.application.data.service;

import java.util.ArrayList;
import java.util.List;

import com.example.application.data.entity.Person;
import com.vaadin.flow.component.grid.Grid;

public class PersonService {

	private Person person = new Person();
	private Grid<Person> grid = new Grid<>();
	

	
	public String update(Person bean) {
		return bean.toString();
		
	}
	
	public void store(Person bean) {
		person=bean;
//		grid.(person);
	}
	
	public String toString() {
		return (person.getFirstName());
	}
	
//	public List<Person> viewAll() {
////		return list;
//	}
	

}
