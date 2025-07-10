package org.example.es.Service;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import org.springframework.data.elasticsearch.core.query.highlight.Highlight;
import lombok.RequiredArgsConstructor;
import org.example.es.Data.QuestionDocument;
import org.example.es.dto.HighlightedResult;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightField;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QuestionSearchService {

    private final ElasticsearchOperations elasticsearchOperations;
    private final ElasticsearchTemplate elasticsearchTemplate;


//    public List<HighlightedResult> searchWithHighlight(String keyword) {
//        Query query = match(m -> m.field("title").query(keyword));
//
//        HighlightField highlightField = new HighlightField("title");
//        Highlight highlight = new Highlight(List.of(highlightField));
//        HighlightQuery highlightQuery = new HighlightQuery(highlight, QuestionDocument.class);
//
//        NativeQuery nativeQuery = NativeQuery.builder().withQuery(query).withHighlightQuery(highlightQuery).build();
//
//        SearchHits<QuestionDocument> searchHits = elasticsearchOperations.search(nativeQuery, QuestionDocument.class);
//
//        return searchHits.getSearchHits().stream().map(hit -> {
//            String highlightedTitle = hit.getHighlightField("title").get(0);
//            return new HighlightedResult(highlightedTitle, hit.getContent().getContent());
//        }).toList();
//
//    }


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
        return new HighlightedResult(highlightedTitle, highlightedContent);
    }).toList();


    }

}
