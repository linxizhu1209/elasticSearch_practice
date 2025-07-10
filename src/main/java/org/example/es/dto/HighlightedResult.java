package org.example.es.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

public record HighlightedResult (String highlightedTitle, String highlightedContent){
}
