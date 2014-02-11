package de.ww.json.ref.serializer.test.data;

import java.util.ArrayList;
import java.util.Date;

import de.ww.json.ref.serializer.annotations.JustReference;
import de.ww.json.ref.serializer.annotations.RestPath;

/**
 * Überlegen, evtl. brauch ich oben den Rest-Path nicht wenn 
 * ich alternativ dazu unten unter @JustReference(lookupController=TestControllerDummy.class)
 * alles notwendige aus der Controller-Annotation des Controllers rauslesen kann.
 * 
 * Dadurch wäre dann ein vernünftiges Binding an die zukünftige Version der StatlessUI
 * (dann knockout.java) möglich.
 *
 * Auch im anderen Fall (ohne knockout.java) muss der Pfad in die Just-Reference-Annotation 
 * rein und nicht über dem Entity stehen, das ist einfach zu unflexibel.
 *
 */
@RestPath("http://localhost:8080/test/entity/customer/{id}")
public class TestCustomerEntity {

	private int id;
	private Integer testInteger;
	private String name, vorname;
	private TestCustomerEntity child;
	private Date birthDate;
	private ArrayList<TestCustomerEntity> children;
	
	public int getId() {
		return this.id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getVorname() {
		return vorname;
	}
	
	public void setVorname(String vorname) {
		this.vorname = vorname;
	}
	
	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public ArrayList<String> getBeispiele() {
		ArrayList<String> lst = new ArrayList<String>();
		lst.add("Hallo");
		lst.add("Welt");
		return lst;
	}
	
	@JustReference(lookupController=TestControllerDummy.class)
	public ArrayList<TestCustomerEntity> getChildren() {
		return children;
	}
	
	public ArrayList<TestCustomerEntity> getChildren2() {
		return children;
	}
	
	public void setChildren(ArrayList<TestCustomerEntity> children) {
		this.children = children;
	}
	
	@JustReference(lookupController=TestControllerDummy.class)
	public TestCustomerEntity getChild() {
		return child;
	}
	
	public void setChild(TestCustomerEntity child) {
		this.child = child;
	}

	public Integer getTestInteger() {
		return testInteger;
	}

	public void setTestInteger(Integer testInteger) {
		this.testInteger = testInteger;
	}	
}
