package com.vdda.tool;

import com.vdda.slack.SlackParameters;
import lombok.Getter;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;
import java.util.stream.Collectors;

public class Request {
	@Getter
	private Map<String, String> parametersMap;

	public Request(String parameters) {
		parametersMap = toMap(parameters);
	}

	private static Map<String, String> toMap(String parameters) {
		Map<String, String> list = new HashMap<>();

		if (parameters == null) {
			return list;
		}

		list = Arrays.stream(parameters.split("&"))
				.map(p -> p.split("="))
				.filter(a -> a.length == 2)
				.collect(Collectors.toMap(e -> decode(e[0]), e -> decode(e[1])));

		return list;
	}

	public String getParameter(String parameter) {
		return parametersMap.get(parameter);
	}

	private static String decode(final String encoded) {
		try {
			return encoded == null ? null : URLDecoder.decode(encoded, "UTF-8");
		} catch (final UnsupportedEncodingException e) {
			throw new RuntimeException("Impossible: UTF-8 is a required encoding", e);
		}
	}

	/**
	 * @param argumentOffset set the number of arguments to skip
	 * @return an Optional list of arguments
	 */
	public List<String> getArguments(int argumentOffset) {
		Optional<List<String>> optionalArguments = Optional.ofNullable(parametersMap.get(SlackParameters.TEXT.toString()))
				.map(s -> s.split(" "))
				.map(Arrays::asList)
				.map(a -> a.subList(argumentOffset, a.size()));

		return optionalArguments.orElse(new ArrayList<>());
	}

	public List<String> getArguments() {
		return getArguments(0);
	}
}
