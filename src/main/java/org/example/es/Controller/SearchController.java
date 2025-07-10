package org.example.es.Controller;

import lombok.RequiredArgsConstructor;
import org.example.es.Data.QuestionDocument;
import org.example.es.Repository.QuestionDocumentRepository;
import org.example.es.Service.QuestionSearchService;
import org.example.es.dto.HighlightedResult;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {

    private final QuestionDocumentRepository questionDocumentRepository;
    private final QuestionSearchService questionSearchService;

    @GetMapping("/form")
    public String searchForm(){
        return "search_form";
    }

    @GetMapping("/results")
    public String searchResults(@RequestParam String keyword, Model model){
        long start = System.currentTimeMillis();
        List<QuestionDocument> results = questionDocumentRepository.searchByTitle(keyword);
        long end = System.currentTimeMillis();
        model.addAttribute("results", results);
        model.addAttribute("keyword", keyword);
        model.addAttribute("time", end-start);
        return "search_results";
    }

    @GetMapping("/highlight")
    public String searchWithHighlight(@RequestParam String keyword, Model model){
        List<HighlightedResult> results = questionSearchService.searchWithHighlight(keyword);
        model.addAttribute("results", results);
        return "search_results";
    }
}
