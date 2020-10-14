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

	public boolean isDone() {
		return done;
	}

	public void setDone(boolean done) {
		this.done = done;
	}

	public void assignResources(List<Resource> asList) {
	}
	
    /**
     * Converts the content of this instance to json to be sent to the client.
     *
     * @return json
     */
    @SuppressWarnings("deprecation")
	protected JsonObject toJson() {
        JsonObject jsonObject = Json.createObject();
        jsonObject.put("id", JsonUtils.toJsonValue(getId()));
        jsonObject.put("title", JsonUtils.toJsonValue(getTitle()));

        boolean fullDayEvent = isAllDay();
        jsonObject.put("allDay", JsonUtils.toJsonValue(fullDayEvent));

        jsonObject.put("start", JsonUtils.toJsonValue(getStartUTC() == null ? null : getStartTimezone().formatWithZoneId(getStartUTC())));
        jsonObject.put("end", JsonUtils.toJsonValue(getEndUTC() == null ? null : getEndTimezone().formatWithZoneId(getEndUTC())));
        jsonObject.put("editable", isEditable());
        Optional.ofNullable(getColor()).ifPresent(s -> jsonObject.put("color", s));
        jsonObject.put("rendering", JsonUtils.toJsonValue(getRenderingMode()));

        jsonObject.put("daysOfWeek", JsonUtils.toJsonValue(getRecurringDaysOfWeeks() == null || getRecurringDaysOfWeeks().isEmpty() ? null : getRecurringDaysOfWeeks().stream().map(dayOfWeek -> dayOfWeek == DayOfWeek.SUNDAY ? 0 : dayOfWeek.getValue())));
        jsonObject.put("startTime", JsonUtils.toJsonValue(getRecurringStartTime()));
        jsonObject.put("endTime", JsonUtils.toJsonValue(getRecurringEndTime()));
        jsonObject.put("startRecur", JsonUtils.toJsonValue(getRecurringStartDate() == null ? null : getStartTimezone().formatWithZoneId(getRecurringStartDate())));
        jsonObject.put("endRecur", JsonUtils.toJsonValue(getRecurringEndDate() == null ? null : getEndTimezone().formatWithZoneId(getRecurringEndDate())));

        jsonObject.put("done", JsonUtils.toJsonValue(isDone()));
        jsonObject.put("description", JsonUtils.toJsonValue(getDescription()));

        return jsonObject;
    }
    
    protected void update(@NotNull JsonObject object) {
        String id = object.getString("id");
        if (!this.getId().equals(id)) {
            throw new IllegalArgumentException("IDs are not matching.");
        }

        JsonUtils.updateString(object, "title", this::setTitle);
        JsonUtils.updateBoolean(object, "editable", this::setEditable);
        JsonUtils.updateBoolean(object, "allDay", this::setAllDay);
        JsonUtils.updateDateTime(object, "start", this::setStart, getStartTimezone());
        JsonUtils.updateDateTime(object, "end", this::setEnd, getEndTimezone());
        JsonUtils.updateString(object, "color", this::setColor);
        JsonUtils.updateBoolean(object, "done", this::setDone);
    }
	
}
