package de.ww.json.ref.serializer.test;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import de.ww.json.ref.serializer.ISerializer;
import de.ww.json.ref.serializer.SerializerFactory;
import de.ww.json.ref.serializer.SerializerImplementation;
import de.ww.json.ref.serializer.exceptions.SerializerException;
import de.ww.json.ref.serializer.test.data.TestCustomerEntity;

public class TestSerializer {

	@Test
	public void testArrayToListObject_ObjectArray() throws SerializerException {
		SerializerImplementation ser = SerializerFactory.getSerializerTestInstance();
		String[] lst = {"a", "b", "c", "d"};
		List<?> list = ser.arrayToListObject(lst);
		assertEquals(4, list.size());
	}
	
	@Test
	public void testArrayToListObject_PrimitveArray() throws SerializerException {
		SerializerImplementation ser = SerializerFactory.getSerializerTestInstance();
		int[] lst = {1,2,3,4};
		List<?> list = ser.arrayToListObject(lst);
		assertEquals(4, list.size());
	}
	
	@Test
	public void testSerializeCustomerEntity() throws Exception {
		ISerializer ser = SerializerFactory.getSerializerTestInstance();
		TestCustomerEntity data = getTestData();
		String str = ser.serialize(data);
		
		System.out.println("Ergebnis: "+str);
		assertEquals(true, str.length() > 0);
	}
	
	@Test
	public void testGetValue() throws Exception {
		SerializerImplementation ser = SerializerFactory.getSerializerTestInstance();
		TestCustomerEntity data = getTestData();
		
		Method m = data.getClass().getMethod("getName");		
		Object value = ser.getValue(data, m);		
		assertEquals("'Wolfgang'", value);
		
		m = data.getClass().getMethod("getId");		
		value = ser.getValue(data, m);		
		assertEquals("1", value);
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
		entity.setTestInteger(1234);
		entity.setChild(entity2);
		
		ArrayList<TestCustomerEntity> lst = new ArrayList<TestCustomerEntity>();
		
		lst.add(new TestCustomerEntity());
		lst.add(new TestCustomerEntity());
		lst.add(new TestCustomerEntity());
		
		lst.get(0).setBirthDate(new Date());
		lst.get(0).setId(1000);
		
		entity.setChildren(lst);
		
		
		return entity;
	}
	
}
