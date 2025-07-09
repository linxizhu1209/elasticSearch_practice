package org.example.es.Repository;

import org.example.es.Data.QuestionDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface QuestionDocumentRepository extends ElasticsearchRepository<QuestionDocument, String>, CustomQuestionSearchRepository {

}
