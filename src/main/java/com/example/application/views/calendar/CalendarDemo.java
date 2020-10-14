package com.example.application.views.calendar;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasText;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.NativeButton;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import org.vaadin.stefan.fullcalendar.*;

import com.example.application.views.calendar.CalendarDialog;
import com.example.application.views.main.MainView;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@PageTitle("Calendar")
@Route(value = "calendar", layout = MainView.class)
@CssImport("./styles/views/calendar/calendar-demo.css")
public class CalendarDemo extends Div {

	// componenti
	private static final String[] COLORS = { "tomato", "orange", "dodgerblue", "mediumseagreen", "gray", "slateblue", "violet" };
	private FullCalendar calendar;
	private ComboBox<CalendarView> comboBoxView;
	private Button buttonDatePicker;
	private HorizontalLayout toolbar;
	private ComboBox<Timezone> timezoneComboBox;


	
	public CalendarDemo() {
		setClassName("container");
		
		createToolbar();
		createCalendarInstance();
		
		add(toolbar,calendar);
		createTestEntries(calendar);
	}

	// creo la barra strumenti
	private Component createToolbar() {
		Button buttonToday = new Button("", VaadinIcon.HOME.create(), e -> calendar.today());
		Button buttonPrevious = new Button("", VaadinIcon.ANGLE_LEFT.create(), e -> calendar.previous());
		Button buttonNext = new Button("", VaadinIcon.ANGLE_RIGHT.create(), e -> calendar.next());
		
		// simulate the date picker light that we can use in polymer
		DatePicker gotoDate = new DatePicker();
		gotoDate.addValueChangeListener(event1 -> calendar.gotoDate(event1.getValue()));
		gotoDate.getElement().getStyle().set("visibility", "hidden");
		gotoDate.getElement().getStyle().set("position", "fixed");
		gotoDate.setWidth("0px");
		gotoDate.setHeight("0px");
		gotoDate.setWeekNumbersVisible(true);
		buttonDatePicker = new Button(VaadinIcon.CALENDAR.create());
		buttonDatePicker.getElement().appendChild(gotoDate.getElement());
		// quando clicco il bottone apro il mio datepicker
		buttonDatePicker.addClickListener(event -> gotoDate.open());

		// inserisco le varie viste in una lista -> con filtro sort per ogni nome
		List<CalendarView> calendarViews = new ArrayList<>(Arrays.asList(CalendarViewImpl.values()));
		calendarViews.addAll(Arrays.asList(SchedulerView.values()));
		calendarViews.sort(Comparator.comparing(CalendarView::getName));

		// combobox in base alle variabili contenute in calendarViews DEFAULT : MONTH
		comboBoxView = new ComboBox<>("", calendarViews);
		// setto di default il contenuto della combo a DAY_GRID_MONTH
		comboBoxView.setValue(CalendarViewImpl.DAY_GRID_MONTH);
//		comboBoxView.setWidth("25%");
		// quando seleziono una vista differente la modifico, se null torno a quella di
		// default
		comboBoxView.addValueChangeListener(e -> {
			CalendarView value = e.getValue();
			calendar.changeView(value == null ? CalendarViewImpl.DAY_GRID_MONTH : value);
		});

		// gestione della lingua
		List<Locale> items = Arrays.asList(CalendarLocale.getAvailableLocales());
		ComboBox<Locale> comboBoxLocales = new ComboBox<>();
//		comboBoxLocales.setWidth("10%");
		comboBoxLocales.setItems(items);
		comboBoxLocales.setValue(CalendarLocale.getDefault());
		comboBoxLocales.addValueChangeListener(event -> calendar.setLocale(event.getValue()));
		comboBoxLocales.setRequired(true);
		comboBoxLocales.setPreventInvalidInput(true);

		timezoneComboBox = new ComboBox<>("");
		timezoneComboBox.setItemLabelGenerator(Timezone::getClientSideValue);
		timezoneComboBox.setItems(Timezone.getAvailableZones());
		timezoneComboBox.setValue(Timezone.UTC);
		timezoneComboBox.addValueChangeListener(event -> {
			Timezone value = event.getValue();
			calendar.setTimezone(value != null ? value : Timezone.UTC);
		});

		// delete all entries
		Button removeAllEntries = new Button("E", VaadinIcon.TRASH.create(), event -> calendar.removeAllEntries());

		// delete all resources
		Button removeAllResources = new Button("R", VaadinIcon.TRASH.create(),
				event -> ((FullCalendarScheduler) calendar).removeAllResources());

//		buttonDatePicker.setWidth("25%");

		toolbar = new HorizontalLayout();

		toolbar.setClassName("toolbar");
		

		// addThousand
		toolbar.add(buttonToday, buttonPrevious, buttonNext, buttonDatePicker, 
							   comboBoxView, comboBoxLocales, removeAllEntries, removeAllResources);
		toolbar.expand(buttonPrevious);
		toolbar.expand(buttonNext);
		Optional.ofNullable(timezoneComboBox).ifPresent(toolbar::add);
		
		return toolbar;
	}

