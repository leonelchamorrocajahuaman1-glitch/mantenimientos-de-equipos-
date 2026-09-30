/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.mavenproject1;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.awt.Desktop;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

/** Servidor local para ejecutar la interfaz HTML desde Apache NetBeans. */
public class Mavenproject1 {

    private static final int PORT = 8080;
    private static final Map<String, String> TYPES = Map.of(
            ".html", "text/html; charset=UTF-8",
            ".css", "text/css; charset=UTF-8",
            ".js", "application/javascript; charset=UTF-8",
            ".json", "application/json; charset=UTF-8",
            ".png", "image/png", ".jpg", "image/jpeg", ".svg", "image/svg+xml");

    public static void main(String[] args) throws IOException {
        Path webRoot = Paths.get(System.getProperty("user.dir")).toAbsolutePath().normalize();
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/", exchange -> serve(exchange, webRoot));
        server.setExecutor(null);
        server.start();

        String address = "http://localhost:" + PORT + "/";
        System.out.println("MobileStore ejecutándose en " + address);
        System.out.println("Presiona Ctrl+C en NetBeans para detener el servidor.");
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(URI.create(address));
            }
        } catch (UnsupportedOperationException ex) {
            System.out.println("Abre manualmente: " + address);
        }
    }

    private static void serve(HttpExchange exchange, Path webRoot) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
            exchange.sendResponseHeaders(405, -1);
            exchange.close();
            return;
        }

        String requestPath = URLDecoder.decode(exchange.getRequestURI().getPath(), StandardCharsets.UTF_8);
        if (requestPath.equals("/")) requestPath = "/index.html";
        Path file = webRoot.resolve(requestPath.substring(1)).normalize();
        if (!file.startsWith(webRoot) || !Files.isRegularFile(file)) {
            byte[] body = "404 - Archivo no encontrado".getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
            exchange.sendResponseHeaders(404, body.length);
            try (OutputStream out = exchange.getResponseBody()) { out.write(body); }
            return;
        }

        String extension = "";
        String name = file.getFileName().toString();
        int dot = name.lastIndexOf('.');
        if (dot >= 0) extension = name.substring(dot).toLowerCase();
        exchange.getResponseHeaders().set("Content-Type", TYPES.getOrDefault(extension, "application/octet-stream"));
        byte[] body = Files.readAllBytes(file);
        exchange.sendResponseHeaders(200, body.length);
        try (OutputStream out = exchange.getResponseBody()) { out.write(body); }
    }
}
