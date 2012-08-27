package com.owlike.genson.functional;

import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.sql.Timestamp;

import org.junit.Test;

import com.owlike.genson.Context;
import com.owlike.genson.Deserializer;
import com.owlike.genson.Factory;
import com.owlike.genson.Genson;
import com.owlike.genson.TransformationException;
import com.owlike.genson.annotation.JsonProperty;
import com.owlike.genson.reflect.BeanDescriptor;
import com.owlike.genson.stream.ObjectReader;

public class CombinedObjectTest {
	@Test
	public void combineMultipleJsonObjectIntoSingleObject() throws TransformationException,
			IOException {
		String json = "{\"Person\":{\"id\":\"2\"},\"Dog\":{\"dateOfBirth\":\"2012-08-20 00:00:00\",\"price\" : \"10.00\"}}";
		Genson genson = new Genson.Builder().withDeserializerFactory(new MyClassConverterFactory()).create();
		MyClass myClass = genson.deserialize(json, MyClass.class);
		System.out.println(myClass);
	}
	
	public static class MyClassConverterFactory implements Factory<Deserializer<MyClass>> {
		@SuppressWarnings("unchecked")
		@Override
		public Deserializer<MyClass> create(Type type, Genson genson) {
			BeanDescriptor<MyClass> myClassDescriptor = (BeanDescriptor<MyClass>) genson.getBeanDescriptorFactory().provide(MyClass.class, genson);
			return new MyClassConverter(myClassDescriptor);
		}
	}

	public static class MyClassConverter implements Deserializer<MyClass> {
		BeanDescriptor<MyClass> myClassDescriptor;
		public MyClassConverter(BeanDescriptor<MyClass> myClassDescriptor) {
			this.myClassDescriptor = myClassDescriptor;
		}

		@Override
		public MyClass deserialize(ObjectReader reader, Context ctx)
				throws TransformationException, IOException {
			reader.beginObject();
			MyClass myClass = new MyClass();
			for (; reader.hasNext();) {
				reader.next();
				if ("Person".equals(reader.name())) {
					myClassDescriptor.deserialize(myClass, reader, ctx);
				} else if ("Dog".equals(reader.name())) {
					myClassDescriptor.deserialize(myClass, reader, ctx);
				}
			}
			reader.endObject();
			return myClass;
		}
	}

	public static class MyClass {
		@JsonProperty("id") private String personsId;
		@JsonProperty("dateOfBirth") private Timestamp dogsDateOfBirth;
		@JsonProperty("price") private BigDecimal dogsPrice;
		public MyClass() {
		}
	}
}