	private void createCalendarInstance() {
		calendar = FullCalendarBuilder.create().withAutoBrowserTimezone().withEntryLimit(3).withScheduler().build();

		calendar.setClassName("calendar");
		
		((FullCalendarScheduler) calendar).setSchedulerLicenseKey("GPL-My-Project-Is-Open-Source");

		calendar.setFirstDay(DayOfWeek.MONDAY);
		calendar.setNowIndicatorShown(true);
		calendar.setNumberClickable(true);
		calendar.setTimeslotsSelectable(true);

		calendar.setBusinessHours(
				new BusinessHours(LocalTime.of(9, 0), LocalTime.of(17, 0), BusinessHours.DEFAULT_BUSINESS_WEEK),
				new BusinessHours(LocalTime.of(12, 0), LocalTime.of(15, 0), DayOfWeek.SATURDAY),
				new BusinessHours(LocalTime.of(12, 0), LocalTime.of(13, 0), DayOfWeek.SUNDAY));

		calendar.addDatesRenderedListener(event -> {
			updateIntervalLabel(buttonDatePicker, comboBoxView.getValue(), event.getIntervalStart());
			System.out.println("dates rendered: " + event.getStart() + " " + event.getEnd());
		});

		calendar.addViewSkeletonRenderedListener(event -> {
			System.out.println("View skeleton rendered: " + event);
		});

		calendar.addWeekNumberClickedListener(event -> System.out.println("week number clicked: " + event.getDate()));
		calendar.addTimeslotClickedListener(
				event -> System.out.println("timeslot clicked: " + event.getDateTime() + " " + event.isAllDay()));
		calendar.addDayNumberClickedListener(event -> System.out.println("day number clicked: " + event.getDate()));
		calendar.addTimeslotsSelectedListener(event -> System.out.println("timeslots selected: "
				+ event.getStartDateTime() + " -> " + event.getEndDateTime() + " " + event.isAllDay()));

		calendar.addEntryDroppedListener(event -> System.out.println(event.applyChangesOnEntry()));
		((FullCalendarScheduler) calendar).addEntryDroppedSchedulerListener(event -> {
			System.out.println("Old resource: " + event.getOldResource());
			System.out.println("New resource: " + event.getNewResource());

			System.out.println(event.applyChangesOnEntry());
		});
		calendar.addEntryResizedListener(event -> System.out.println(event.applyChangesOnEntry()));

		//apro la form riguardante la entry selezionata
		calendar.addEntryClickedListener(
				event -> new CalendarDialog(calendar, (MyEntry) event.getEntry(), false).open());

		//creo una nuova entry in una qualunque cella
		((FullCalendarScheduler) calendar).addTimeslotsSelectedSchedulerListener((event) -> {
			MyEntry entry = new MyEntry();

			entry.setStart(event.getStartDateTimeUTC());
			entry.setEnd(event.getEndDateTimeUTC());
			entry.setAllDay(event.isAllDay());
			entry.setColor("dodgerblue");
			//instanzio un dialog = form connesso alla cella selezionata
			new CalendarDialog(calendar, entry, true).open();
		});

		//aggiungo più entità rispetto al limite = 3
		calendar.addLimitedEntriesClickedListener(event -> {
			Collection <Entry> entries = calendar.getEntries(event.getClickedDate());
			if (!entries.isEmpty()) {
				Dialog dialog = new Dialog();
				VerticalLayout dialogLayout = new VerticalLayout();
				dialogLayout.setSpacing(false);
				dialogLayout.setPadding(false);
				dialogLayout.setMargin(false);
				dialogLayout.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.STRETCH);

								
				//mostro all'utente il numero di entries in tal giorno/giorni
				dialogLayout.add(new Span("Entries of " + event.getClickedDate()));
				
				entries.stream().sorted(Comparator.comparing(Entry::getTitle)).map(entry -> {
					//una volta cliccato il bottone mostro il dettaglio della entry con la sua form solita
					Button button = new Button(entry.getTitle(),
							//apro il dialogo con una instanza già creata, quindi i campi sono pre compilati
							clickEvent -> new CalendarDialog(calendar, (MyEntry) entry, false).open());
					Style style = button.getStyle();
					 
//					styleIcon.set("color", "black" );
					style.set("background-color", "lightgrey");
					style.set("color", Optional.ofNullable(entry.getColor()).orElse("rgb(58, 135, 173)"));
					style.set("border-radius", "3px");
					style.set("text-align", "left");
					style.set("margin", "1px");
					return button;
				}).forEach(dialogLayout::add);

				dialog.add(dialogLayout);
				dialog.open();
			}
		});

