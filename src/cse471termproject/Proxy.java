package cse471termproject;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;
import java.io.File;
import java.io.IOException;
import java.io.FileWriter;

public class Proxy extends Thread{

	final static ArrayList<String> forbiddenAddresses = new ArrayList<>();
	public static ArrayList<String> reportwriter = new ArrayList<>();
	public static ServerSocket welcomeSocket;
	public static Socket connectionSocket;
	
	public static String addforbidden;
	public static int forbcounter = 0;
	
	public static String getforbd;
	
	public static String date;
	public static String getDate() {
		return date;
	}

	public static void setDate(String date) {
		Proxy.date = date;
	}

	public static String ipadr;
	public static String getIpadr() {
		return ipadr;
	}

	public static void setIpadr(String ipadr) {
		Proxy.ipadr = ipadr;
	}

	public static String domain;
	public static String getDomain() {
		return domain;
	}

	public static void setDomain(String domain) {
		Proxy.domain = domain;
	}

	public static String path;
	public static String getPath() {
		return path;
	}

	public static void setPath(String path) {
		Proxy.path = path;
	}

	public static String httpmethod;
	public static String getHttpmethod() {
		return httpmethod;
	}

	public static void setHttpmethod(String httpmethod) {
		Proxy.httpmethod = httpmethod;
	}

	public static int statcode;
	
	public static String localipaddress;
	
	

	public static String getLocalipaddress() {
		return localipaddress;
	}

	public static void setLocalipaddress(String localtoken) {
		Proxy.localipaddress = localtoken;
	}

	public static int getStatcode() {

		return statcode;
	}

	public static void setStatcode(int statcode) {
		Proxy.statcode = statcode;
	}

	public static String getGetforbd(int index) {
		
		return forbiddenAddresses.get(index);
	}

	public static void setGetforbd(String getforbd) {
		Proxy.getforbd = getforbd;
	}

	public static int getForbcounter() {
		forbcounter = forbiddenAddresses.size();

		return forbcounter;
	}

	public static void setForbcounter(int forbcounter) {
		Proxy.forbcounter = forbcounter;
	}

	public static String getAddforbidden() {
		return addforbidden;
	}

	public static void setAddforbidden(String addforbidden) {
		Proxy.addforbidden = addforbidden;
	}

	public static void addFobdn(String address)
	{

		forbiddenAddresses.add(address);
	}
	
	public static boolean welcomeflag = true;
	public static boolean whileflag = true;
	
	public static boolean isWhileflag() {
		return whileflag;
	}

	public static void setWhileflag(boolean whileflag) {
		Proxy.whileflag = whileflag;
	}

	public static boolean isFlag() {
		return welcomeflag;
	}

	public static void setFlag(boolean flag) {
		Proxy.welcomeflag = flag;
	}

	public static Runnable ProxyOpen() throws Exception // runnable
	{
		
		
		forbiddenAddresses.add("www.yandex.com.tr");
		forbiddenAddresses.add("www.apple.com");
		forbiddenAddresses.add("www.facebook.com");	

		ServerSocket welcomeSocket = new ServerSocket(8080);
		

		while (true) {

			Socket connectionSocket = welcomeSocket.accept();

			new ServerHandler(connectionSocket);
		}
	}
	
	public static void reportfile() throws IOException
	{
		File file = new File("report.txt");
		FileWriter writer = new FileWriter("report.txt");
		for(int i = 0; i < reportwriter.size();i++)
		{
			writer.write(reportwriter.get(i));
		}
		writer.close();
	}
}

class ServerHandler implements Runnable {

		Socket clientSocket;

		DataInputStream inFromClient;
		DataOutputStream outToClient;

		String host;
		String path;

		PrinterClass pC;

