package com.vdda.command.service;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static com.vdda.command.service.CallbackBuilder.SEPARATOR;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class CallbackBuilderTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private String CALLBACK_ID = "callbackId";
	private String USER_ID = "userId";
	private String OUTCOME = "outcome";


	@Test
	public void contstructorException() throws Exception {

		thrown.expect(InvocationTargetException.class);

		Constructor<CallbackBuilder> c = CallbackBuilder.class.getDeclaredConstructor();
		c.setAccessible(true);
		c.newInstance();
	}

	@Test
	public void build2() throws Exception {
		assertThat(CallbackBuilder.callbackIdBuilder(CALLBACK_ID, USER_ID), is(CALLBACK_ID + SEPARATOR + USER_ID));
	}

	@Test
	public void build3() throws Exception {
		assertThat(CallbackBuilder.callbackIdBuilder(CALLBACK_ID, USER_ID, OUTCOME), is(CALLBACK_ID + SEPARATOR + USER_ID + SEPARATOR + OUTCOME));
	}
}