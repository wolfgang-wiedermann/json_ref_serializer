package de.ww.json.ref.serializer.test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Date;

import org.junit.Test;

import de.ww.json.ref.serializer.Serializer;
import de.ww.json.ref.serializer.test.data.TestCustomerEntity;

public class TestSerializer {

	@Test
	public void testSerializeCustomerEntity() throws Exception {
		TestCustomerEntity data = getTestData();
		String str = Serializer.serialize(data);
		
		System.out.println("Ergebnis: "+str);
		assertEquals(true, str.length() > 0);
	}
	
	
	private TestCustomerEntity getTestData() {
		TestCustomerEntity entity2 = new TestCustomerEntity();
		entity2.setId(2);
		entity2.setName("Mustermann");
		entity2.setVorname("Max");
		
		TestCustomerEntity entity = new TestCustomerEntity();		
		entity.setId(1);
		entity.setName("Wolfgang");
		entity.setVorname("Wiedermann");
		entity.setBirthDate(new Date());
		entity.setChild(entity2);
		
		ArrayList<TestCustomerEntity> lst = new ArrayList<TestCustomerEntity>();
		
		lst.add(new TestCustomerEntity());
		lst.add(new TestCustomerEntity());
		lst.add(new TestCustomerEntity());
		
		entity.setChildren(lst);
		
		
		return entity;
	}
	
}
