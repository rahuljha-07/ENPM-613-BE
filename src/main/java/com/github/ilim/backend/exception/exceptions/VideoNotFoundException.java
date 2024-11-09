package com.github.ilim.backend.exception.exceptions;

import java.util.UUID;

public class VideoNotFoundException extends RuntimeException {
    public VideoNotFoundException(UUID videoId) {
        super("Video(%s) is not found".formatted(videoId));
    }

}
