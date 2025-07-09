package org.example.es.Controller;

import lombok.RequiredArgsConstructor;
import org.example.es.Entity.QuestionCreateRequest;
import org.example.es.Service.QuestionService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
//@RequestMapping("/questions")
public class QuestionController {

    private final QuestionService questionService;

//    @PostMapping
//    public Long create(@RequestBody QuestionCreateRequest request){
//        return questionService.createQuestion(request.getTitle(), request.getContent());
//    }
}
