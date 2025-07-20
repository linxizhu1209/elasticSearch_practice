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
    public String searchForm(Model model) throws IOException {
        List<String> popularKeywords = questionSearchService.getTopKeywordsLast7Days();
        model.addAttribute("popularKeywords", popularKeywords);
        return "search_form";
    }


    @GetMapping("/highlight")
    public String searchQuestions(@RequestParam String keyword, Model model,
                                      @RequestParam(defaultValue = "title") String target,
                                      @RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "10") int size,
                                      @RequestParam(defaultValue = "Desc") String sortOrder) throws IOException {

        List<HighlightedResult> results = questionSearchService.search(keyword, target, sortOrder, page, size);
        model.addAttribute("results", results);
        model.addAttribute("keyword", keyword);
        model.addAttribute("target", target);
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


    @GetMapping("/comment")
    public String searchByComment(
            @RequestParam String author,
            @RequestParam String text,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "Desc") String sortOrder,
            Model model
    ) throws IOException {
        List<HighlightedResult> results = questionSearchService.searchByComment(author,text,sortOrder,page,size);

        model.addAttribute("results", results);
        model.addAttribute("author", author);
        model.addAttribute("text", text);
        model.addAttribute("target","comments");
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        model.addAttribute("sortOrder", sortOrder);
        return "search_results";

    }




}
