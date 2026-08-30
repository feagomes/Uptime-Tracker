package com.example.demo.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.demo.model.MonitoredService;
import com.example.demo.model.PingLog;
import com.example.demo.repository.MonitoredServiceRepository;
import com.example.demo.repository.PingLogRepository;

import jakarta.annotation.PostConstruct;

@Service
public class PingService {

    private final MonitoredServiceRepository serviceRepository;
    private final PingLogRepository pingLogRepository;
    private final HttpClient httpClient;

    public PingService(MonitoredServiceRepository serviceRepository, PingLogRepository pingLogRepository) {
        this.serviceRepository = serviceRepository;
        this.pingLogRepository = pingLogRepository;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }

    
    @PostConstruct
    public void seedInitialServices() {
        if (serviceRepository.count() == 0) {
            serviceRepository.save(new MonitoredService("Google", "https://www.google.com"));
            serviceRepository.save(new MonitoredService("GitHub", "https://www.github.com"));
            
        }
    }

    @Scheduled(fixedRate = 30000)
    public void checkServices() {
        List<MonitoredService> services = serviceRepository.findAll();

        for (MonitoredService service : services) {
            long startTime = System.currentTimeMillis();
            int statusCode;

            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(service.getUrl()))
                        .GET()
                        .timeout(Duration.ofSeconds(5))
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                        .build();

                HttpResponse<Void> response = httpClient.send(request, HttpResponse.BodyHandlers.discarding());
                statusCode = response.statusCode();
            } catch (IOException e) {
                statusCode = 503;
            } catch (InterruptedException e) {
                statusCode = 503;
                Thread.currentThread().interrupt();
            }

            long responseTimeMs = System.currentTimeMillis() - startTime;

            PingLog log = new PingLog(service, statusCode, responseTimeMs);
            pingLogRepository.save(log);

            System.out.println("Ping em [" + service.getName() + "] | Status: " + statusCode + " | Tempo: " + responseTimeMs + "ms");
        }
    }
}