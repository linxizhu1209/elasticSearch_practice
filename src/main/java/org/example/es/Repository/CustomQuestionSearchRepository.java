package org.example.es.Repository;

import org.example.es.Data.QuestionDocument;
import org.example.es.dto.HighlightedResult;

import java.util.List;

public interface CustomQuestionSearchRepository {
    List<QuestionDocument> searchByTitle(String keyword);


}
