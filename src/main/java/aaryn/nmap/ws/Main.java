package aaryn.nmap.ws;

import org.glassfish.grizzly.http.server.CLStaticHttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import aaryn.nmap.util.NmapWebProperties;

import java.io.IOException;
import java.net.URI;

/**
 * Main application -- this launches on localhost 8080 (or whatever is specified
 * by grizzly_port in properties) and runs until the user hits ENTER in the
 * console window used to launch this.
 * 
 * @author aaryno1
 *
 */
public class Main {
	// Base URI the Grizzly HTTP server will listen on
	public static final String BASE_URI;
	static {

		String port;
		try {
			port = NmapWebProperties.getInstance().getProperties()
					.getProperty("grizzly_port");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Using port 8080");
			port = "8080";
		}
		BASE_URI = "http://localhost:" + port + "/";
	}

	/**
	 * Starts Grizzly HTTP server exposing JAX-RS resources defined in this
	 * application.
	 * 
	 * @return Grizzly HTTP server.
	 */
	public static HttpServer startServer() {
		// create a resource config that scans for JAX-RS resources and
		// providers
		// in aaryn.nmap.ws package
		final ResourceConfig rc = new ResourceConfig()
				.packages("aaryn.nmap.ws");

		// create and start a new instance of grizzly http server
		// exposing the Jersey application at BASE_URI
		HttpServer httpServer = GrizzlyHttpServerFactory.createHttpServer(
				URI.create(BASE_URI), rc);

		CLStaticHttpHandler httpHandler = new CLStaticHttpHandler(
				Main.class.getClassLoader(), "/html/", "/js/");
		httpServer.getServerConfiguration()
				.addHttpHandler(httpHandler, "/html");

		return httpServer;
	}

	/**
	 * Main method.
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		final HttpServer server = startServer();
		System.out
				.println(String
						.format("Launching simple nmap web-based scanner. "
								+ "Start at %shtml/index.html\nHit enter to stop it...",
								BASE_URI));
		System.in.read();
		server.stop();
	}
}
