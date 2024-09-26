package com.lostmc.core.permission;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.google.common.cache.CacheLoader;

public class RegexCacheLoader extends CacheLoader<String, Pattern> {

	public static final String RAW_REGEX_CHAR = "$";

	@Override
	public Pattern load(String regex) throws Exception {
		return createPattern(regex);
	}

	protected static Pattern createPattern(String expression) {
		try {
			return Pattern.compile(prepareRegexp(expression), Pattern.CASE_INSENSITIVE);
		} catch (PatternSyntaxException e) {
			return Pattern.compile(Pattern.quote(expression), Pattern.CASE_INSENSITIVE);
		}
	}

	public static String prepareRegexp(String expression) {
		if (expression.startsWith("-")) 
			expression = expression.substring(1);	
		if (expression.startsWith("#")) 
			expression = expression.substring(1);		
		boolean rawRegexp = expression.startsWith(RAW_REGEX_CHAR);
		if (rawRegexp) 
			expression = expression.substring(1);	
		String regexp = rawRegexp ? expression : expression.replace(".", "\\.").replace("*", "(.*)");
		return regexp;
	}
}
