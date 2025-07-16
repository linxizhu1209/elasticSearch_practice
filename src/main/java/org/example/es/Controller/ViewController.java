package org.example.es.Controller;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.GetRequest;
import co.elastic.clients.elasticsearch.core.GetResponse;
import jdk.jfr.Category;
import lombok.RequiredArgsConstructor;
import org.example.es.Data.CommentCreateRequest;
import org.example.es.Data.QuestionDocument;
import org.example.es.Entity.QuestionCreateRequest;
import org.example.es.Service.QuestionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
public class ViewController {

    private final QuestionService questionService;
    private final ElasticsearchClient elasticsearchClient;

    @GetMapping("/questions/form")
    public String questionForm(Model model) {
        model.addAttribute("question", new QuestionCreateRequest());
        return "question_form";
    }

    @PostMapping("/questions")
    public String createQuestion(@ModelAttribute QuestionCreateRequest request) throws IOException {
        questionService.createQuestion(request.getTitle(), request.getContent());
        return "redirect:/search/form";
    }

    @GetMapping("/questions/{id}")
    public String getQuestionDetail(@PathVariable String id, Model model) throws IOException {
        GetRequest request = new GetRequest.Builder().index("questions").id(id).build();

        GetResponse<QuestionDocument> response = elasticsearchClient.get(request, QuestionDocument.class);

        if(!response.found()){
            throw new RuntimeException("Question not found");
        }

        model.addAttribute("question", response.source());
        return "question_detail";
    }

    @PostMapping("/questions/{id}/comments")
    public String addComment(@PathVariable String id, @ModelAttribute CommentCreateRequest request) throws IOException {
        questionService.addCommentToQuestion(id, request.getAuthor(), request.getText());
        return "redirect:/questions/" + id;
    }


}
