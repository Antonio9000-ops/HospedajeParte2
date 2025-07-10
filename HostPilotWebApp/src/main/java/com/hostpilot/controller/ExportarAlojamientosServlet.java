package com.hostpilot.controller;

import com.hostpilot.model.Propiedad;
import com.hostpilot.service.PropiedadService;
import com.hostpilot.service.PropiedadServiceImpl; // <<< CORRECCIÓN 1: Import correcto
import com.hostpilot.service.ServiceException;     // <<< AÑADIR IMPORT
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;                             // <<< AÑADIR IMPORT (para logging)
import org.slf4j.LoggerFactory;                    // <<< AÑADIR IMPORT (para logging)

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/exportar/alojamientos")
public class ExportarAlojamientosServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExportarAlojamientosServlet.class);
    private PropiedadService propiedadService;

    @Override
    public void init() {
        // CORRECCIÓN 2: El tipo de la variable es la interfaz, la instancia es la implementación. Esto está bien.
        this.propiedadService = new PropiedadServiceImpl();
        LOGGER.info("ExportarAlojamientosServlet inicializado.");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        LOGGER.info("Iniciando la exportación de alojamientos a Excel.");
        
        try (Workbook workbook = new XSSFWorkbook()) {
            List<Propiedad> propiedades = propiedadService.obtenerTodasLasPropiedades();
            LOGGER.info("Se exportarán {} propiedades.", propiedades.size());

            Sheet sheet = workbook.createSheet("Alojamientos Hostpilot");
            
            // Ajustar anchos de columna para mejor visualización
            sheet.setColumnWidth(0, 2000);  // ID
            sheet.setColumnWidth(1, 10000); // Título
            sheet.setColumnWidth(2, 6000);  // Ciudad
            sheet.setColumnWidth(3, 4000);  // Precio
            sheet.setColumnWidth(4, 6000);  // Dirección
            sheet.setColumnWidth(5, 3000);  // Capacidad
            sheet.setColumnWidth(6, 2500);  // Rating

            // Estilo para la cabecera
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);
            headerCellStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Crear la fila de cabecera con los nombres correctos
            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "Título", "Ciudad", "Precio por Noche", "Dirección", "Capacidad", "Rating"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerCellStyle);
            }

            // Llenar con los datos usando los getters correctos
            int rowNum = 1;
            for (Propiedad prop : propiedades) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(prop.getId());
                // --- CORRECCIÓN 3: Usar los métodos getter correctos ---
                row.createCell(1).setCellValue(prop.getTitulo());
                row.createCell(2).setCellValue(prop.getCiudad());
                row.createCell(3).setCellValue(prop.getPrecioPorNoche());
                row.createCell(4).setCellValue(prop.getDireccion());
                row.createCell(5).setCellValue(prop.getCapacidad());
                row.createCell(6).setCellValue(prop.getRating());
            }

            // Configurar la respuesta HTTP para la descarga
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=\"reporte_alojamientos_hostpilot.xlsx\"");

            workbook.write(response.getOutputStream());
            LOGGER.info("Archivo Excel generado y enviado exitosamente.");

        } catch (ServiceException e) {
            LOGGER.error("Error de servicio al obtener las propiedades para exportar.", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error de servicio al generar el reporte.");
        } catch (Exception e) {
            LOGGER.error("Error inesperado al generar el archivo Excel.", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error inesperado al generar el reporte.");
        }
    }
}