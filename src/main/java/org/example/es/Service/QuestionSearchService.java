package org.example.es.Service;


import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import lombok.RequiredArgsConstructor;
import org.example.es.Data.QuestionDocument;
import org.example.es.dto.HighlightedResult;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.highlight.Highlight;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightField;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionSearchService {

    private final ElasticsearchOperations elasticsearchOperations;
    private final ElasticsearchTemplate elasticsearchTemplate;


    public List<HighlightedResult> searchWithHighlight(String keyword) {

    Query query = QueryBuilders.bool()
            .should(QueryBuilders.match(m -> m.field("title").query(keyword)))
            .should(QueryBuilders.match(m -> m.field("content").query(keyword)))
            .build()._toQuery();


        HighlightField highlightTitle = new HighlightField("title");
        HighlightField highlightContent = new HighlightField("content");
        Highlight highlight = new Highlight(List.of(highlightTitle, highlightContent));

        HighlightQuery highlightQuery = new HighlightQuery(highlight, QuestionDocument.class);

    NativeQuery nativeQuery = NativeQuery.builder()
            .withQuery(query)
            .withHighlightQuery(highlightQuery)
            .build();

    SearchHits<QuestionDocument> searchHits = elasticsearchOperations.search(nativeQuery, QuestionDocument.class);

    return searchHits.getSearchHits().stream().map(hit -> {
        List<String> highlightedTitleList = hit.getHighlightField("title");
        List<String> highlightedContentList = hit.getHighlightField("content");

        String highlightedTitle = (highlightedTitleList != null && !highlightedTitleList.isEmpty())
                ? highlightedTitleList.get(0)
                : hit.getContent().getTitle();

        String highlightedContent = (highlightedContentList != null && !highlightedContentList.isEmpty())
                ? highlightedContentList.get(0)
                : hit.getContent().getContent();
        OffsetDateTime createAt = hit.getContent().getCreatedAt();
        return new HighlightedResult(highlightedTitle, highlightedContent,createAt);
    }).toList();
    }

    public List<HighlightedResult> searchWithHighlightAndSort(String keyword, SortOrder sortOrder) {

        Query query = QueryBuilders.bool()
                .should(QueryBuilders.match(m -> m.field("title").query(keyword)))
                .should(QueryBuilders.match(m -> m.field("content").query(keyword)))
                .build()._toQuery();

        HighlightField highlightTitle = new HighlightField("title");
        HighlightField highlightContent = new HighlightField("content");
        Highlight highlight = new Highlight(List.of(highlightTitle, highlightContent));

        HighlightQuery highlightQuery = new HighlightQuery(highlight, QuestionDocument.class);


        SortOptions sortOptions = new SortOptions.Builder().field(f -> f.field("createdAt").order(sortOrder)).build();

        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(query)
                .withHighlightQuery(highlightQuery)
                .withSort(List.of(sortOptions))
                .build();

        SearchHits<QuestionDocument> searchHits = elasticsearchOperations.search(nativeQuery, QuestionDocument.class);

        return searchHits.getSearchHits().stream().map(hit -> {
            List<String> highlightedTitleList = hit.getHighlightField("title");
            List<String> highlightedContentList = hit.getHighlightField("content");

            String highlightedTitle = (highlightedTitleList != null && !highlightedTitleList.isEmpty())
                    ? highlightedTitleList.get(0)
                    : hit.getContent().getTitle();

            String highlightedContent = (highlightedContentList != null && !highlightedContentList.isEmpty())
                    ? highlightedContentList.get(0)
                    : hit.getContent().getContent();

            OffsetDateTime createAt = hit.getContent().getCreatedAt();
            return new HighlightedResult(highlightedTitle, highlightedContent, createAt);
        }).toList();


    }


}
