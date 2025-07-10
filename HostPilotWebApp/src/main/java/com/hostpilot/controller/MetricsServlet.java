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

/**
 * Este servlet expone las métricas recolectadas por Micrometer
 * en un formato que Prometheus puede entender.
 * Utiliza la clase de configuración MetricsConfig para obtener el registro de métricas.
 */
@WebServlet("/metrics")
public class MetricsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 1. Obtenemos la instancia única del registro de métricas desde nuestra clase de configuración.
        // Como es estática, siempre será la misma instancia.
        PrometheusMeterRegistry registry = MetricsConfig.getRegistry();

        try {
            // 2. Establecemos el tipo de contenido y el estado HTTP OK.
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType("text/plain; version=0.0.4; charset=utf-8");
            
            // 3. Usamos el método scrape() para escribir todas las métricas en la respuesta HTTP.
            try (Writer writer = resp.getWriter()) {
                registry.scrape(writer);
            }

        } catch (IOException e) {
            // En caso de error al escribir la respuesta, lo registramos y enviamos un error interno del servidor.
            System.err.println("Error al escribir las métricas de Prometheus: " + e.getMessage());
            if (!resp.isCommitted()) {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al generar las métricas.");
            }
        }
    }
}