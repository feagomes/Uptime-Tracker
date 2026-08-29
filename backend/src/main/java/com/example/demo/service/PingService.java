package com.example.demo.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.demo.model.ServiceCheck;
import com.example.demo.repository.ServiceCheckRepository;

@Service
public class PingService {

    private final ServiceCheckRepository repository;
    private final HttpClient httpClient;

    // Lista de URLs para monitorar
    private final List<String> urlsToMonitor = List.of(
            "https://www.google.com",
            "https://www.github.com",
            "http://httpbin.org/status/500" //exemplo que retorna erro 500 para teste
    );

    public PingService(ServiceCheckRepository repository) {
        this.repository = repository;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
    }

    //a cada 30 segundos
    @Scheduled(fixedRate = 30000)
    public void checkServices() {
        for (String url : urlsToMonitor) {
            long startTime = System.currentTimeMillis();
            int statusCode;

            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .GET()
                        .timeout(Duration.ofSeconds(5))
                        .build();

                HttpResponse<Void> response = httpClient.send(request, HttpResponse.BodyHandlers.discarding());
                statusCode = response.statusCode();
            } catch (Exception e) {
                statusCode = 503; //caso de erro de rede
            }

            long responseTimeMs = System.currentTimeMillis() - startTime;

            ServiceCheck check = new ServiceCheck(
                    url.replaceAll("https?://", ""),
                    url,
                    statusCode,
                    responseTimeMs,
                    LocalDateTime.now()
            );

            repository.save(check);
            System.out.println("Ping executado para: " + url + " | Status: " + statusCode + " | Tempo: " + responseTimeMs + "ms");
        }
    }
}