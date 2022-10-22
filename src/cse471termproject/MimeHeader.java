package cse471termproject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;

public class MimeHeader extends HashMap<String, String> {

	public MimeHeader() {

	}

	public MimeHeader(String s) {
		parse(s);
	}

	private void parse(String data) {
		StringTokenizer st = new StringTokenizer(data, "\r\n");
		
		while (st.hasMoreTokens()) {
			String s = st.nextToken();
			
			int i = s.indexOf(':');
			String key = s.substring(0, i);
			String value = s.substring(i + 2);
			put(key, value);
			
		}
	}

	@Override
	public String toString() {
		String str = "";
		Iterator<String> e = keySet().iterator();
		while (e.hasNext()) {
			String key = e.next();
			String val = get(key);
			str += key + ": " + val + "\r\n";
		}
		return str;
	}

}
