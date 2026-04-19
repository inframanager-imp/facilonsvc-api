package com.facilon.app.module.client.service.pdf;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;

/**
 * Loads the NRI form field mapping JSON from classpath once at startup.
 * Fails fast if the resource is missing or malformed — the expander cannot function without it.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KycFormMappingLoader {

    private static final String MAPPING_PATH = "templates/pdf/kyc-form/nri/kyc-form-mapping-nri.json";

    private final ObjectMapper objectMapper;

    @Getter
    private KycFormMapping mapping;

    @PostConstruct
    void load() {
        try {
            ClassPathResource resource = new ClassPathResource(MAPPING_PATH);
            try (InputStream is = resource.getInputStream()) {
                mapping = objectMapper.readValue(is, KycFormMapping.class);
            }
            log.info("Loaded KYC form mapping: {} direct, {} multiDirect, {} dateSplits, {} charSplits, {} constants",
                    mapping.getDirect().size(),
                    mapping.getMultiDirect().size(),
                    mapping.getDateSplits().size(),
                    mapping.getCharSplits().size(),
                    mapping.getConstants().size());
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load KYC mapping from classpath: " + MAPPING_PATH, e);
        }
    }
}
