package com.vdda.tool;

import mockit.Expectations;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URLDecoder;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by francois
 * on 2016-12-25
 * for vandiedakaf solutions
 */
public class ParametersTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private String parameters;

    @Before
    public void setUp() throws Exception {
        parameters = "param1=value1&param2=value2&param3=https%3A%2F%2Fhooks.slack.com";
    }

    @Test
    public void exist() throws Exception {
        Map<String, String> parameterMap = Parameters.parse(parameters);

        assertEquals("value1", parameterMap.get("param1"));
        assertEquals("value2", parameterMap.get("param2"));
        assertEquals("https://hooks.slack.com", parameterMap.get("param3"));
    }

    @Test
    public void nonExist() throws Exception {
        Map<String, String> parameterMap = Parameters.parse(parameters);

        assertNull(parameterMap.get("param0"));
    }

    @Test
    public void nullParameters() throws Exception {
        Map<String, String> parameterMap = Parameters.parse(null);

        assertTrue(parameterMap.size() == 0);
    }

    @Test
    public void emptyString() throws Exception {
        Map<String, String> parameterMap = Parameters.parse("");

        assertTrue(parameterMap.size() == 0);
    }

    @Test
    public void noPairs() throws Exception {
        Map<String, String> parameterMap = Parameters.parse("nada");

        assertTrue(parameterMap.size() == 0);
    }

    @Test
    public void brokenPairs() throws Exception {
        Map<String, String> parameterMap = Parameters.parse("nada1&nada2");

        assertTrue(parameterMap.size() == 0);
    }

    @Test
    public void contstructorException() throws Exception {

        thrown.expect(InvocationTargetException.class);

        Constructor<Parameters> c = Parameters.class.getDeclaredConstructor();
        c.setAccessible(true);
        Parameters u = c.newInstance();
    }

    @Test
    public void unsupportedEncodingException(@Mocked URLDecoder urlDecoder) throws Exception {

        thrown.expect(RuntimeException.class);

        new Expectations() {{
            urlDecoder.decode(anyString, anyString);
            result = new UnsupportedEncodingException("Testing an exception condition.");
        }};

        Map<String, String> parameterMap = Parameters.parse(parameters);
    }
}
