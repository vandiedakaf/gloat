package com.vdda.tool;

import mockit.Expectations;
import mockit.Mocked;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

import static org.junit.Assert.*;

public class RequestTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private String parameters;

    @Before
    public void setUp() throws Exception {
        parameters = "param1=value1&param2=value2&param3=https%3A%2F%2Fhooks.slack.com";
    }

    @Test
    public void exist() throws Exception {
		Request request = new Request(this.parameters);
		Map<String, String> parameterMap = request.getParametersMap();

        assertEquals("value1", parameterMap.get("param1"));
        assertEquals("value2", parameterMap.get("param2"));
        assertEquals("https://hooks.slack.com", parameterMap.get("param3"));
    }

    @Test
    public void nonExist() throws Exception {
		Request request = new Request(this.parameters);
		Map<String, String> parameterMap = request.getParametersMap();

        assertNull(parameterMap.get("param0"));
    }

    @Test
    public void nullParameters() throws Exception {
		Request request = new Request(null);
		Map<String, String> parameterMap = request.getParametersMap();

        assertTrue(parameterMap.size() == 0);
    }

    @Test
    public void emptyString() throws Exception {
		Request request = new Request("");
		Map<String, String> parameterMap = request.getParametersMap();

        assertTrue(parameterMap.size() == 0);
    }

    @Test
    public void noPairs() throws Exception {
		Request request = new Request("nada");
		Map<String, String> parameterMap = request.getParametersMap();

        assertTrue(parameterMap.size() == 0);
    }

    @Test
    public void brokenPairs() throws Exception {
		Request request = new Request("nada1&nada2");
		Map<String, String> parameterMap = request.getParametersMap();

        assertTrue(parameterMap.size() == 0);
    }

    @Test
    public void unsupportedEncodingException(@Mocked URLDecoder urlDecoder) throws Exception {

        thrown.expect(RuntimeException.class);

        new Expectations() {{
            urlDecoder.decode(anyString, anyString);
            result = new UnsupportedEncodingException("Testing an exception condition.");
        }};

		new Request(this.parameters);
    }
}
