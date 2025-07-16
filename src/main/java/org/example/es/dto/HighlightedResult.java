package org.example.es.dto;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

public record HighlightedResult (String highlightedTitle, String highlightedContent, LocalDateTime createdAt){
}
