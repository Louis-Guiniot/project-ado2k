package com.example.application.views.calendar;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.checkbox.CheckboxGroupVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.data.binder.Binder;

import org.vaadin.stefan.fullcalendar.Entry;
import org.vaadin.stefan.fullcalendar.FullCalendar;
import org.vaadin.stefan.fullcalendar.Timezone;

import java.time.*;
import java.util.Set;

public class CalendarDialog extends Dialog {

	private static final String[] COLORS = { "tomato", "orange", "dodgerblue", "mediumseagreen", "gray", "slateblue",
			"violet" };
	private final DialogEntry dialogEntry;
	private final CustomDateTimePicker fieldStart;
	private final CustomDateTimePicker fieldEnd;
	private final CheckboxGroup<DayOfWeek> fieldRDays;

	public CalendarDialog(FullCalendar calendar, MyEntry entry, boolean newInstance) {
		this.dialogEntry = DialogEntry.of(entry, calendar.getTimezone());

		setCloseOnEsc(true);
		setCloseOnOutsideClick(true);

		setWidth("500px");

		// init fields

		TextField fieldTitle = new TextField("Title");
		ComboBox<String> fieldColor = new ComboBox<>("Color", COLORS);
		fieldColor.setPreventInvalidInput(false);
		fieldColor.setAllowCustomValue(true);
		fieldColor.addCustomValueSetListener(event -> fieldColor.setValue(event.getDetail()));
		fieldColor.setClearButtonVisible(true);

		TextArea fieldDescription = new TextArea("Description");

		Checkbox fieldRecurring = new Checkbox("Recurring event");
		Checkbox fieldAllDay = new Checkbox("All day event");

		fieldStart = new CustomDateTimePicker("Start");
		fieldEnd = new CustomDateTimePicker("End");

		boolean allDay = dialogEntry.allDay;
		fieldStart.setDateOnly(allDay);
		fieldEnd.setDateOnly(allDay);

		//checkbox per scegliere se fatto o no
		Checkbox done = new Checkbox("done");

		Span infoEnd = new Span(
				"End is always exclusive, e.g. for a 1 day event you need to set for instance 4th of May as start and 5th of May as end.");
		infoEnd.getStyle().set("font-size", "0.8em");
		infoEnd.getStyle().set("color", "gray");

		fieldRDays = new CheckboxGroup<>();
		fieldRDays.setLabel("Recurrence days of week");
		fieldRDays.setItems(DayOfWeek.values());
		fieldRDays.addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL);

		fieldAllDay.addValueChangeListener(event -> {
			fieldStart.setDateOnly(event.getValue());
			fieldEnd.setDateOnly(event.getValue());
		});

		fieldRecurring.addValueChangeListener(event -> updateRecurringFieldsState(event.getValue()));

		// init binder
		Binder<DialogEntry> binder = new Binder<>(DialogEntry.class);

		// required fields
		binder.forField(fieldTitle).asRequired().bind(DialogEntry::getTitle, DialogEntry::setTitle);
		binder.forField(fieldStart).asRequired().bind(DialogEntry::getStart, DialogEntry::setStart);
		binder.forField(fieldEnd).asRequired().bind(DialogEntry::getEnd, DialogEntry::setEnd);
		

		// optional fields
		binder.bind(done,DialogEntry::isDone, DialogEntry::setDone);
		binder.bind(fieldColor, DialogEntry::getColor, DialogEntry::setColor);
		binder.bind(fieldDescription, DialogEntry::getDescription, DialogEntry::setDescription);
		binder.bind(fieldAllDay, DialogEntry::isAllDay, DialogEntry::setAllDay);
		binder.bind(fieldRecurring, DialogEntry::isRecurring, DialogEntry::setRecurring);
		binder.bind(fieldRDays, DialogEntry::getRecurringDays, DialogEntry::setRecurringDays);

		binder.setBean(dialogEntry);

		// init buttons
		Button buttonSave = new Button("Save", e -> {
			if (binder.validate().isOk()) {
				if (newInstance) {
					calendar.addEntry(dialogEntry.updateEntry());
				} else {
					calendar.removeEntries(dialogEntry.getEntry());
					calendar.addEntry(dialogEntry.updateEntry());
					
					//alendar.updateEntry(dialogEntry.updateEntry());
				}
			}
			close();
		});
		buttonSave.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

		Button buttonCancel = new Button("Cancel", e -> close());
		buttonCancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

		HorizontalLayout buttons = new HorizontalLayout(buttonSave, buttonCancel);

		if (!newInstance) {
			Button buttonRemove = new Button("Remove", e -> {
				calendar.removeEntry(entry);
				close();
			});
			buttonRemove.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
			buttons.add(buttonRemove);
		}

		// TODO add resource assignment widget

		// layouting

		VerticalLayout mainLayout = new VerticalLayout(fieldTitle, fieldColor, fieldDescription,
				new HorizontalLayout(fieldAllDay, fieldRecurring), fieldStart, fieldEnd, infoEnd, fieldRDays,done);

