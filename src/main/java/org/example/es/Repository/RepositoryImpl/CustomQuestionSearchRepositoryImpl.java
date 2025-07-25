package org.example.es.Repository.RepositoryImpl;

import lombok.RequiredArgsConstructor;
import org.example.es.Data.QuestionDocument;
import org.example.es.Repository.CustomQuestionSearchRepository;
import org.example.es.dto.HighlightedResult;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import java.util.List;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.highlight.Highlight;

import static co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders.match;


@RequiredArgsConstructor
public class CustomQuestionSearchRepositoryImpl implements CustomQuestionSearchRepository {

    private final ElasticsearchOperations elasticsearchOperations;

    @Override
    public List<QuestionDocument> searchByTitle(String keyword) {

        Query query = match(m -> m.field("title").query(keyword));
        NativeQuery nativeQuery = NativeQuery.builder().withQuery(query).withPageable(PageRequest.of(0, 10000)).build();


        SearchHits<QuestionDocument> searchHits = elasticsearchOperations.search(nativeQuery, QuestionDocument.class);

        return searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .toList();
    }




}