		calendar.addBrowserTimezoneObtainedListener(event -> {
			System.out.println("Use browser's timezone: " + event.getTimezone().toString());
			timezoneComboBox.setValue(event.getTimezone());
		});

	}

	private static void createTestEntries(FullCalendar calendar) {
		LocalDate now = LocalDate.now();

		Resource meetingRoomRed = createResource((Scheduler) calendar, "Meetingroom Red", "#ff0000");
		Resource meetingRoomGreen = createResource((Scheduler) calendar, "Meetingroom Green", "green");
		Resource meetingRoomBlue = createResource((Scheduler) calendar, "Meetingroom Blue", "blue");
		Resource computer1A = createResource((Scheduler) calendar, "Computer 1A", "lightbrown");
		Resource computer1B = createResource((Scheduler) calendar, "Computer 1B", "lightbrown");
		Resource computer1C = createResource((Scheduler) calendar, "Computer 1C", "lightbrown");

		createResource((Scheduler) calendar, "Computer room 1", "brown",
				Arrays.asList(computer1A, computer1B, computer1C));

		Resource computerRoom2 = createResource((Scheduler) calendar, "Computer room 2", "brown");
		// here we must NOT use createResource, since they are added to the calendar
		// later
		Resource computer2A = new Resource(null, "Computer 2A", "lightbrown");
		Resource computer2B = new Resource(null, "Computer 2B", "lightbrown");
		Resource computer2C = new Resource(null, "Computer 2C", "lightbrown");

		// not realistic, just a demonstration of automatic recursive adding
		computer2A.addChild(new Resource(null, "Mouse", "orange"));
		computer2A.addChild(new Resource(null, "Screen", "orange"));
		computer2A.addChild(new Resource(null, "Keyboard", "orange"));

		List<Resource> computerRoom2Children = Arrays.asList(computer2A, computer2B, computer2C);
		computerRoom2.addChildren(computerRoom2Children);
		((Scheduler) calendar).addResources(computerRoom2Children);

		createTimedEntry(calendar, "Kickoff meeting with customer #1", now.withDayOfMonth(3).atTime(10, 0), 120, null, false,
				meetingRoomBlue, meetingRoomGreen, meetingRoomRed);
		createTimedBackgroundEntry(calendar, now.withDayOfMonth(3).atTime(10, 0), 120, null, false,
				meetingRoomBlue, meetingRoomGreen, meetingRoomRed);
		createTimedEntry(calendar, "Kickoff meeting with customer #2", now.withDayOfMonth(7).atTime(11, 30), 120,
				"mediumseagreen",false, meetingRoomRed);
		createTimedEntry(calendar, "Kickoff meeting with customer #3", now.withDayOfMonth(12).atTime(9, 0), 120,
				"mediumseagreen",false, meetingRoomGreen);
		createTimedEntry(calendar, "Kickoff meeting with customer #4", now.withDayOfMonth(13).atTime(10, 0), 120,
				"mediumseagreen",false, meetingRoomGreen);
		createTimedEntry(calendar, "Kickoff meeting with customer #5", now.withDayOfMonth(17).atTime(11, 30), 120,
				"mediumseagreen",false, meetingRoomBlue);
		createTimedEntry(calendar, "Kickoff meeting with customer #6", now.withDayOfMonth(22).atTime(9, 0), 120,
				"mediumseagreen",false, meetingRoomRed);

		createTimedEntry(calendar, "Kickoff meeting with customer #1", now.withDayOfMonth(3).atTime(10, 0), 120, null,false);
		createTimedBackgroundEntry(calendar, now.withDayOfMonth(3).atTime(10, 0), 120, null,false);
		createTimedEntry(calendar, "Kickoff meeting with customer #2", now.withDayOfMonth(7).atTime(11, 30), 120,
				"mediumseagreen",false);
		createTimedEntry(calendar, "Kickoff meeting with customer #3", now.withDayOfMonth(12).atTime(9, 0), 120,
				"mediumseagreen",false);
		createTimedEntry(calendar, "Kickoff meeting with customer #4", now.withDayOfMonth(13).atTime(10, 0), 120,
				"mediumseagreen",false);
		createTimedEntry(calendar, "Kickoff meeting with customer #5", now.withDayOfMonth(17).atTime(11, 30), 120,
				"mediumseagreen",false);
		createTimedEntry(calendar, "Kickoff meeting with customer #6", now.withDayOfMonth(22).atTime(9, 0), 120,
				"mediumseagreen",false);

		createTimedEntry(calendar, "Grocery Store", now.withDayOfMonth(7).atTime(17, 30), 45, "violet",false);
		createTimedEntry(calendar, "Dentist", now.withDayOfMonth(20).atTime(11, 30), 60, "violet",false);
		createTimedEntry(calendar, "Cinema", now.withDayOfMonth(10).atTime(20, 30), 140, "dodgerblue",false);
		createDayEntry(calendar, "Short trip", now.withDayOfMonth(17), 2, "dodgerblue" ,false);
		createDayEntry(calendar, "John's Birthday", now.withDayOfMonth(23), 1, "gray"  ,true);
		createDayEntry(calendar, "This special holiday", now.withDayOfMonth(4), 1, "gray" ,false);

		createDayEntry(calendar, "Multi 1", now.withDayOfMonth(12), 2, "tomato",true);
		createDayEntry(calendar, "Multi 2", now.withDayOfMonth(12), 2, "tomato",true);
		createDayEntry(calendar, "Multi 3", now.withDayOfMonth(12), 2, "tomato",true);
		createDayEntry(calendar, "Multi 4", now.withDayOfMonth(12), 2, "tomato",true);
		createDayEntry(calendar, "Multi 5", now.withDayOfMonth(12), 2, "tomato",true);
		createDayEntry(calendar, "Multi 6", now.withDayOfMonth(12), 2, "tomato",true);
		createDayEntry(calendar, "Multi 7", now.withDayOfMonth(12), 2, "tomato",true);
		createDayEntry(calendar, "Multi 8", now.withDayOfMonth(12), 2, "tomato",true);
		createDayEntry(calendar, "Multi 9", now.withDayOfMonth(12), 2, "tomato",true);
		createDayEntry(calendar, "Multi 10", now.withDayOfMonth(12), 2, "tomato",true);

//		createDayBackgroundEntry(calendar, now.withDayOfMonth(4), 6, "#B9FFC3");
//		createDayBackgroundEntry(calendar, now.withDayOfMonth(19), 2, "#CEE3FF");
//		createTimedBackgroundEntry(calendar, now.withDayOfMonth(20).atTime(11, 0), 150, "#FBC8FF");

		createRecurringEvents(calendar);
	}

	static void createRecurringEvents(FullCalendar calendar) {
		LocalDate now = LocalDate.now();

		MyEntry recurring = new MyEntry();
		recurring.setRecurring(true);
		recurring.setTitle(now.getYear() + "'s sunday event");
		recurring.setColor("lightgray");
		recurring.setRecurringDaysOfWeeks(Collections.singleton(DayOfWeek.SUNDAY));

		recurring.setRecurringStartDate(now.with(TemporalAdjusters.firstDayOfYear()), calendar.getTimezone());
		recurring.setRecurringEndDate(now.with(TemporalAdjusters.lastDayOfYear()), calendar.getTimezone());
		recurring.setRecurringStartTime(LocalTime.of(14, 0));
		recurring.setRecurringEndTime(LocalTime.of(17, 0));
		recurring.setEditable(true);

		calendar.addEntry(recurring);
	}

	static void createDayEntry(FullCalendar calendar, String title, LocalDate start, int days, String color,boolean done) {
		MyEntry entry = new MyEntry();
		setValues(calendar, entry, title, start.atStartOfDay(), days, ChronoUnit.DAYS, color, done);
		
		entry.setEditable(true);
		calendar.addEntry(entry);
	}

	static void createTimedEntry(FullCalendar calendar, String title, LocalDateTime start, int minutes, String color,boolean done) {
		MyEntry entry = new MyEntry();
		setValues(calendar, entry, title, start, minutes, ChronoUnit.MINUTES, color, done);

		entry.setEditable(true);
		calendar.addEntry(entry);
	}

	static void createDayBackgroundEntry(FullCalendar calendar, LocalDate start, int days, String color, boolean done) {
		MyEntry entry = new MyEntry();
		
		setValues(calendar, entry, "BG", start.atStartOfDay(), days, ChronoUnit.DAYS, color,done);
//		System.out.println("name: "+entry.getTitle()+" done: "+entry.isDone());
		
//		 The given string will be interpreted as js function on client side
//		 and attached as eventRender callback. 
//		 Make sure, that it does not contain any harmful code.
		
		String doneColor="";
		if(done) {
			doneColor="green";
			System.out.println("green selected");
		}else {
			doneColor="red";
			System.out.println("red selected");
		}
		
		calendar.setEntryRenderCallback("" +
			"function(info) {" +
		        "   console.log(info.event.title + 'X');" +
		        " 	var newDiv = document.createElement('div'); " +
		        " 	info.el.firstChild.insertAdjacentHTML('afterbegin', '<Span  style="

		        + "background-color:white;color:red;"
//		        + "display:inline-block;"
		        + "display:inline-flex;align-items:center;"
		        + "margin:3px;"
//		        + "padding:3px;"
				+ "border-radius:3px;width:16px;height:16px;"
		        + ">"
		        + "<span style="
		        + "background-color:"+doneColor+";"
		        + "border-radius:50%;width:10px;height:10px;"
		        + "display:inline;"
//				+ "margin-left:3px;margin-right:3px;"
				+ "margin:3px;"
				+ ">"
		        + "</span></span>'); " +
		        "   return info.el; " +
		        "}"
		);
		entry.setEditable(true);

		calendar.addEntry(entry);
	}

	static void createTimedBackgroundEntry(FullCalendar calendar, LocalDateTime start, int minutes, String color, boolean done) {
		MyEntry entry = new MyEntry();
		
		setValues(calendar, entry, "BG", start, minutes, ChronoUnit.MINUTES, color,done);
//		System.out.println("name: "+entry.getTitle()+" done: "+entry.isDone());
//		entry.setRenderingMode(Entry.RenderingMode.BACKGROUND);
		
//		 The given string will be interpreted as js function on client side
//		 and attached as eventRender callback. 
//		 Make sure, that it does not contain any harmful code.
		
		String doneColor="";
		if(done) {
			doneColor="green";
		}else {
			doneColor="orange";
		}
		
		calendar.setEntryRenderCallback("" +
				"function(info) {" +
		        "   console.log(info.event.title + 'X');" +
		        " 	var newDiv = document.createElement('div'); " +
		        " 	info.el.firstChild.insertAdjacentHTML('afterbegin', '<Span  style="

		        + "background-color:white;color:red;"
//		        + "display:inline-block;"
		        + "display:inline-flex;align-items:center;"
		        + "border-radius:3px;width:16px;height:16px;"
		        + "margin:3px;"
//		        + "padding:3px;"
		        + ">"
		        + "<span style="
		        + "background-color:"+doneColor+";"
		        + "border-radius:50%;width:10px;height:10px;"
		        + "display:inline;"
//				+ "margin-left:3px;margin-right:3px;"
		        + "margin:3px;"
				+ ">"
//		        + "display:inline-block;>"
		        + "</span></span>'); " +
		        "   return info.el; " +
		        "}"
		);
		entry.setEditable(true);

		calendar.addEntry(entry);
	}

	static void setValues(FullCalendar calendar, MyEntry entry, String title, LocalDateTime start, int amountToAdd,
			ChronoUnit unit, String color, boolean done) {
		entry.setTitle(title);
		entry.setStart(start, calendar.getTimezone());
		entry.setEnd(entry.getStartUTC().plus(amountToAdd, unit));
		entry.setAllDay(unit == ChronoUnit.DAYS);
		entry.setColor(color);
		entry.setDone(done);
		System.out.println("name: "+entry.getTitle()+" done: "+entry.isDone());
	}

