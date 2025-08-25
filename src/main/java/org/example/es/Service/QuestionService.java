package org.example.es.Service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.GetRequest;
import co.elastic.clients.elasticsearch.core.GetResponse;
import lombok.RequiredArgsConstructor;
import org.example.es.Data.Comment;
import org.example.es.Data.QuestionDocument;
import org.example.es.Entity.Question;
import org.example.es.Repository.QuestionRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final ElasticsearchClient elasticsearchClient;

    public Long createQuestion(String title, String content) throws IOException {
        if(StringUtils.isEmpty(title) || StringUtils.isEmpty(content)){
            throw new IllegalArgumentException("Title and content cannot be empty");
        }
        Question q = new Question(title,content);
//        questionRepository.save(q);

        String createdAtStr = q.getCreatedAt().format(DateTimeFormatter.ISO_DATE_TIME);

        QuestionDocument doc = QuestionDocument.builder()
                .id(q.getId())
                .title(title)
                .content(content)
                .createdAt(createdAtStr)
                .titleSuggest(List.of(title))
                .build();

        elasticsearchClient.index(i -> i
                .index("questions")
                .id(String.valueOf(q.getId()))
                .document(doc));

        return q.getId();
    }

    public void addCommentToQuestion(String id, String author, String text) throws IOException {
        GetRequest request = GetRequest.of(g -> g
                .index("questions")
                .id(id));

        GetResponse<QuestionDocument> response = elasticsearchClient.get(request, QuestionDocument.class);

        if(!response.found()){
            throw new IllegalArgumentException("Question not found");
        }

        QuestionDocument doc = response.source();
        doc.addComment(new Comment(author,text, LocalDateTime.now()));

        elasticsearchClient.index(i -> i
                .index("questions")
                .id(id)
                .document(doc)
        );
    }
}
