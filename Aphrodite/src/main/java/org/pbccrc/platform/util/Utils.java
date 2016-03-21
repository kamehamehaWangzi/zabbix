package org.pbccrc.platform.util;

import java.util.List;

public class Utils {
	
	public static String convertListToStringWithoutBracket(List<String> list) {
		if(list == null) {
			return null;
		}
		
		String result = list.toString().replace("[", "").replace("]", "").replace(", ", ",");
		return result;
	}

}