		public ServerHandler(Socket s) {
			try {
				clientSocket = s;
				pC = new PrinterClass();

				pC.add("A connection from a client is initiated...");

				inFromClient = new DataInputStream(s.getInputStream());
				outToClient = new DataOutputStream(s.getOutputStream());

				Thread th = new Thread(this);
				th.start();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}

		@Override
		public void run() {
			
			try {
				String hd = getHeader(inFromClient);

				int sp1 = hd.indexOf(' ');
				int sp2 = hd.indexOf(' ', sp1 + 1);
				int eol = hd.indexOf('\r');

				String reqHeaderRemainingLines = hd.substring(eol + 2);

				MimeHeader reqMH = new MimeHeader(reqHeaderRemainingLines);
				
				String url = hd.substring(sp1 + 1, sp2);

				String method = hd.substring(0, sp1);

				host = reqMH.get("Host");
				
				InetAddress IPAddress = InetAddress.getByName(host);
				InetAddress localip = InetAddress.getLocalHost();
				
				String locip = localip.toString();
				String[] localtoken = locip.split("/");
				
				String realip = IPAddress.toString();
				String[] tokens = realip.split("/");					
				
				reqMH.put("Connection", "close");

				URL u = new URL(url);

				String tmpPath = u.getPath();

				String tmpHost = u.getHost();

				path = ((tmpPath == "") ? "/" : tmpPath);
				
				Proxy.setLocalipaddress(localtoken[1]);
				Proxy.setHttpmethod(method);
				Proxy.setDomain(host);
				Proxy.setIpadr(tokens[1]);
				Proxy.setPath(path);
				if (Proxy.forbiddenAddresses.contains(host)) 
				{
					pC.add("Connection blocked to the host due to the proxy policy");
					outToClient.writeBytes(createErrorPage(401, "Not Authorized", method));
				} 
				else if (host.equals(tmpHost)) 
				{
					if (method.equalsIgnoreCase("get")) 
					{	
						pC.add("Client requests...\r\nHost: " + host + "\r\nPath: " + path);

						handleProxy(url, reqMH,"GET");	
					}
					else if(method.equalsIgnoreCase("head"))
					{
						pC.add("Client requests...\r\nHost: " + host + "\r\nPath: " + path);

						handleProxy(url, reqMH,"HEAD");
					}
					else if(method.equalsIgnoreCase("post"))
					{
						pC.add("Client requests...\r\nHost: " + host + "\r\nPath: " + path);

						handleProxy(url, reqMH,"POST");
					}
					else if(method.equalsIgnoreCase("connect"))
					{
						pC.add("Client requests...\r\nHost: " + host + "\r\nPath: " + path);

						handleProxy(url, reqMH,"CONNECT");
					}
					else 
					{
						pC.add("Requested method " + method + " is not allowed on proxy server");
//						String finalstr ="LocalIP: " + Proxy.getLocalipaddress() + "   - Date: " + Proxy.getDate() + "   - WebsiteIP: " + Proxy.getIpadr() + "   - Domain: " + Proxy.getDomain() + "   - Path: " + Proxy.getPath() + "   - HttpMethod: " + Proxy.getHttpmethod() + "   - StatusCode: " + Proxy.getStatcode() + "\n";
//						Proxy.reportwriter.add(finalstr);
						outToClient.writeBytes(createErrorPage(405, "Method Not Allowed", method));
					}
				
				} 
				else 
				{
					pC.add("Error for request: " + url);
					String finalstr ="LocalIP: " + Proxy.getLocalipaddress() + "   - Date: " + Proxy.getDate() + "   - WebsiteIP: " + Proxy.getIpadr() + "   - Domain: " + Proxy.getDomain() + "   - Path: " + Proxy.getPath() + "   - HttpMethod: " + Proxy.getHttpmethod() + "   - StatusCode: " + Proxy.getStatcode() + "\n";
					Proxy.reportwriter.add(finalstr);
				}
				pC.removeThread();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private void handleProxy(String url, MimeHeader reqMH,String str) {
			try {
				pC.add("\r\nInitiating the server connection");
				Socket sSocket = new Socket(host, 80);
				DataInputStream inFromServer = new DataInputStream(sSocket.getInputStream());
				DataOutputStream outToServer = new DataOutputStream(sSocket.getOutputStream());
				reqMH.put("User-Agent", reqMH.get("User-Agent") + " via CSE471 Proxy");

				pC.add("\r\nSending to server...\r\n" + str + path + " HTTP/1.1\r\n" + reqMH + "\r\n");

				outToServer.writeBytes(str + " " + path + " HTTP/1.1\r\n" + reqMH + "\r\n");
								
				pC.add("HTTP request sent to: " + host);

				
				
				ByteArrayOutputStream bAOS = new ByteArrayOutputStream(10000);

				int a;

				byte[] buffer = new byte[1024];

				while ((a = inFromServer.read(buffer)) != -1) {
					bAOS.write(buffer, 0, a);
				}

				byte[] response = bAOS.toByteArray();

				String rawResponse = new String(response);

				String responseHeader = rawResponse.substring(0, rawResponse.indexOf("\r\n\r\n"));

				pC.add("\r\nResponse Header\r\n" + responseHeader);
					
		        String[] findcode = responseHeader.split(" ");
		 
		        int i = Integer.parseInt(findcode[1]); 
		        Proxy.setStatcode(i);
		            
				
				pC.add("\r\n\r\nGot " + response.length + " bytes of response data...\r\n"
						+ "Sending it back to the client...\r\n");

				outToClient.write(response);

				outToClient.close();

				sSocket.close();

				pC.add("Served http://" + host + path + "\r\nExiting ServerHelper thread...\r\n"
						+ "\r\n----------------------------------------------------" + "\r\n");
				Proxy.setDomain(host);
				String finalstr ="LocalIP: " + Proxy.getLocalipaddress() + "   - Date: " + Proxy.getDate() + "   - WebsiteIP: " + Proxy.getIpadr() + "   - Domain: " + Proxy.getDomain() + "   - Path: " + Proxy.getPath() + "   - HttpMethod: " + Proxy.getHttpmethod() + "   - StatusCode: " + Proxy.getStatcode() + "\n";
				Proxy.reportwriter.add(finalstr);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private String createErrorPage(int code, String msg, String address) {
			String html_page = code + " " + msg + "\nError when fetching URL:" + address;
	
			MimeHeader mh = makeMimeHeader("text/html", html_page.length());
			HttpResponse hr = new HttpResponse(code, msg, mh);
			
			Proxy.setStatcode(code);

			String finalstr ="LocalIP: " + Proxy.getLocalipaddress() + "   - Date: " + Proxy.getDate() + "   - WebsiteIP: " + Proxy.getIpadr() + "   - Domain: " + Proxy.getDomain() + "   - Path: " + Proxy.getPath() + "   - HttpMethod: " + Proxy.getHttpmethod() + "   - StatusCode: " + Proxy.getStatcode() + "\n";
			Proxy.reportwriter.add(finalstr);
			return hr + html_page;
			
		}

		private MimeHeader makeMimeHeader(String type, int length) {
			MimeHeader mh = new MimeHeader();
			Date d = new Date();
			TimeZone gmt = TimeZone.getTimeZone("GMT");
			SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy hh:mm:ss zzz");
			SimpleDateFormat writefile = new SimpleDateFormat("dd-MMM-yyyy-hh:mm:ss-zzz");
			sdf.setTimeZone(gmt);
			writefile.setTimeZone(gmt);
			String sdf_date = sdf.format(d);
			String writefile_date = writefile.format(d);
			Proxy.setDate(writefile_date);

			mh.put("date", sdf_date);
			mh.put("server", "CSE471");
			mh.put("content-type", type);

			if (length >= 0)
				mh.put("Content-Length", String.valueOf(length));
			return mh;
		}

		public String getHeader(DataInputStream in) throws Exception {
			byte[] header = new byte[1024];

			int data;
			int h = 0;

			while ((data = in.read()) != -1) {
				header[h++] = (byte) data;

				if (header[h - 1] == '\n' && header[h - 2] == '\r' && header[h - 3] == '\n' && header[h - 4] == '\r') {
					break;
				}
			}
			
			return new String(header, 0, h);
		}

	}



