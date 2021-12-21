package game.reversi;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ReversiServer {

    private final HttpServer server;
    private final ReversiHandler onNewGame = new ReversiHandler();
    private final ReversiHandler onMove = new ReversiHandler();

    public ReversiServer(String hostname, int port) throws IOException {
        server = HttpServer.create(new InetSocketAddress(hostname, port), 0);
        server.createContext("/new", onNewGame);
        server.createContext("/move", onMove);
        server.setExecutor(Executors.newFixedThreadPool(4));
        server.start();
    }

    public void onNewGame(Function<String, String> doThis) {
        onNewGame.subscribe(doThis);
    }

    public void onMove(Function<String, String> doThis) {
        onMove.subscribe(doThis);
    }

    private static class ReversiHandler implements HttpHandler {
        private Function<String, String> doThis = (unused) -> "";

        public void subscribe(Function<String, String> doThis) {
            this.doThis = doThis;
        }

        public void handle(HttpExchange exchange) {
            String body = new BufferedReader(new InputStreamReader(exchange.getRequestBody()))
                    .lines().parallel().collect(Collectors.joining("\n"));
            String response = doThis.apply(body);
            OutputStream output = exchange.getResponseBody();
            try {
                exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                exchange.sendResponseHeaders(200, response.length());
                output.write(response.getBytes());
                output.flush();
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
