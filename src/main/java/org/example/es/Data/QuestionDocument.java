package org.example.es.Data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QuestionDocument {

    @Id
    private Long id;
    private String title;
    private String content;
    private String createdAt;

    @JsonProperty("titleSuggest")
    private List<String> titleSuggest; // 자동완성용 필드
    private List<Comment> comments;
    private List<Comment> commentsFlat; // object 타입 실험

    public void addComment(Comment comment) {
        if(comments == null){
            comments = new ArrayList<>();
        }
        comments.add(comment);
    }
}
