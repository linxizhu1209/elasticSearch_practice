package org.example.es.Service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.NestedQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.common.recycler.Recycler;
import org.example.es.Data.Comment;
import org.example.es.Data.QuestionDocument;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class QuestionDualIndexService {

    private final ElasticsearchClient elasticsearchClient;

    public void saveSample() throws IOException {
        List<Comment> comments = List.of(
                new Comment("Kim", "감사합니다", LocalDateTime.now()),
                new Comment("Lee", "좋은 질문입니다", LocalDateTime.now())
        );

        QuestionDocument doc = new QuestionDocument();
        doc.setTitle("엔비디아 고점은요?");
        doc.setContent("엔비디아 주식은 앞으로 어떻게 될까요?");
        doc.setCreatedAt(LocalDateTime.now().toString());
        doc.setComments(comments);
        doc.setCommentsFlat(comments);

        // ✅ nested 인덱스에 저장
        IndexRequest<QuestionDocument> nestedRequest = IndexRequest.of(i -> i
                .index("questions_nested")
                .document(doc));
        elasticsearchClient.index(nestedRequest);

        // ✅ object 인덱스에도 저장
        IndexRequest<QuestionDocument> objectRequest = IndexRequest.of(i -> i
                .index("questions_object")
                .document(doc));
        elasticsearchClient.index(objectRequest);

    }


    public List<QuestionDocument> searchNested(String author, String text) throws IOException {
        Query authorMatch = MatchQuery.of(m -> m.field("comments.author").query(author))._toQuery();
        Query textMatch = MatchQuery.of(m -> m.field("comments.text").query(text))._toQuery();

        Query nestedQuery = NestedQuery.of(n -> n
                .path("comments")
                .query(q -> q.bool(b -> b.must(List.of(authorMatch, textMatch)))))._toQuery();

        SearchRequest request = SearchRequest.of(s -> s
                .index("questions_nested")
                .query(nestedQuery)
                .size(10));

        SearchResponse<QuestionDocument> response = elasticsearchClient.search(request, QuestionDocument.class);
        return response.hits().hits().stream().map(Hit::source).toList();
    }

    public List<QuestionDocument> searchObject(String author, String text) throws IOException {
        Query authorMatch = MatchQuery.of(m -> m.field("comments.author").query(author))._toQuery();
        Query textMatch = MatchQuery.of(m -> m.field("comments.text").query(text))._toQuery();

        Query query = BoolQuery.of(b -> b
                .must(List.of(authorMatch, textMatch))
        )._toQuery();

        SearchRequest request = SearchRequest.of(s -> s
                .index("questions_object")  // object 타입으로 색인된 인덱스 이름
                .query(query)
                .size(10)
        );

        SearchResponse<QuestionDocument> response =
                elasticsearchClient.search(request, QuestionDocument.class);

        return response.hits().hits().stream()
                .map(Hit::source)
                .toList();
    }

}
