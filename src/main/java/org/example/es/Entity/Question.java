package org.example.es.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Question {

    @Id
    @GeneratedValue
    private Long id;

    private String title;
    private String content;
    private OffsetDateTime createdAt;
    public Question(String title, String content) {
        this.title = title;
        this.content = content;
        this.createdAt = OffsetDateTime.now();
    }


}
