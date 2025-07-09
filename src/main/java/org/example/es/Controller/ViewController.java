package org.example.es.Controller;

import jdk.jfr.Category;
import lombok.RequiredArgsConstructor;
import org.example.es.Entity.QuestionCreateRequest;
import org.example.es.Service.QuestionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class ViewController {

    private final QuestionService questionService;

    @GetMapping("/questions/form")
    public String questionForm(Model model) {
        model.addAttribute("question", new QuestionCreateRequest());
        return "question_form";
    }

    @PostMapping("/questions")
    public String createQuestion(@ModelAttribute QuestionCreateRequest request) {
        questionService.createQuestion(request.getTitle(), request.getContent());
        return "redirect:/search/form";
    }

}
