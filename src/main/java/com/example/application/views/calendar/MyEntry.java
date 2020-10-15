package com.example.application.views.calendar;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

import javax.validation.constraints.NotNull;

import org.vaadin.stefan.fullcalendar.Entry;
import org.vaadin.stefan.fullcalendar.JsonUtils;
import org.vaadin.stefan.fullcalendar.Resource;

import elemental.json.Json;
import elemental.json.JsonObject;

public class MyEntry extends Entry {

	private boolean done;
	
	private String colorDone;


	public boolean isDone() {
		return done;
	}

	public void setDone(boolean done) {
		this.done = done;
	}

	public void assignResources(List<Resource> asList) {
	}
	
	public String getColorDone() {
		if(isDone()) {
			colorDone ="green";
		}
		else {
			colorDone ="red";
		}
		
		return colorDone;
	}

	public void setColorDone (String colorDone) {
		this.colorDone = colorDone;
	}
	
	/**
     * Converts the content of this instance to json to be sent to the client.
     *
     * @return json
     */
	@Override
    protected JsonObject toJson() {
		
		JsonObject jsonObject = super.toJson();
		
        jsonObject.put("done", JsonUtils.toJsonValue(isDone()));
        jsonObject.put("colorDone", JsonUtils.toJsonValue(getColorDone()));
        
        return jsonObject;
    }
    
	@Override
    protected void update(@NotNull JsonObject object) {
		
		super.update(object);
		
        JsonUtils.updateBoolean(object, "done", this::setDone);
 //       JsonUtils.updateString(object, "colorDone", this::setColorDone);
    
    }
}
