package com.hostpilot.controller;

import com.hostpilot.config.DatabaseConfig;
import com.hostpilot.config.MySQLDatabaseConfig;
import com.hostpilot.dao.DAOException; // Importar DAOException
import com.hostpilot.dao.PagoDAO;
import com.hostpilot.dao.PagoDAOImpl;
import com.hostpilot.dao.PropiedadDAO;
import com.hostpilot.dao.PropiedadDAOImpl;
import com.hostpilot.dao.ReservaDAO;
import com.hostpilot.dao.ReservaDAOImpl;
import com.hostpilot.model.Pago;
import com.hostpilot.model.Propiedad;
import com.hostpilot.model.Reserva;
import com.hostpilot.security.SessionManager;
import com.hostpilot.service.PagoService;
import com.hostpilot.service.PagoServiceImpl;
import com.hostpilot.service.ReservaService;
import com.hostpilot.service.ReservaServiceImpl;
import com.hostpilot.service.ServiceException;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Element;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.Font;
import com.itextpdf.text.BaseColor;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional; // Importar Optional
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/download-pdf")
public class DownloadPdfController extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(DownloadPdfController.class.getName());
    private ReservaService reservaService;
    private PagoService pagoService;
    private PropiedadDAO propiedadDAO;
    private ReservaDAO reservaDAO;

    @Override
    public void init() throws ServletException {
        try {
            DatabaseConfig dbConfig = new MySQLDatabaseConfig();
            ReservaDAO reservaDAOImpl = new ReservaDAOImpl(dbConfig);
            PropiedadDAO propiedadDAOImpl = new PropiedadDAOImpl(dbConfig);
            PagoDAO pagoDAOImpl = new PagoDAOImpl(dbConfig);

            this.pagoService = new PagoServiceImpl(pagoDAOImpl);
            // Asegúrate de que ReservaServiceImpl tenga el constructor con PagoService
            this.reservaService = new ReservaServiceImpl(reservaDAOImpl, propiedadDAOImpl, this.pagoService); 
            this.propiedadDAO = propiedadDAOImpl;
            this.reservaDAO = reservaDAOImpl;

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al inicializar DownloadPdfController", e);
            throw new ServletException("Fallo al inicializar el controlador de PDF", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Long userId = SessionManager.getCurrentUserId(request);
        if (userId == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Debe iniciar sesión para descargar PDFs.");
            return;
        }

        String type = request.getParameter("type");
        if (type == null || type.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Tipo de descarga de PDF no especificado.");
            return;
        }

        try {
            response.setContentType("application/pdf");
            OutputStream os = response.getOutputStream();
            Document document = new Document();
            PdfWriter.getInstance(document, os);
            document.open();

            Font fontTitle = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, BaseColor.BLUE);
            Font fontSubtitle = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
            Font fontNormal = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);
            Font fontBold = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);

            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", new Locale("es", "ES"));
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", new Locale("es", "ES"));

            if ("reserva".equalsIgnoreCase(type)) {
                String reservaIdStr = request.getParameter("id");
                if (reservaIdStr == null || reservaIdStr.isEmpty()) {
                    throw new ServiceException("ID de reserva no especificado.");
                }
                int reservaId = Integer.parseInt(reservaIdStr);

                Reserva reserva = reservaService.buscarReservaPorId(reservaId);
                if (reserva == null || reserva.getIdUsuario() != userId.intValue()) {
                    throw new ServiceException("Reserva no encontrada o no pertenece al usuario.");
                }

                // Las llamadas a DAO aquí deben estar cubiertas por try-catch si no hay un Service para ellas
                // o si el Service no convierte DAOException a ServiceException.
                // Sin embargo, ya hemos hecho que los Service lancen ServiceException, así que esto debería funcionar
                // si los métodos del Service llaman a los DAO y capturan DAOException para relanzar ServiceException.
                Propiedad propiedad = propiedadDAO.buscarPorId((long) reserva.getIdPropiedad()).orElse(null);
                List<Pago> pagos = reservaDAO.obtenerPagosPorReserva(reservaId); // Esto puede lanzar DAOException

                response.setHeader("Content-Disposition", "attachment; filename=\"reserva_" + reserva.getId() + ".pdf\"");

                document.add(new Paragraph("Detalles de la Reserva", fontTitle));
                document.add(Chunk.NEWLINE);

                if (propiedad != null) {
                    document.add(new Paragraph("Propiedad: " + propiedad.getTitulo(), fontSubtitle));
                    document.add(new Paragraph("Ubicación: " + propiedad.getCiudad() + ", Perú", fontNormal));
                    document.add(new Paragraph("Precio por noche: S/" + String.format("%.2f", propiedad.getPrecioPorNoche()), fontNormal));
                    document.add(Chunk.NEWLINE);
                }

                document.add(new Paragraph("Reserva ID: " + reserva.getId(), fontBold));
                document.add(new Paragraph("Check-in: " + reserva.getFechaCheckin().format(dateFormatter), fontNormal));
                document.add(new Paragraph("Check-out: " + reserva.getFechaCheckout().format(dateFormatter), fontNormal));
                document.add(new Paragraph("Huéspedes: " + reserva.getNumeroAdultos() + " adultos, " + reserva.getNumeroNinos() + " niños, " + reserva.getNumeroBebes() + " bebés, " + reserva.getNumeroMascotas() + " mascotas", fontNormal));
                document.add(new Paragraph("Estado: " + reserva.getEstado(), fontNormal));
                document.add(new Paragraph("Total de la reserva: S/" + String.format("%.2f", reserva.getTotal()), fontBold));
                document.add(Chunk.NEWLINE);

                if (!pagos.isEmpty()) {
                    document.add(new Paragraph("Pagos Asociados:", fontSubtitle));
                    PdfPTable pagoTable = new PdfPTable(4);
                    pagoTable.setWidthPercentage(100);
                    pagoTable.setSpacingBefore(10f);
                    pagoTable.addCell(new PdfPCell(new Phrase("Fecha", fontBold)));
                    pagoTable.addCell(new PdfPCell(new Phrase("Método", fontBold)));
                    pagoTable.addCell(new PdfPCell(new Phrase("Monto", fontBold)));
                    pagoTable.addCell(new PdfPCell(new Phrase("Transacción ID", fontBold)));

                    for (Pago pago : pagos) {
                        pagoTable.addCell(new Phrase(pago.getFechaPago() != null ? pago.getFechaPago().format(dateTimeFormatter) : "N/A", fontNormal));
                        pagoTable.addCell(new Phrase(pago.getMetodo(), fontNormal));
                        pagoTable.addCell(new Phrase("S/" + String.format("%.2f", pago.getMonto()), fontNormal));
                        pagoTable.addCell(new Phrase(pago.getTransaccionId(), fontNormal));
                    }
                    document.add(pagoTable);
                }

            } else if ("historial".equalsIgnoreCase(type)) {
                response.setHeader("Content-Disposition", "attachment; filename=\"historial_pagos_" + userId + ".pdf\"");
                
                document.add(new Paragraph("Historial de Pagos", fontTitle));
                document.add(Chunk.NEWLINE);

                List<Pago> historialPagos = pagoService.obtenerHistorialPagosUsuario(userId.intValue());

                if (historialPagos.isEmpty()) {
                    document.add(new Paragraph("No hay pagos registrados para este usuario.", fontNormal));
                } else {
                    PdfPTable table = new PdfPTable(6);
                    table.setWidthPercentage(100);
                    table.setSpacingBefore(10f);
                    float[] columnWidths = {2f, 3f, 1.5f, 2f, 1.5f, 3f};
                    table.setWidths(columnWidths);

                    table.addCell(new PdfPCell(new Phrase("Fecha", fontBold)));
                    table.addCell(new PdfPCell(new Phrase("Propiedad", fontBold)));
                    table.addCell(new PdfPCell(new Phrase("Reserva ID", fontBold)));
                    table.addCell(new PdfPCell(new Phrase("Método", fontBold)));
                    table.addCell(new PdfPCell(new Phrase("Monto", fontBold)));
                    table.addCell(new PdfPCell(new Phrase("Transacción ID", fontBold)));

                    Map<Integer, Reserva> reservasCache = new HashMap<>();
                    Map<Integer, Propiedad> propiedadesCache = new HashMap<>();

                    for (Pago pago : historialPagos) {
                        Reserva reserva = null;
                        try {
                            reserva = reservaService.buscarReservaPorId(pago.getReservaId()); // Esto puede lanzar ServiceException
                        } catch (ServiceException e) {
                            LOGGER.log(Level.WARNING, "No se pudo encontrar reserva " + pago.getReservaId() + " para pago " + pago.getId() + " en PDF.", e);
                        }
                        
                        Propiedad propiedad = null;
                        if (reserva != null) {
                            try {
                                propiedad = propiedadDAO.buscarPorId((long)reserva.getIdPropiedad()).orElse(null); // Esto puede lanzar DAOException
                            } catch (DAOException e) {
                                LOGGER.log(Level.WARNING, "No se pudo encontrar propiedad " + reserva.getIdPropiedad() + " para reserva " + reserva.getId() + " en PDF.", e);
                            }
                        }

                        table.addCell(new Phrase(pago.getFechaPago() != null ? pago.getFechaPago().format(dateTimeFormatter) : "N/A", fontNormal));
                        table.addCell(new Phrase(propiedad != null ? propiedad.getTitulo() : "N/A", fontNormal));
                        table.addCell(new Phrase(String.valueOf(pago.getReservaId()), fontNormal));
                        table.addCell(new Phrase(pago.getMetodo(), fontNormal));
                        table.addCell(new Phrase("S/" + String.format("%.2f", pago.getMonto()), fontNormal));
                        table.addCell(new Phrase(pago.getTransaccionId(), fontNormal));
                    }
                    document.add(table);
                }

            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Tipo de descarga de PDF no válido.");
            }

            document.close();

        } catch (DocumentException | ServiceException | NumberFormatException | DAOException e) { // AÑADIDO: DAOException
            LOGGER.log(Level.SEVERE, "Error al generar el PDF para el usuario " + userId, e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al generar el PDF: " + e.getMessage());
        }
    }
}