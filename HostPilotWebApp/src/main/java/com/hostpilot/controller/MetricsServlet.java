package com.hostpilot.controller;

import com.hostpilot.config.MetricsConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;

@WebServlet("/metrics")
public class MetricsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Obtenemos la instancia de nuestro registro de métricas
        PrometheusMeterRegistry registry = MetricsConfig.getRegistry();

        try {
            // Establecemos el tipo de contenido que Prometheus espera
            resp.setStatus(HttpServletResponse.SC_OK);
            
          
            resp.setContentType("text/plain; version=0.0.4; charset=utf-8");
            
            // Usamos el método scrape() para obtener todas las métricas en el formato correcto
            Writer writer = resp.getWriter();
            registry.scrape(writer);
            writer.flush();

        } catch (IOException e) {
            // En caso de error, lo registramos
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throw new ServletException("Error al escribir las metricas de Prometheus", e);
        }
    }
}