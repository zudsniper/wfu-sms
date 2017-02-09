package cc.holstr.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegExp {

	public static List<String> search(String total, String exp) {
		return search(total, exp, new int[0]);
	}

	public static List<String> search(String total, String exp, int... patterns) {
		List<String> output = new ArrayList<String>();
		try {
			Pattern p = compilePattern(exp, patterns);
				Matcher match = p.matcher(total);
				if (match.find()) {
					for (int i = 0; i < match.groupCount(); i++) {
						output.add(match.group(i));
					}
				}
		} catch(java.util.regex.PatternSyntaxException e) {
			e.printStackTrace();
		}
		return output;
	}
	public static String find(String total, String exp) {
		return find(total, exp, new int[0]);
	}

	public static String find(String total, String exp, int... patterns) {
		try {

			Pattern p = compilePattern(exp, patterns);
			Matcher match = p.matcher(total);
			if(match.find()){
				return match.group(0);
			}
		} catch(java.util.regex.PatternSyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static boolean contains(String total, String exp) {
		return contains(total, exp, new int[0]);
	}

	public static boolean contains(String total, String exp, int... patterns) {
		try {
			Pattern p = compilePattern(exp, patterns);
			Matcher match = p.matcher(total);
			if(match.find()){
				return true;
			}
		} catch(java.util.regex.PatternSyntaxException e) {
			e.printStackTrace();
		}
		return false;
	}

	private static Pattern compilePattern(String exp, int[] patterns) {
		Pattern p = null;
		if(patterns.length>0) {
			int finalPattern = patterns[0];
			for(int i : patterns) {
				finalPattern = finalPattern | i;
			}
			p= Pattern.compile(exp, finalPattern);
		} else {
			p = Pattern.compile(exp);
		}
		return p;
	}
}

