package edu.bbte.replate.shared.dto.outgoing;

import org.springframework.core.io.Resource;

public record ImageDownloadDto(
        Resource resource,
        String fileName,
        String mimeType
) {
}
