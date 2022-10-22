package cse471termproject;

public class HttpResponse {
	public static int statusCode;
	public static int stat;
    public static int getStat() {
		return stat;
	}

	public static void setStat(int stat) {
		HttpResponse.stat = stat;
	}

	public static int getStatusCode() {
		return statusCode;
	}

	public static void setStatusCode(int statusCode) {
		HttpResponse.statusCode = statusCode;
	}

	String reasonPhrase;
    MimeHeader mh;
    public HttpResponse(String response) {

    	String[] split_1 = response.split("\n",2);
    	String split_2 = split_1[0];
    	String[] split_3 = split_2.split(" ", 3);
    	
    	statusCode = Integer.parseInt(split_3[1]);
    	reasonPhrase = split_3[2];
    	String raw_mime_header = split_3[1];
    	mh = new MimeHeader(raw_mime_header);
    	
    	setStat(statusCode);	
    		
        
    }

    public HttpResponse(int code, String reason, MimeHeader m) {
        statusCode = code;
        reasonPhrase = reason;
        mh = m;
        mh.put("Connection", "close");
    }

    public String toString() {
        return "HTTP/1.1 " + statusCode + " " + reasonPhrase + "\r\n" + mh + "\r\n";
    }
}
