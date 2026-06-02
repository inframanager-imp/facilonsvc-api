package com.facilon.app.module.dsr.dto;

import org.springframework.core.io.Resource;

/** A downloadable evidence file streamed through the authenticated API (never a raw path). */
public record DsrFilePayload(Resource resource, String filename, String contentType) {
}
