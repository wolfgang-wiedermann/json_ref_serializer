package de.ww.json.ref.serializer.test.data;

import java.util.ArrayList;
import java.util.Date;

import de.ww.json.ref.serializer.annotations.JustReference;
import de.ww.json.ref.serializer.annotations.RestPath;

@RestPath("http://localhost:8080/test/entity/customer/{id}")
public class TestCustomerEntity {

	private int id;
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
	
	// Dieses JustReference funktioniert noch nicht!
	@JustReference
	public ArrayList<TestCustomerEntity> getChildren() {
		return children;
	}
	
	public void setChildren(ArrayList<TestCustomerEntity> children) {
		this.children = children;
	}
	
	@JustReference
	public TestCustomerEntity getChild() {
		return child;
	}
	
	public void setChild(TestCustomerEntity child) {
		this.child = child;
	}	
}
