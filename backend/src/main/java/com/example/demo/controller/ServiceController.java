package com.example.demo.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.MonitoredService;
import com.example.demo.model.PingLog;
import com.example.demo.repository.MonitoredServiceRepository;
import com.example.demo.repository.PingLogRepository;

@CrossOrigin(origins = "*") 
@RestController
@RequestMapping("/api/services")
public class ServiceController {

    private final MonitoredServiceRepository serviceRepository;
    private final PingLogRepository pingLogRepository;

    public ServiceController(MonitoredServiceRepository serviceRepository, PingLogRepository pingLogRepository) {
        this.serviceRepository = serviceRepository;
        this.pingLogRepository = pingLogRepository;
    }

    //lista os sites monitorados
    @GetMapping
    public List<MonitoredService> getAllServices() {
        return serviceRepository.findAll();
    }

    //cadastra um novo site para monitoramento
    @PostMapping
    public MonitoredService createService(@RequestBody MonitoredService service) {
        return serviceRepository.save(service);
    }

    //busca o histórico de pings de um site monitorado específico
    @GetMapping("/{id}/history")
    public List<PingLog> getServiceHistory(@PathVariable Long id) {
        return pingLogRepository.findByServiceIdOrderByTimestampDesc(id);
    }

    //devolve um resumo visual do status do site monitorado
    @GetMapping("/{id}/stats")
    public ServiceStats getServiceStats(@PathVariable Long id) {
        List<PingLog> history = pingLogRepository.findByServiceIdOrderByTimestampDesc(id);
        if (history.isEmpty()) {
            return new ServiceStats(100.0, 0, "UNKNOWN");
        }

        long successfulPings = history.stream().filter(p -> p.getStatusCode() >= 200 && p.getStatusCode() < 300).count();
        double uptimePercentage = ((double) successfulPings / history.size()) * 100;
        int lastStatusCode = history.get(0).getStatusCode();

        return new ServiceStats(
            Math.round(uptimePercentage * 100.0) / 100.0, 
            history.size(), 
            (lastStatusCode >= 200 && lastStatusCode < 300) ? "ONLINE" : "OFFLINE"
        );
    }

    //dto para enviar o resumo visual do status do site monitorado
    public record ServiceStats(double uptimePercentage, int totalChecks, String status) {}
}