//	static void setValues(FullCalendar calendar, MyEntry entry, String title, LocalDateTime start,
//			int amountToAdd, ChronoUnit unit, String color, boolean done) {
//		entry.setTitle(title);
//		entry.setStart(start, calendar.getTimezone());
//		entry.setEnd(entry.getStartUTC().plus(amountToAdd, unit));
//		entry.setAllDay(unit == ChronoUnit.DAYS);
//		entry.setColor(color);
//	}

	static Resource createResource(Scheduler calendar, String s, String color) {
		Resource resource = new Resource(null, s, color);
		calendar.addResource(resource);
		return resource;
	}

	static Resource createResource(Scheduler calendar, String s, String color, Collection<Resource> children) {
		Resource resource = new Resource(null, s, color, children);
		calendar.addResource(resource);
		return resource;
	}

	static void createTimedEntry(FullCalendar calendar, String title, LocalDateTime start, int minutes, String color, boolean done,
			Resource... resources) {
		MyEntry entry = new MyEntry();
		setValues(calendar, entry, title, start, minutes, ChronoUnit.MINUTES, color,done);
		if (resources != null && resources.length > 0) {
			entry.assignResources(Arrays.asList(resources));
		}
		entry.setEditable(true);
		calendar.addEntry(entry);
	}

	static void createTimedBackgroundEntry(FullCalendar calendar, LocalDateTime start, int minutes, String color, boolean done,
			Resource... resources) {
		MyEntry entry = new MyEntry();
		setValues(calendar, entry, "BG", start, minutes, ChronoUnit.MINUTES, color,done);
//		System.out.println("name: "+entry.getTitle()+" done: "+entry.isDone());
		
		String doneColor="";
		if(done) {
			doneColor="orange";
		}else {
			doneColor="red";
		}
		
//		entry.setRenderingMode(Entry.RenderingMode.BACKGROUND);
		calendar.setEntryRenderCallback("" +
				"function(info) {" +
		        "   console.log(info.event.title + 'X');" +
		        " 	var newDiv = document.createElement('div'); " +
		        " 	info.el.firstChild.insertAdjacentHTML('afterbegin', '<Span  style="

		        + "background-color:white;color:red;"
//		        + "display:inline-block;"
		        + "display:inline-flex;align-items:center;"
		        + "border-radius:3px;width:16px;height:16px;"
		        + "margin:3px;"
//		        + "padding:3px;"
		        + ">"
		        + "<span style="
		        + "background-color:"+doneColor+";"
		        + "border-radius:50%;width:10px;height:10px;"
//		        + "display:inline-block>"
				+ "display:inline;"
		        + "margin:3px;"
//				+ "margin-left:3px;margin-right:3px;"
				+ ">"
		        + "</span></span>'); " +
		        "   return info.el; " +
		        "}"
			);
		if (resources != null && resources.length > 0) {
			entry.assignResources(Arrays.asList(resources));
		}
		entry.setEditable(true);

		calendar.addEntry(entry);
	}

	void updateIntervalLabel(HasText intervalLabel, CalendarView view, LocalDate intervalStart) {
		String text = "--";
		Locale locale = calendar.getLocale();

		if (view == null) {
			text = intervalStart.format(DateTimeFormatter.ofPattern("MMMM yyyy").withLocale(locale));
		} else if (view instanceof CalendarViewImpl) {
			switch ((CalendarViewImpl) view) {
			default:
			case DAY_GRID_MONTH:
			case LIST_MONTH:
				text = intervalStart.format(DateTimeFormatter.ofPattern("MMMM yyyy").withLocale(locale));
				break;
			case TIME_GRID_DAY:
			case DAY_GRID_DAY:
			case LIST_DAY:
				text = intervalStart.format(DateTimeFormatter.ofPattern("dd.MM.yyyy").withLocale(locale));
				break;
			case TIME_GRID_WEEK:
			case DAY_GRID_WEEK:
			case LIST_WEEK:
				text = intervalStart.format(DateTimeFormatter.ofPattern("dd.MM.yy").withLocale(locale)) + " - "
						+ intervalStart.plusDays(6).format(DateTimeFormatter.ofPattern("dd.MM.yy").withLocale(locale))
						+ " (cw " + intervalStart.format(DateTimeFormatter.ofPattern("ww").withLocale(locale)) + ")";
				break;
			case LIST_YEAR:
				text = intervalStart.format(DateTimeFormatter.ofPattern("yyyy").withLocale(locale));
				break;
			}
		} else if (view instanceof SchedulerView) {
			switch ((SchedulerView) view) {
			case TIMELINE_DAY:
			case RESOURCE_TIMELINE_DAY:
			case RESOURCE_TIME_GRID_DAY:
				text = intervalStart.format(DateTimeFormatter.ofPattern("dd.MM.yyyy").withLocale(locale));
				break;
			case TIMELINE_WEEK:
			case RESOURCE_TIMELINE_WEEK:
			case RESOURCE_TIME_GRID_WEEK:
				text = intervalStart.format(DateTimeFormatter.ofPattern("dd.MM.yy").withLocale(locale)) + " - "
						+ intervalStart.plusDays(6).format(DateTimeFormatter.ofPattern("dd.MM.yy").withLocale(locale))
						+ " (cw " + intervalStart.format(DateTimeFormatter.ofPattern("ww").withLocale(locale)) + ")";
				break;
			case TIMELINE_MONTH:
			case RESOURCE_TIMELINE_MONTH:
				text = intervalStart.format(DateTimeFormatter.ofPattern("MMMM yyyy").withLocale(locale));
				break;
			case TIMELINE_YEAR:
			case RESOURCE_TIMELINE_YEAR:
				text = intervalStart.format(DateTimeFormatter.ofPattern("yyyy").withLocale(locale));
				break;
			}
		}

		intervalLabel.setText(text);
	}

}