package org.pbccrc.platform.util;


import java.util.List;

public class StringUtils {
	
	static String STR_VARIABLE = "";

	public static void splitParams(List<String> params, String preffix, String suffix, String args) {

		if (args.indexOf(preffix) != -1 && args.indexOf(suffix) != -1) {

			STR_VARIABLE = args.substring(args.indexOf(preffix) + preffix.length(), args.indexOf(suffix));
			args = args.substring(args.indexOf(suffix) + suffix.length());
			params.add(STR_VARIABLE);
			if (!"".equals(args) && args != null) {
				splitParams(params, preffix, suffix, args);
			}
		}

	}
	
	public static boolean isNull(String str) {
		
		if (null == str || STR_VARIABLE.equals(str) || str.length() == 0) {
			return true;
		}
		
		return false;
	}
	
}
