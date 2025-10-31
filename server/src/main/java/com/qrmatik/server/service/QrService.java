package com.qrmatik.server.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.qrmatik.server.model.TableEntity;
import com.qrmatik.server.repository.TableRepository;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class QrService {

    private final TableRepository tableRepository;

    @Value("${app.base-url:http://localhost:5173}")
    private String baseUrl;

    public QrService(TableRepository tableRepository) {
        this.tableRepository = tableRepository;
    }

    public byte[] generateQrPdfForTenant(String tenantCode) throws IOException {
        List<TableEntity> tables = tableRepository.findAll();
        // filter by tenantCode if provided
        if (tenantCode != null && !tenantCode.isBlank()) {
            tables.removeIf(t -> t.getTenant() == null || t.getTenant().getCode() == null
                    || !tenantCode.equals(t.getTenant().getCode()));
        }

        try (PDDocument doc = new PDDocument()) {
            for (TableEntity table : tables) {
                PDPage page = new PDPage(PDRectangle.LETTER);
                doc.addPage(page);

                String url = buildTableUrl(tenantCode, table.getCode());
                BufferedImage qr = generateQrImage(url, 300, 300);

                PDImageXObject pdImage = LosslessFactory.createFromImage(doc, qr);

                try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                    // center image
                    float pageW = page.getMediaBox().getWidth();
                    float pageH = page.getMediaBox().getHeight();
                    float imgW = pdImage.getWidth();
                    float imgH = pdImage.getHeight();
                    float x = (pageW - imgW) / 2f;
                    float y = (pageH - imgH) / 2f + 50;
                    cs.drawImage(pdImage, x, y, imgW, imgH);

                    // draw table label below
                    cs.beginText();
                    cs.newLineAtOffset(pageW / 2f - 50, y - 30);
                    cs.setFont(PDType1Font.HELVETICA_BOLD, 16);
                    cs.showText("Table: " + Optional.ofNullable(table.getCode()).orElse(""));
                    cs.endText();
                }
            }

            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                doc.save(baos);
                return baos.toByteArray();
            }
        }
    }

    private String buildTableUrl(String tenantCode, String tableCode) {
        // Build subdomain-based URL: https://{tenant}.{domain}/menu?table={code}
        // Dev: baseUrl like http://localhost:5173 => use {tenant}.localhost:5173
        // Prod: baseUrl like https://app.example.com => use {tenant}.example.com (strip common app/www prefix)
        try {
            java.net.URL u = new java.net.URL(baseUrl);
            String protocol = u.getProtocol();
            String host = u.getHost();
            int port = u.getPort(); // -1 if not set

            String effectiveHost = host;
            if (tenantCode != null && !tenantCode.isBlank()) {
                String tc = tenantCode.trim();
                if (host.equalsIgnoreCase("localhost") || host.endsWith(".localhost")) {
                    // local dev: {tenant}.localhost[:port]
                    effectiveHost = tc + ".localhost";
                } else {
                    // production: prefix tenant; if host starts with app./www. remove that prefix
                    String domain = host;
                    if (domain.startsWith("app.")) domain = domain.substring(4);
                    else if (domain.startsWith("www.")) domain = domain.substring(4);
                    effectiveHost = tc + "." + domain;
                }
            }

            String origin = protocol + "://" + effectiveHost + (port > -1 ? ":" + port : "");
            return origin + "/menu?table=" + tableCode;
        } catch (Exception e) {
            // fallback to previous behavior
            String tenantSegment = tenantCode == null || tenantCode.isBlank() ? "" : "/r/" + tenantCode;
            return baseUrl + tenantSegment + "/menu?table=" + tableCode;
        }
    }

    private BufferedImage generateQrImage(String text, int width, int height) throws IOException {
        QRCodeWriter writer = new QRCodeWriter();
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.MARGIN, 1);
        BitMatrix bitMatrix;
        try {
            bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, width, height, hints);
        } catch (WriterException e) {
            throw new IOException("Failed to generate QR code", e);
        }
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }

    public byte[] generateQrPng(String text, int size) throws IOException {
        int s = (size <= 0 ? 300 : size);
        BufferedImage img = generateQrImage(text, s, s);
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(img, "png", baos);
            return baos.toByteArray();
        }
    }
}
