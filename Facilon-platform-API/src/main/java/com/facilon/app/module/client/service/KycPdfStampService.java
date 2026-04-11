package com.facilon.app.module.client.service;

import com.facilon.app.module.client.dto.KycFormDataDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Fills the KYC PDF form using AcroForm fields.
 *
 * The template PDF ({@code pdf/kyc-form-fillable.pdf}) contains named
 * AcroForm text fields (e.g. "firstName", "panNumber") created by the
 * one-time Python script {@code create-fillable-pdf.py}. This service
 * simply fills each field by name using PDFBox's {@link PDAcroForm} API.
 *
 * This is the industry-standard approach for PDF form filling:
 * - No coordinate guessing
 * - Fields are filled by name: {@code form.getField("firstName").setValue("Samanta")}
 * - Original layout is pixel-perfect (we use the original PDF as the template)
 * - Easy to maintain: add/move fields by editing the fillable PDF template
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class KycPdfStampService {

    private static final String FILLABLE_PDF = "pdf/kyc-form-fillable.pdf";

    /**
     * Fill the KYC AcroForm PDF with investor data.
     *
     * @param dto flat form data from {@link KycPdfService#buildFormData}
     * @return filled PDF as byte array
     */
    public byte[] stampPdf(KycFormDataDto dto) {
        try {
            // Convert DTO to flat map
            Map<String, String> dataMap = dtoToMap(dto);

            // Load the fillable template
            ClassPathResource resource = new ClassPathResource(FILLABLE_PDF);
            byte[] templateBytes;
            try (InputStream is = resource.getInputStream()) {
                templateBytes = is.readAllBytes();
            }

            try (PDDocument document = Loader.loadPDF(templateBytes)) {
                PDAcroForm acroForm = document.getDocumentCatalog().getAcroForm();

                if (acroForm == null) {
                    log.error("No AcroForm found in {}! Run create-fillable-pdf.py first.", FILLABLE_PDF);
                    throw new RuntimeException("KYC fillable PDF has no AcroForm fields");
                }

                // Need appearances so PDF viewers render the field values
                acroForm.setNeedAppearances(true);

                int filled = 0;
                int skipped = 0;

                for (PDField field : acroForm.getFields()) {
                    String fieldName = field.getFullyQualifiedName();
                    String value = dataMap.get(fieldName);

                    if (value != null && !value.isBlank()) {
                        try {
                            field.setValue(value.trim());
                            filled++;
                        } catch (Exception e) {
                            log.warn("Failed to set field '{}': {}", fieldName, e.getMessage());
                            skipped++;
                        }
                    } else {
                        skipped++;
                    }
                }

                // Optionally flatten (makes fields non-editable, looks like printed text)
                // acroForm.flatten();

                log.info("KYC AcroForm filled: {} fields set, {} skipped (empty/error), total {}",
                        filled, skipped, acroForm.getFields().size());

                // Save
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                document.save(out);
                return out.toByteArray();
            }
        } catch (Exception e) {
            log.error("Failed to fill KYC AcroForm PDF: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to fill KYC PDF: " + e.getMessage(), e);
        }
    }

    /**
     * Convert the DTO to a flat String map using reflection.
     */
    private Map<String, String> dtoToMap(KycFormDataDto dto) {
        Map<String, String> map = new HashMap<>();
        for (Field f : KycFormDataDto.class.getDeclaredFields()) {
            f.setAccessible(true);
            try {
                Object val = f.get(dto);
                if (val != null && val instanceof String s && !s.isBlank()) {
                    map.put(f.getName(), s);
                } else if (val != null && !(val instanceof java.util.List) && !(val instanceof Map)) {
                    map.put(f.getName(), String.valueOf(val));
                }
            } catch (IllegalAccessException ignored) {
            }
        }
        return map;
    }
}
