package org.example.es.dto;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

public record HighlightedResult (
        Long id,
        String highlightedTitle,
        String highlightedContent,
        LocalDateTime createdAt,
        List<String> matchedComments,
        List<CommentDto> comments
        ){
}
