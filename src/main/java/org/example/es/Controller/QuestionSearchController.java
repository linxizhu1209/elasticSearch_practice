package org.example.es.Controller;

import lombok.RequiredArgsConstructor;
import org.example.es.Data.QuestionDocument;
import org.example.es.Entity.Question;
import org.example.es.Repository.QuestionDocumentRepository;
import org.example.es.Repository.QuestionRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/search")
public class QuestionSearchController {

    private final QuestionDocumentRepository questionDocumentRepository;
    private final QuestionRepository questionRepository;

    @GetMapping("/es")
    public Iterable<QuestionDocument> search(@RequestParam String keyword){
        return questionDocumentRepository.searchByTitle(keyword);
    }

    @GetMapping("/db")
    public List<Question> searchByDb(@RequestParam String keyword){
        return questionRepository.findByTitleContaining(keyword);
    }


}