		mainLayout.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.STRETCH);
		mainLayout.setSizeFull();

		mainLayout.getElement().getStyle().set("overflow-y", "auto");

		add(mainLayout, buttons);

		// additional layout init

		updateRecurringFieldsState(dialogEntry.isRecurring());
		fieldTitle.focus();
	}

	protected void updateRecurringFieldsState(boolean recurring) {
		if (recurring) {
			fieldStart.setLabel("Start of recurrence");
			fieldEnd.setLabel("End of recurrence");
		} else {
			fieldStart.setLabel("Start");
			fieldEnd.setLabel("End");
		}
		fieldRDays.setVisible(recurring);
	}

	/**
	 * see https://vaadin.com/components/vaadin-custom-field/java-examples
	 */
	public static class CustomDateTimePicker extends CustomField<LocalDateTime> {

		private final DatePicker datePicker = new DatePicker();
		private final TimePicker timePicker = new TimePicker();
		private boolean dateOnly;

		CustomDateTimePicker(String label) {
			setLabel(label);
			add(datePicker, timePicker);
		}

		@Override
		protected LocalDateTime generateModelValue() {
			final LocalDate date = datePicker.getValue();
			final LocalTime time = timePicker.getValue();

			if (date != null) {
				if (dateOnly || time == null) {
					return date.atStartOfDay();
				}

				return LocalDateTime.of(date, time);
			}

			return null;

		}

		@Override
		protected void setPresentationValue(LocalDateTime newPresentationValue) {
			datePicker.setValue(newPresentationValue != null ? newPresentationValue.toLocalDate() : null);
			timePicker.setValue(newPresentationValue != null ? newPresentationValue.toLocalTime() : null);
		}

		public void setDateOnly(boolean dateOnly) {
			this.dateOnly = dateOnly;
			timePicker.setVisible(!dateOnly);
		}
	}

	private static class DialogEntry {
		private String id;
		private String title;
		private String color;
		private String description;
		private LocalDateTime start;
		private LocalDateTime end;
		private boolean allDay;
		private boolean recurring;
		private boolean done;
		private Set<DayOfWeek> recurringDays;
		private Timezone timezone;
		private MyEntry entry;
		private String colorDone;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getColor() {
			return color;
		}

		public void setColor(String color) {
			this.color = color;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public LocalDateTime getStart() {
			return start;
		}

		public void setStart(LocalDateTime start) {
			this.start = start;
		}

		public LocalDateTime getEnd() {
			return end;
		}

		public void setEnd(LocalDateTime end) {
			this.end = end;
		}

		public boolean isAllDay() {
			return allDay;
		}

		public void setAllDay(boolean allDay) {
			this.allDay = allDay;
		}

		public boolean isRecurring() {
			return recurring;
		}

		public void setRecurring(boolean recurring) {
			this.recurring = recurring;
		}

		public Set<DayOfWeek> getRecurringDays() {
			return recurringDays;
		}

		public void setRecurringDays(Set<DayOfWeek> recurringDays) {
			this.recurringDays = recurringDays;
		}

		public boolean isDone() {
			return done;
		}

		public void setDone(boolean done) {
			this.done = done;
		}

		public Timezone getTimezone() {
			return timezone;
		}

		public void setTimezone(Timezone timezone) {
			this.timezone = timezone;
		}

		public MyEntry getEntry() {
			return entry;
		}

		public void setEntry(MyEntry entry) {
			this.entry = entry;
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

		public void setColorDone(String colorDone) {
			this.colorDone = colorDone;
		}

		public static DialogEntry of(MyEntry entry, Timezone timezone) {
			DialogEntry dialogEntry = new DialogEntry();

			dialogEntry.setTimezone(timezone);
			dialogEntry.setEntry(entry);

			dialogEntry.setTitle(entry.getTitle());
			dialogEntry.setColor(entry.getColor());
			dialogEntry.setDescription(entry.getDescription());
			dialogEntry.setAllDay(entry.isAllDay());
			dialogEntry.setDone(entry.isDone());
			dialogEntry.setColorDone(entry.getColorDone());

			boolean recurring = entry.isRecurring();
			dialogEntry.setRecurring(recurring);

			if (recurring) {
				dialogEntry.setRecurringDays(entry.getRecurringDaysOfWeeks());

				dialogEntry.setStart(entry.getRecurringStartDate(timezone).atTime(entry.getRecurringStartTime()));
				dialogEntry.setEnd(entry.getRecurringEndDate(timezone).atTime(entry.getRecurringEndTime()));
			} else {
				dialogEntry.setStart(entry.getStart(timezone));
				dialogEntry.setEnd(entry.getEnd(timezone));
			}

			return dialogEntry;
		}

		/**
		 * Updates the stored entry instance and returns it after updating.
		 *
		 * @return entry instnace
		 */
		private MyEntry updateEntry() {
			entry.setTitle(title);
			entry.setColor(color);
			entry.setDescription(description);
			entry.setAllDay(allDay);
			entry.setRecurring(recurring);
			entry.setDone(done);
			entry.setColorDone(colorDone);

			if (recurring) {
				entry.setRecurringDaysOfWeeks(getRecurringDays());

				entry.setStart((Instant) null);
				entry.setEnd((Instant) null);

				entry.setRecurringStartDate(start.toLocalDate(), timezone);
				entry.setRecurringStartTime(allDay ? null : start.toLocalTime());

				entry.setRecurringEndDate(end.toLocalDate(), timezone);
				entry.setRecurringEndTime(allDay ? null : end.toLocalTime());
			} else {
				entry.setStart(start, timezone);
				entry.setEnd(end, timezone);

				entry.setRecurringStartDate(null);
				entry.setRecurringStartTime(null);
				entry.setRecurringEndDate(null);
				entry.setRecurringEndTime(null);
				entry.setRecurringDaysOfWeeks(null);
			}

			return entry;
		}
	}
}