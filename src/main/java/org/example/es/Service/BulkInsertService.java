package org.example.es.Service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import co.elastic.clients.elasticsearch.core.bulk.BulkResponseItem;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.es.Data.QuestionDocument;
import org.example.es.Entity.Question;
import org.example.es.Repository.QuestionRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BulkInsertService {

    private final QuestionService questionService;
    private final QuestionRepository questionRepository;
    private final ElasticsearchClient elasticsearchClient;
    private final RedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    private String toJson(Object obj){
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e){
            throw new RuntimeException("직렬화 실패 " , e);
        }
    }
    public void insertManyQuestions(int count) throws IOException {
        for (int j = 0; j < count; j++) {
            String title = "엘라스틱서치 테스트 제목 - " + j;
            String content = "테스트 내용입니다. - "+j;
//            questionService.createQuestion(title,content);

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
        }
    }


    public void insertManyBulk(int count) throws IOException {
        List<BulkOperation> operations = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            Question q = new Question("벌크 질문 제목 " + i, "벌크 질문 내용 " + i);
//            questionRepository.save(q);

            QuestionDocument doc = QuestionDocument.builder()
                    .id(q.getId())
                    .title(q.getTitle())
                    .content(q.getContent())
                    .createdAt(q.getCreatedAt().format(DateTimeFormatter.ISO_DATE_TIME))
                    .titleSuggest(List.of(q.getTitle()))
                    .build();

            operations.add(BulkOperation.of(op -> op
                    .index(idx -> idx.index("questions").id(String.valueOf(q.getId())).document(doc))
            ));
        }

        BulkRequest request = BulkRequest.of(b -> b.operations(operations));
        elasticsearchClient.bulk(request);
    }


    public void insertManyBulkWithFailures(int count) throws IOException {
        List<BulkOperation> operations = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            Question q = new Question("제목 " + i, "내용 " + i);

            // 일부러 실패 유도: 제목이 null이면 Elasticsearch에서 저장 실패함
            String title = (i % 100 == 0) ? null : q.getTitle();

            QuestionDocument doc = QuestionDocument.builder()
                    .id(q.getId())
                    .title(title)
                    .content(q.getContent())
                    .createdAt(q.getCreatedAt().format(DateTimeFormatter.ISO_DATE_TIME))
                    .titleSuggest(List.of(q.getTitle()))
                    .build();

            operations.add(BulkOperation.of(op -> op
                    .index(idx -> idx
                            .index("questions")
                            .id(String.valueOf(q.getId()))
                            .document(doc)
                    )
            ));
        }

        BulkRequest request = BulkRequest.of(b -> b.operations(operations));
        BulkResponse response = elasticsearchClient.bulk(request);

        for (BulkResponseItem item : response.items()) {
            if (item.error() != null) {
                // 실패한 요청을 Redis 큐에 추가
                B
                redisTemplate.opsForList().leftPush("retry:es-bulk", toJson(item.operation()));
                System.err.println("❌ 실패한 문서 ID: " + item.id());
            }
        }
    }


}
