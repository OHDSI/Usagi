/*******************************************************************************
 * Copyright 2017 Observational Health Data Sciences and Informatics
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.ohdsi.utilities;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class StringUtilities {

	public static String join(Collection<?> s, String delimiter) {
		StringBuffer buffer = new StringBuffer();
		Iterator<?> iter = s.iterator();
		if (iter.hasNext()) {
			buffer.append(iter.next().toString());
		}
		while (iter.hasNext()) {
			buffer.append(delimiter);
			buffer.append(iter.next().toString());
		}
		return buffer.toString();
	}

	public static String join(Object[] objects, String delimiter) {
		StringBuffer buffer = new StringBuffer();
		if (objects.length != 0)
			buffer.append(objects[0].toString());
		for (int i = 1; i < objects.length; i++) {
			buffer.append(delimiter);
			buffer.append(objects[i].toString());
		}
		return buffer.toString();
	}

	public static boolean isInteger(String string) {
		try {
			Integer.parseInt(string);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	public static boolean isLong(String string) {
		try {
			Long.parseLong(string);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	public static boolean isNumber(String string) {
		try {
			Double.parseDouble(string);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public static String escape(String string) {
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < string.length(); i++) {
			char currentChar = string.charAt(i);
			if (currentChar == '"' || currentChar == '?' || currentChar == ';' || currentChar == '\\' || currentChar == '|') {
				result.append('\\');
			}
			result.append(currentChar);
		}
		return result.toString();
	}

	public static String unescape(String string) {
		StringBuffer result = new StringBuffer();
		if (string.length() > 0) {
			if (string.charAt(0) == '"' && string.charAt(string.length() - 1) == '"') {
				result.append(string.substring(1, string.length() - 1));
			} else {
				boolean escape = false;
				char currentchar;
				for (int i = 0; i < string.length(); i++) {
					currentchar = string.charAt(i);
					if (escape) {
						escape = false;
						result.append(currentchar);
					} else {
						if (currentchar == '\\') {
							escape = true;
						} else {
							result.append(currentchar);
						}
					}
				}
			}
		}
		return result.toString();
	}

	public static List<String> safeSplit(String string, char delimiter) {
		List<String> result = new ArrayList<String>();
		if (string.length() == 0) {
			result.add("");
			return result;
		}
		boolean literal = false;
		boolean escape = false;
		int startpos = 0;
		int i = 0;
		char currentchar;
		while (i < string.length()) {
			currentchar = string.charAt(i);
			if (currentchar == '"' && !escape) {
				literal = !literal;
			}
			if (!literal && (currentchar == delimiter && !escape)) {
				result.add(string.substring(startpos, i));
				startpos = i + 1;
			}
			if (currentchar == '\\') {
				escape = !escape;
			} else {
				escape = false;
			}
			i++;
		}
		result.add(string.substring(startpos, i));
		return result;
	}

	public static boolean containsNumber(String string) {
		for (int i = 0; i < string.length(); i++) {
			if ((int) string.charAt(i) < 58 && (int) string.charAt(i) > 47) {
				return true;
			}
		}
		return false;
	}

	public static int countNumbers(String string) {
		int total = 0;
		for (int i = 0; i < string.length(); i++) {
			if ((int) string.charAt(i) < 58 && (int) string.charAt(i) > 47) {
				total++;
			}
		}
		return total;
	}

	public static boolean containsLetter(String string) {
		for (int i = 0; i < string.length(); i++) {
			if (Character.isLetter(string.charAt(i))) {
				return true;
			}
		}
		return false;
	}

	public static int countLetters(String string) {
		int total = 0;
		for (int i = 0; i < string.length(); i++) {
			if (Character.isLetter(string.charAt(i))) {
				total++;
			}
		}
		return total;
	}

	public static List<String> mapToWords(String string) {
		List<String> result = new ArrayList<String>();

		int start = 0;
		int i = 0;
		for (; i < string.length(); i++) {
			char ch = string.charAt(i);
			if (!Character.isLetterOrDigit(ch)
					&& !(ch == '\'' && i > 0 && Character.isLetter(string.charAt(i - 1)) && string.length() - 1 > i && string.charAt(i + 1) == 's' && (string
							.length() - 2 == i || !Character.isLetterOrDigit(string.charAt(i + 2))))) { // leaves ' in possesive pattern
				if (start != i) {
					result.add(string.substring(start, i));
				}
				start = i + 1;
			}
		}
		if (start != i) {
			result.add(string.substring(start, i));
		}
		return result;
	}

	public static String now() {
		Date d = new Date();
		DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
		return df.format(d);
	}

	public static void outputWithTime(String message) {
		System.out.println(now() + "\t" + message);
	}

	public static int countsCharactersInUpperCase(String string) {
		int uppercase = 0;
		int charInt = 0;
		for (int i = 0; i < string.length(); i++) {
			charInt = (int) string.charAt(i);
			if (charInt > 64 && charInt < 91) {
				uppercase++;
			}
		}
		return uppercase;
	}

	public static int countsCharactersInLowerCase(String string) {
		int lowercase = 0;
		int charInt = 0;
		for (int i = 0; i < string.length(); i++) {
			charInt = (int) string.charAt(i);
			if (charInt > 96 && charInt < 123) {
				lowercase++;
			}
		}
		return lowercase;
	}

	public static String findBetween(String source, String pre, String post) {
		int start = source.indexOf(pre);
		if (start == -1)
			return "";
		int end = source.indexOf(post, start + pre.length());
		if (end == -1)
			return "";
		return source.substring(start + pre.length(), end);
	}

	public static List<String> multiFindBetween(String source, String pre, String post) {
		List<String> result = new ArrayList<String>();
		int start = 0;
		int end = 0;
		while (start != -1 && end != -1) {
			start = source.indexOf(pre, end);
			if (start != -1) {
				end = source.indexOf(post, start + pre.length());
				if (end != -1)
					result.add(source.substring(start + pre.length(), end));
			}
		}
		return result;
	}

	public static String wordWrap(String text, int lineLength) {
		text = text.trim();
		if (text.length() < lineLength)
			return text;
		if (text.substring(0, lineLength).contains("\n"))
			return text.substring(0, text.indexOf("\n")).trim() + "\n\n" + wordWrap(text.substring(text.indexOf("\n") + 1), lineLength);
		int place = Math.max(Math.max(text.lastIndexOf(" ", lineLength), text.lastIndexOf("\t", lineLength)), text.lastIndexOf("-", lineLength));
		return text.substring(0, place).trim() + "\n" + wordWrap(text.substring(place), lineLength);
	}

	public static boolean isDate(String string) {
		if (string.length() == 10) {
			if ((string.charAt(4) == '-' || string.charAt(4) == '/') || (string.charAt(4) == string.charAt(7)))
				try {
					int year = Integer.parseInt(string.substring(0, 4));
					if (year < 1700 || year > 2200)
						return false;
					int month = Integer.parseInt(string.substring(5, 7));
					if (month < 1 || month > 12)
						return false;
					int day = Integer.parseInt(string.substring(8, 10));
					if (day < 1 || day > 31)
						return false;
					return true;
				} catch (Exception e) {
					return false;
				}
		} else if (string.length() == 8) {
			if ((string.charAt(2) == '-' || string.charAt(5) == '/') || (string.charAt(2) == string.charAt(5)))
				try {
					Integer.parseInt(string.substring(6, 8));
					int month = Integer.parseInt(string.substring(0, 2));
					if (month < 1 || month > 12)
						return false;
					int day = Integer.parseInt(string.substring(3, 5));
					if (day < 1 || day > 31)
						return false;
					return true;
				} catch (Exception e) {
					return false;
				}

		}
		return false;
	}

}
