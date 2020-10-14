package com.example.application.data.entity;

import javax.persistence.Entity;
import com.example.application.data.AbstractEntity;

@Entity
public class Entry extends AbstractEntity {
	private String title;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	
}
