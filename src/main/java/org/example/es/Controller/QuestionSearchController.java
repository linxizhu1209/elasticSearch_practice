package org.example.es.Controller;

import lombok.RequiredArgsConstructor;
import org.example.es.Data.QuestionDocument;
import org.example.es.Repository.QuestionDocumentRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/questions")
public class QuestionSearchController {

    private final QuestionDocumentRepository questionDocumentRepository;

    @GetMapping("/search")
    public Iterable<QuestionDocument> search(@RequestParam String keyword){
        return questionDocumentRepository.searchByTitle(keyword);
    }
}
