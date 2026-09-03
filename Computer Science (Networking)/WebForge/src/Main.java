import server.HttpServer;

public class Main {
	
	public static void main(String[] args) {
		final int port = 4321;
		HttpServer server = new HttpServer(port);
		server.start();
	}
	
}