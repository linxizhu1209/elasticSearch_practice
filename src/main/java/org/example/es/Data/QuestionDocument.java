package org.example.es.Data;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "questions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionDocument {

    @Id
    private Long id;

    private String title;
    private String content;

}
