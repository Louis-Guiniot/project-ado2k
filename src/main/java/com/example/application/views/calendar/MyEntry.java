package com.example.application.views.calendar;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.validation.constraints.NotNull;

import org.vaadin.stefan.fullcalendar.Entry;
import org.vaadin.stefan.fullcalendar.JsonUtils;
import org.vaadin.stefan.fullcalendar.Resource;
import org.vaadin.stefan.fullcalendar.Timezone;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.api.client.util.DateTime;

import elemental.json.Json;
import elemental.json.JsonObject;

public class MyEntry extends Entry {

	private boolean done;
	
	private String colorDone;

	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime editDate = LocalDateTime.now();

	private String eTag = UUID.randomUUID().toString();
	
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
	
	public LocalDateTime getEditDate() {
		return editDate;
	}

	public void setEditDate(LocalDateTime editDate) {
		this.editDate = editDate;
	}

	public String geteTag() {
		return eTag;
	}

	public void seteTag(String eTag) {
		this.eTag = eTag;
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
        jsonObject.put("editDate", JsonUtils.toJsonValue(getEditDate()));
        jsonObject.put("eTag", JsonUtils.toJsonValue(geteTag()));
        
        return jsonObject;
    }
    
	@Override
    protected void update(@NotNull JsonObject object) {
		
		super.update(object);
		
        JsonUtils.updateBoolean(object, "done", this::setDone);
//        JsonUtils.updateDateTime(object, "editDate", this::setEditDate,null);
        JsonUtils.updateString(object, "eTag", this::seteTag);
    
    }


}
