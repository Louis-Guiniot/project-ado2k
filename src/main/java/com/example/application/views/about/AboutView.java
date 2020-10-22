package com.example.application.views.about;

import java.awt.Container;
import java.awt.Window;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.application.data.entity.GenderEnum;
import com.example.application.data.entity.Person;
import com.example.application.data.service.PersonService;
//import com.example.application.data.service.PersonService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;

import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.RequiredFieldConfigurator;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinRequest;
import com.example.application.views.main.MainView;

@PageTitle("About")
@Route(value = "about", layout = MainView.class)
@CssImport("./styles/views/about/about-view.css")
public class AboutView extends Div {
	
	private TextField firstName = new TextField("First name");
	private TextField lastName = new TextField("Last name");
	private TextField email = new TextField("Email address");

	private DatePicker dateOfBirth = new DatePicker("Birthday");
	private TextField phone = new TextField("Phone number");
	
	private ComboBox<String> gender = new ComboBox<>("Gender");
	private TextField occupation = new TextField("Occupation");
	private TextField mobile = new TextField("Mobile phone number");

	private TextField city = new TextField("City");
	private ComboBox<String> province = new ComboBox<>("Province");
	private TextField address = new TextField("Address");
	private TextField cap = new TextField("Cap");
	private TextField road = new TextField("Road");

	private Button cancel = new Button(new Icon (VaadinIcon.TRASH));
	private Button save = new Button(new Icon (VaadinIcon.CHECK));
	
	private PersonService service = new PersonService();
	private Person person = new Person();
	private Binder<Person> binder = new Binder<>(Person.class);
	
	public AboutView() {
		setId("about-view");

		add(createFormLayout());	

		binder.bindInstanceFields(this);
		clearForm();

		// when click clear the form fields
		cancel.addClickListener(e -> clearForm());

		// when click save data and clear form
		save.addClickListener(e -> {
			service.store(binder.getBean());
			Notification.show("Person details stored.");
			clearForm();
		});
	}

	// method for clear form
	private void clearForm() {
		binder.setBean(new Person());
	}

