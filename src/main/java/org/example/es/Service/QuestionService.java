package org.example.es.Service;

import lombok.RequiredArgsConstructor;
import org.example.es.Data.QuestionDocument;
import org.example.es.Entity.Question;
import org.example.es.Repository.QuestionDocumentRepository;
import org.example.es.Repository.QuestionRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final QuestionDocumentRepository questionDocumentRepository;

    public Long createQuestion(String title, String content){
        if(StringUtils.isEmpty(title) || StringUtils.isEmpty(content)){
            throw new IllegalArgumentException("Title and content cannot be empty");
        }
        Question q = new Question(title,content);
        questionRepository.save(q);

        QuestionDocument doc = QuestionDocument.builder()
                .id(q.getId())
                .title(title)
                .content(content)
                .createdAt(q.getCreatedAt())
                .build();
        questionDocumentRepository.save(doc);

        return q.getId();
    }

}
