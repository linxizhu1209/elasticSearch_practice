package org.example.es.Controller;

import co.elastic.clients.elasticsearch._types.SortOrder;
import lombok.RequiredArgsConstructor;
import org.example.es.Data.QuestionDocument;
import org.example.es.Service.QuestionSearchService;
import org.example.es.dto.HighlightedResult;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {

    private final QuestionSearchService questionSearchService;

    @GetMapping("/form")
    public String searchForm(){
        return "search_form";
    }
    

    @GetMapping("/highlight")
    public String searchWithHighlight(@RequestParam String keyword, Model model,
                                      @RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "10") int size,
                                      @RequestParam(defaultValue = "Desc") SortOrder sortOrder) throws IOException {

        List<HighlightedResult> results = questionSearchService.searchWithHighlightAndSort(keyword, sortOrder, page, size);
        model.addAttribute("results", results);
        model.addAttribute("keyword", keyword);
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        model.addAttribute("sortOrder", sortOrder);
        return "search_results";
    }

    @GetMapping("/autocomplete")
    @ResponseBody
    public List<String> autocomplete(@RequestParam String keyword) throws IOException {
        return questionSearchService.autocompleteTitle(keyword);
    }


}
