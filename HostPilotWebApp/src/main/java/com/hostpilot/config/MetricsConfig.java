package com.hostpilot.config;

import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;

public class MetricsConfig {

    private static final PrometheusMeterRegistry registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);

    // Bloque estático para configurar el registro una sola vez cuando la clase es cargada.
    static {
        // Añadir métricas automáticas de la JVM y del sistema
        new JvmMemoryMetrics().bindTo(registry);
        new JvmGcMetrics().bindTo(registry);
        new JvmThreadMetrics().bindTo(registry);
        new ProcessorMetrics().bindTo(registry);
    }

    public static PrometheusMeterRegistry getRegistry() {
        return registry;
    }
}