package org.example.es.utils;

import lombok.RequiredArgsConstructor;
import org.example.es.Data.QuestionDocument;
import org.example.es.Entity.Question;
import org.example.es.Repository.QuestionDocumentRepository;
import org.example.es.Repository.QuestionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class QuestionDummyLoader implements CommandLineRunner {

    private final QuestionRepository questionRepository;
    private final QuestionDocumentRepository questionDocumentRepository;

    @Override
    public void run(String... args) throws Exception {
        if (questionRepository.count() > 0) return;
        List<Question> questions = new ArrayList<>();
        for (int i=1;i<=10000;i++){
            String title = "질문 입니다 " + i;
            String content = "내용 입니다!! - " + i;
            Question q = new Question(title, content);
            questions.add(q);
        }
        List<Question> saved = questionRepository.saveAll(questions);
        questionRepository.flush(); // 반
        OffsetDateTime createdAt = OffsetDateTime.now();
        List<QuestionDocument> docs = saved.stream().map(q -> new QuestionDocument(q.getId(), q.getTitle(), q.getContent(),createdAt))
                .toList();
        questionDocumentRepository.saveAll(docs);

        System.out.println("1만 개 데이터 삽입 완료!");
    }
}
