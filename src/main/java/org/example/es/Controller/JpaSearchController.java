package org.example.es.Controller;

import lombok.RequiredArgsConstructor;
import org.example.es.Entity.Question;
import org.example.es.Service.QuestionJpaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/jpa-search")
@RequiredArgsConstructor
public class JpaSearchController {

    private final QuestionJpaService questionJpaService;

    @GetMapping("/form")
    public String searchForm() {
        return "jpa_search_form.html";
    }

    @GetMapping("/results")
    public String searchResults(@RequestParam String keyword, Model model) {
        long start = System.currentTimeMillis();
        List<Question> results = questionJpaService.searchByTitle(keyword);
        long end = System.currentTimeMillis();
        model.addAttribute("results", results);
        model.addAttribute("keyword", keyword);
        model.addAttribute("time", end-start);
        return "jpa_search_results";
    }
}
