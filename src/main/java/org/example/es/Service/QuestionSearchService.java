package org.example.es.Service;


import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.NestedQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Highlight;
import co.elastic.clients.elasticsearch.core.search.HighlightField;
import lombok.RequiredArgsConstructor;
import org.example.es.Data.QuestionDocument;
import org.example.es.dto.HighlightedResult;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class QuestionSearchService {

    private final ElasticsearchClient elasticsearchClient;

//    public List<HighlightedResult> searchWithHighlightAndSort(String keyword, SortOrder sortOrder, int page, int size) throws IOException {
//
//        Query query = QueryBuilders.bool()
//                .should(QueryBuilders.match(m -> m.field("title").query(keyword)))
//                .should(QueryBuilders.match(m -> m.field("content").query(keyword)))
//                .build()._toQuery();
//
//        Highlight highlight = Highlight.of(h -> h
//                .fields("title", HighlightField.of(f -> f))
//                .fields("content", HighlightField.of(f -> f)));
//
//
//        SortOptions sortOptions = new SortOptions.Builder().field(f -> f.field("createdAt").order(sortOrder)).build();
//
//        SearchRequest request = SearchRequest.of(sr -> sr
//                .index("questions")
//                .query(query)
//                .highlight(highlight)
//                .sort(sortOptions)
//                .from(page * size)
//                .size(size)
//        );
//
//        SearchResponse<QuestionDocument> response = elasticsearchClient.search(request, QuestionDocument.class);
//
//        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
//
//        return response.hits().hits().stream().map(hit -> {
//            QuestionDocument doc = hit.source();
//
//            String highlightedTitle = Optional.ofNullable(hit.highlight().get("title"))
//                    .flatMap(list -> list.stream().findFirst())
//                    .orElse(doc.getTitle());
//
//            String highlightedContent = Optional.ofNullable(hit.highlight().get("content"))
//                    .flatMap(list -> list.stream().findFirst())
//                    .orElse(doc.getContent());
//
//            LocalDateTime createdAt = LocalDateTime.parse(doc.getCreatedAt(), formatter);
//            return new HighlightedResult(doc.getId(),highlightedTitle, highlightedContent, createdAt);
//        }).toList();
//    }

    public List<String> autocompleteTitle(String prefix) throws IOException {
        SearchRequest request = SearchRequest.of(sr -> sr.index("questions").suggest(s -> s.suggesters("title-suggest", sg -> sg.completion(
                cs-> cs.field("titleSuggest").size(10)
        ).prefix(prefix))
        )
        );

        SearchResponse<Void> response = elasticsearchClient.search(request, Void.class);

        return Optional.ofNullable(response.suggest())
                .map(s -> s.get("title-suggest"))
                .orElse(List.of())
                .stream()
                .flatMap(entry -> entry.completion() != null? entry.completion().options().stream() : Stream.empty())
                .map(option -> option.text()).toList();
    }


    public List<HighlightedResult> search(String keyword, String target, String sortOrder, int page, int size) throws IOException {
        int from = page * size;

        Query query;
        if("title".equals(target)){
            query = MatchQuery.of(m -> m.field("title").query(keyword))._toQuery();
        } else if("content".equals(target)){
            query = MatchQuery.of(m -> m.field("content").query(keyword))._toQuery();
        } else if("comments".equals(target)){
            query = NestedQuery.of(n -> n.path("comments").query(q -> q.match(m -> m.field("comments.text").query(keyword))))._toQuery();
        } else {
            throw new IllegalArgumentException("지원하지 않는 검색 대상: "+target);
        }

        SortOptions sort = SortOptions.of(s -> s.field(f->f.field("createdAt").order("Asc".equalsIgnoreCase(sortOrder) ? SortOrder.Asc : SortOrder.Desc)));

        Highlight highlight = Highlight.of(h -> h
                .fields("title", f -> f)
                .fields("content", f -> f)
                .fields("comments.text", f -> f)
        );

        SearchRequest request = SearchRequest.of(sr -> sr
                .index("questions")
                .query(query)
                .highlight(highlight)
                .sort(sort)
                .from(from)
                .size(size)
        );

        SearchResponse<QuestionDocument> response = elasticsearchClient.search(request, QuestionDocument.class);

        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

        return response.hits().hits().stream().map(hit -> {
            QuestionDocument doc = hit.source();
            String highlightedTitle = Optional.ofNullable(hit.highlight().get("title"))
                    .flatMap(list -> list.stream().findFirst())
                    .orElse(doc.getTitle());

            String highlightedContent = Optional.ofNullable(hit.highlight().get("content"))
                    .flatMap(list -> list.stream().findFirst())
                    .orElse(doc.getContent());

            List<String> highlightedComments = Optional.ofNullable(hit.highlight().get("comments.text"))
                    .map(list -> list.stream().limit(2).toList()).orElse(List.of());

            LocalDateTime createdAt = LocalDateTime.parse(doc.getCreatedAt(), formatter);

            return new HighlightedResult(doc.getId(),highlightedTitle, highlightedContent, createdAt, highlightedComments);
        }).toList();
    }
}
