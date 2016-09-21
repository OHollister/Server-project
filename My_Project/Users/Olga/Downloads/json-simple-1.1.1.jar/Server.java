import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URISyntaxException;
import java.util.ArrayList;
import javax.servlet.http.HttpServlet;
import javax.servlet.*;
import javax.servlet.http.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/* PROGRAM
 * 
 *  A java network service that listens for HTTP requests on TCP port 80. 
 *  The service should respond with an HTTP 200 response if the URL "/" is called using a GET request. 
 *  It should also return a JSON array with 3 elements in it for this request. 
 *  IF any other URL is called, the service should respond with HTTP status code 400 and the 
 *  JSON response `{ error: "route does not exist" }
 *  Example: user calls route "/login"
 * 
 */


//http://localhost:1234/


public class Server {

	public static void main(String args[]) throws IOException,
			ServletException, InterruptedException, URISyntaxException {
		
		System.out.println("TEST");

		final ServerSocket server = new ServerSocket();
		server.setReuseAddress(true);
		server.bind(new InetSocketAddress(1234));

		final boolean okToContinue = true;
		/* Without infinite loop-server will be shutdown */
		while (okToContinue) {

			Thread t = new Thread(new Runnable() {
				@Override
				public void run() {

					try {

						Socket socket = server.accept();
						System.out.println((char) socket.getInputStream()
								.read());

						/*
						 * read HTTP REQUEST from the client socket
						 * When you connect to -browser will send a GETHTTP request to
						 * the server
						 */
						System.out.println("Port is " + server.getLocalPort());

						/* my ip address */
						InetAddress ip = InetAddress.getLocalHost();
						String hostname = ip.getHostName();
						System.out.println("IP " + ip);

						/*
						 * Find the client's host name, and IP address
						 */
						InetAddress inetAddress = socket.getInetAddress();

						/* Show the host name for this IP address */
						System.out.println("Client's host name is "
								+ inetAddress.getHostName() + "\n");

						/*
						 * Read the content of request using InputStream opened
						 * from the client socket
						 */
						InputStreamReader isr = new InputStreamReader(socket
								.getInputStream());

						/* BufferedReader to read multiple lines */
						BufferedReader reader = new BufferedReader(isr);
						ArrayList<String> httpRequest = new ArrayList<String>();
						String line = reader.readLine();

						while (!line.isEmpty()) {
							httpRequest.add(line);
							System.out.println(line);
							line = reader.readLine();
						}

						String methodAndUri = httpRequest.get(0);
						System.out.println("This is my first line: "
								+ " ' " + methodAndUri + " ' ");
						/*Split Uri into two pieces*/
						String[] partsOfUri = methodAndUri.split(" ",3); 
						String requestPart = partsOfUri[0];// Get method
						String uriPart = partsOfUri[1];// Uri
						String httpPart = partsOfUri[2];// http Part
						System.out.println(" ' " +requestPart +" ' "); 
						System.out.println(" ' " + uriPart +" ' ");
						System.out.println(" ' " + httpPart +" ' ");
					
						/*
						 * send HTTP RESPONSE back to the client
						 * http://localhost:1234 socket will automatically closed
						 * by Java once you are done with response
						 */
						if (uriPart.trim().equals("/"))
								 {

							JSONArray array = new JSONArray();
							array.add("I");
							array.add("Love");
							array.add("Java");

							/*
							 * Reference for HTTP Headers
							 * https://www.w3.org/Protocols
							 * /rfc2616/rfc2616-sec6.html
							 */

							String httpValidResponse = "HTTP/1.1 200 OK\r\n"
									+ "Content-Type: application/json\r\n "
									+ "Accept: text/html, application/xhtml+xml, application/xml;q=0.9, */*;q=0.8, application/json\r\n"
									+ "Host: " + hostname + "\r\n\r\n"
									+ array.toJSONString();

							/*
							 * getBytes method returns an array of bytes in
							 * UTF-8 format
							 */
							socket.getOutputStream().write(
									httpValidResponse.getBytes("UTF-8"));

						} else {
							
							JSONArray array2 = new JSONArray();
							array2.add("HTTP/1.1 401 Unauthorized");
							String httpNotValidResponse = array2.toJSONString();
							socket.getOutputStream().write(
									httpNotValidResponse.getBytes("UTF-8"));
						}

						socket.close();

					} catch (IOException e) {
						/* Error can accure when creating a socket */
						System.out
								.println("YIKES! ERROR! Cannot create a socket! Terminating!");
						e.printStackTrace();
						System.exit(1);
					}
				}
			});
			t.start();
			t.join();

		} // End while

	}

}