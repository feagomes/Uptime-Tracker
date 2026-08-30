# 📡 Uptime Tracker - Full-Stack Monitoring System

 **https://uptime-tracker-five.vercel.app/**

Sistema de monitoramento de disponibilidade (Uptime) de serviços web pra checar a saúde de URLs em tempo real, contornar firewalls e exibir o histórico de latência e status HTTP.

## Tecnologias Utilizadas
* **Backend:** Java 21, Spring Boot, Spring Data JPA
* **Frontend:** React, Vite, Axios
* **Banco de Dados:** PostgreSQL
* **Infraestrutura:** Docker, Render (Backend/DB), Vercel (Frontend)

## Principais problemas resolvidos
* **Bypass de Anti-Bot (WAF):** Implementação de custom headers (`User-Agent`) e resolução de redirecionamentos (HTTP 301/302) para monitorar sites estritos como LinkedIn.
* **Cold Start & Deploy:** Containerização do backend com Docker para implantação no Render e configuração de CORS para comunicação fluida com o frontend na Vercel.
* **Concorrência:** Uso de `@Scheduled` no Spring Boot para pings automatizados e assíncronos a cada 30 segundos, sem travar a thread principal.

## Pra rodar localmente
1. Clone o repositório.
2. Na pasta `backend`, rode `mvn spring-boot:run`.
3. Na pasta `frontend`, rode `npm install` e depois `npm run dev`.