	// method for create the whole layout
	private Component createFormLayout() {
		
		// required fields
		firstName.setRequired(true);
		lastName.setRequired(true);
		email.setRequired(true);
		phone.setRequired(true);
		dateOfBirth.setRequired(true);
		occupation.setRequired(true);
		gender.setRequired(true);
		mobile.setRequired(true);
		address.setRequired(true);
		road.setRequired(true);
		city.setRequired(true);
		cap.setRequired(true);
		province.setRequired(true);
		
		// container layout
		VerticalLayout box = new VerticalLayout();

		// principal layout
		FormLayout formLayout = new FormLayout();

		// for insert label on top of each layout
		VerticalLayout vertPers = new VerticalLayout();
		VerticalLayout vertOther = new VerticalLayout();
		VerticalLayout vertAddress = new VerticalLayout();

		// different form layout categorized
		FormLayout personalLayout = new FormLayout();
		FormLayout otherLayout = new FormLayout();
		FormLayout addressLayout = new FormLayout();

		// layout for buttons
		HorizontalLayout buttonLayout = new HorizontalLayout();
		HorizontalLayout labelHeader = new HorizontalLayout();
		
		Icon vLogo = new Icon(VaadinIcon.VAADIN_H);
		Icon compressIcon = new Icon(VaadinIcon.COMPRESS_SQUARE);
				
		// define id e class HTML

		// vertical layout
		box.setClassName("box");
		vertPers.setClassName("vert");
		vertOther.setClassName("vert");
		vertAddress.setClassName("vert");
		vertAddress.setId("vert-address");

		// form layout
		formLayout.setId("form");
		personalLayout.setId("personal");
		otherLayout.setId("other");
		addressLayout.setId("address");

		buttonLayout.addClassName("button-layout");
		save.setId("save-btn");
		cancel.setId("cancel-btn");
		
		vLogo.setId("vLogo");
		compressIcon.setId("compressLogo");
		vLogo.setClassName("icon");
		compressIcon.setClassName("icon");

		// setting responsive adapter
		formLayout.setResponsiveSteps(new ResponsiveStep("45em", 1), new ResponsiveStep("60em", 3));

		personalLayout.setResponsiveSteps(new ResponsiveStep("15em", 1), new ResponsiveStep("32em", 2));

		addressLayout.setResponsiveSteps(new ResponsiveStep("15em", 1), new ResponsiveStep("32em", 2),
				new ResponsiveStep("40em", 3));
		
	
		// insert value into combo box
		gender.setPreventInvalidInput(true);
		gender.setItems("M","F","ND");
		gender.addCustomValueSetListener(e -> gender.setValue(e.getDetail()));

		// BUILDING FASE

		// build layout header, add label
		Label header = new Label("Informations form");
		labelHeader.add(vLogo,header,compressIcon);
		labelHeader.setClassName("header-label");
		labelHeader.setSizeFull();
		labelHeader.setSpacing(false);

		// build layout persona, add field , span and label
		email.setHelperText("insert your email");
		personalLayout.add(firstName, lastName, email);
		personalLayout.setColspan(email, 2);
		vertPers.add(new Label("Personal info"), personalLayout);

		// build layout other, add field , span and label
		dateOfBirth.setHelperText("scroll to select year");
		otherLayout.add(phone, dateOfBirth);
		phone.setWidth("100%");
		dateOfBirth.setWidth("100%");
		vertOther.add(new Label("Other info"), otherLayout);

		// build layout address, add field , span and label
		province.setHelperText("specific the right abbreviation");
		province.setItems("AG", "AL", "AN", "AO", "AR", "AP", "AT", "AV", "BT", "BL", "BN", "BG", "BI", "BZ", "BS",
				"BR", "CL", "CB", "CE", "CZ", "CH", "CO", "CS", "CR", "KR", "CN", "EN", "FM", "FE", "FG", "FC", "FR",
				"GO", "GR", "IM", "IS", "SP", "AQ", "LT", "LE", "LC", "LI", "LO", "LU", "MC", "MN", "MS", "MT", "ME",
				"MB", "MO", "NO", "NU", "OR", "PD", "PR", "PV", "PG", "PU", "PE", "PC", "PI", "PT", "PN", "PZ", "PO",
				"RG", "RA", "RE", "RI", "RN", "RO", "SA", "SS", "SV", "SI", "SR", "SO", "TA", "TE", "TR", "TP", "TN",
				"TV", "TS", "UD", "VA", "VB", "VC", "VR", "VV", "VI", "VT");

		addressLayout.add(address, road, city, cap, province);
		addressLayout.setColspan(address, 2);
		vertAddress.add(new Label("Shipment info"), addressLayout);

		// build layout button, add field
		buttonLayout.setSizeFull();
		save.setWidth("10%");
		cancel.setWidth("10%");
		buttonLayout.setWidth("100%");
		buttonLayout.add(save, cancel);

		// add each layout to the form container
		gender.setHelperText("select your gender");
		formLayout.add(vertPers, vertOther, occupation, gender, mobile, vertAddress);
		formLayout.setColspan(vertPers, 2);
		formLayout.setColspan(vertAddress, 3);

		// add the form container to a vertical layout
		box.add(labelHeader, formLayout, buttonLayout);
		box.setWidth("90%");
		
		binder.bind(firstName,Person::getFirstName, Person::setFirstName);
		binder.bind(lastName, Person::getLastName, Person::setLastName);
		binder.bind(email, Person::getEmail, Person::setEmail);
		binder.bind(phone, Person::getPhone, Person::setPhone);
		binder.bind(dateOfBirth, Person::getDateOfBirth, Person::setDateOfBirth);
		binder.bind(occupation, Person::getOccupation, Person::setOccupation);
		binder.bind(gender,Person::getGender, Person::setGender);
		binder.bind(mobile, Person::getMobile, Person::setMobile);
		binder.bind(address, Person::getAddress, Person::setAddress);
		binder.bind(road, Person::getRoad, Person::setRoad);
		binder.bind(city, Person::getCity, Person::setCity);
		binder.bind(cap, Person::getCap, Person::setCap);
		binder.bind(province, Person::getProvince, Person::setProvince);
		
		binder.setBean(person);
		
		return box;
	}
}
