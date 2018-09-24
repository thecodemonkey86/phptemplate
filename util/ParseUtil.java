package util;

import java.io.IOException;

public class ParseUtil {

	public static String substrToNextChar(String html, int offset, char c) throws IOException {
		int endIndex = html.indexOf(c, offset);
		if (endIndex == -1) {
			throw new IOException("syntax error. Expected \'" + c +'\'');
		}
		return html.substring(offset, endIndex);
	}
	
	public static Pair<String,Integer> getIndexAndSubstrToNextChar(String html, int offset, char c) throws IOException {
		int endIndex = html.indexOf(c, offset);
		if (endIndex == -1) {
			throw new IOException("syntax error. Expected \'" + c +'\'');
		}
		return new Pair<String, Integer>( html.substring(offset, endIndex), endIndex);
	}
	
	public static String substrToNextChar(String html, char[] chars, int fromIndex) throws IOException {
		Pair<Integer, Character> p = firstIndexOf(html, chars, fromIndex);
		if (p.getValue1() == -1) {
			throw new IOException("syntax error");
		}
		return html.substring(fromIndex, p.getValue1() );
	}
	
	public static Pair<Integer,Character> firstIndexOf(String s,char c1,char c2,int fromIndex) {
		 final int max = s.length();
	        if (fromIndex < 0) {
	            fromIndex = 0;
	        } else if (fromIndex >= max) {
	            // Note: fromIndex might be near -1>>>1.
	            return new Pair<Integer, Character>(-1, null);
	        }

	        for (int i = fromIndex; i < max; i++) {
                if (s.charAt(i) == c1) {
                    return new Pair<Integer, Character>(i,c1);
                } else  if (s.charAt(i) == c2) {
                	return new Pair<Integer, Character>(i,c2);
                }
            }
            return new Pair<Integer, Character>(-1, null);
	}
	
	public static Pair<Integer,Character> firstIndexOf(String s,char[] chars,int fromIndex) {
		 final int max = s.length();
	        if (fromIndex < 0) {
	            fromIndex = 0;
	        } else if (fromIndex >= max) {
	            // Note: fromIndex might be near -1>>>1.
	            return new Pair<Integer, Character>(-1, null);
	        }

	        for (int i = fromIndex; i < max; i++) {
	        	for(char c:chars) {
	        		if (s.charAt(i) == c) {
	                    return new Pair<Integer, Character>(i,c);
	        		}
	        	}
           }
           return new Pair<Integer, Character>(-1, null);
	}
	
	/*public static String substrToNextWhiteSpace(String html, int offset)
			throws IOException {
		int sp = html.indexOf(' ', offset);
		int tab = html.indexOf('\t', offset);
		int lf = html.indexOf('\n', offset);
		int cr = html.indexOf('\r', offset);

		int k = sp > -1 ? sp : -1;

		if (tab > -1 && tab < k) {
			k = tab;
		}
		if (lf > -1 && lf < k) {
			k = lf;
		}
		if (cr > -1 && cr < k) {
			k = cr;
		}
		if (k == -1) {
			throw new IOException("syntax error");
		}
		return html.substring(offset, k );
	}*/

	public static String substrToNextWhiteSpace(String html, int fromIndex) throws IOException {
		Pair<Integer, Character> p = firstIndexOf(html, new char[] {' ','\t','\n','\r'}, fromIndex);
		if (p.getValue1() == -1) {
			throw new IOException("syntax error");
		}
		return html.substring(fromIndex, p.getValue1() );
	}
	
	

	public static String dropWhitespaces(String string) {
		return StringUtil.dropAll(StringUtil.dropAll(string, '\r','\n','\t'),"  ");
	}
	
	
}
