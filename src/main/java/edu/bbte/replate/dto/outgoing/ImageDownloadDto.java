package edu.bbte.replate.dto.outgoing;

import org.springframework.core.io.Resource;

public record ImageDownloadDto(
        Resource resource,
        String fileName,
        String mimeType
) {
}
