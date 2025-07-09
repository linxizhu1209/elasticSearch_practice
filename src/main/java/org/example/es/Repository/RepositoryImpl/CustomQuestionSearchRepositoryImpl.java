package org.example.es.Repository.RepositoryImpl;

import lombok.RequiredArgsConstructor;
import org.example.es.Data.QuestionDocument;
import org.example.es.Repository.CustomQuestionSearchRepository;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import java.util.List;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import static co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders.match;


@RequiredArgsConstructor
public class CustomQuestionSearchRepositoryImpl implements CustomQuestionSearchRepository {

    private final ElasticsearchOperations elasticsearchOperations;

    @Override
    public List<QuestionDocument> searchByTitle(String keyword) {

        Query query = match(m -> m.field("title").query(keyword));
        NativeQuery nativeQuery = NativeQuery.builder().withQuery(query).build();


        SearchHits<QuestionDocument> searchHits = elasticsearchOperations.search(nativeQuery, QuestionDocument.class);

        return searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .toList();
    }
}
