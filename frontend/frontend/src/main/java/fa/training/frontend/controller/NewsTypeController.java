package fa.training.frontend.controller;

import fa.training.frontend.dto.request.news_type.NewsTypeRequest;
import fa.training.frontend.dto.request.news_type.NewsTypeDto;
import fa.training.frontend.service.NewsTypeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;


@Controller
@RequestMapping("/news-types")
public class NewsTypeController {

    //redirect controller
    private static final String REDIRECT_NEWS_TYPES = "redirect:/news-types";

    //define attribute name
    private static final String MESSAGE_ATTRIBUTE = "message";

    private final NewsTypeService newsTypeService;

    @Autowired
    public NewsTypeController(NewsTypeService newsTypeService) {
        this.newsTypeService = newsTypeService;
    }

    @GetMapping
    public String getAll(Model model) {
        List<NewsTypeDto> newsTypes = newsTypeService.getAll();
        model.addAttribute("newsTypes", newsTypes);
        return "news-type/list";
    }

    @GetMapping("/add")
    public String create(Model model) {
        model.addAttribute("newsType", new NewsTypeRequest());
        return "news-type/add";
    }

    @PostMapping("/add")
    public String create(@ModelAttribute("newsType") @Valid NewsTypeRequest newsType,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "news-type/add";
        }
        newsTypeService.create(newsType);
        redirectAttributes.addAttribute(MESSAGE_ATTRIBUTE, "Add news type successfully!");
        return REDIRECT_NEWS_TYPES;
    }

    @GetMapping("/update/{id}")
    public String update(Model model, @PathVariable String id) {
        NewsTypeDto newsTypeDto = newsTypeService.getById(id);
        model.addAttribute("request", newsTypeDto);
        return "news-type/update";
    }

    @PostMapping("/update/{id}")
    public String update(@PathVariable String id,
                         @ModelAttribute("request") @Valid NewsTypeRequest request,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {

            return "news-type/update";
        }
        newsTypeService.update(request, id);
        redirectAttributes.addAttribute(MESSAGE_ATTRIBUTE, "Add news type successfully!");
        return REDIRECT_NEWS_TYPES;
    }

    // Handle the deletion of selected employees
    @PostMapping("/delete")
    public String deleteNewsTypeList(@RequestParam("deleteIds") String ids,
                                     RedirectAttributes redirectAttributes) {
        if (ids != null && !ids.isEmpty()) {
            if (newsTypeService.deleteNewsType(ids)) {
                redirectAttributes.addFlashAttribute(MESSAGE_ATTRIBUTE, "Deletion failed.");
                redirectAttributes.addFlashAttribute("messageType", "error");
            }
        } else {
            redirectAttributes.addFlashAttribute(MESSAGE_ATTRIBUTE, "No news type selected for deletion.");
            redirectAttributes.addFlashAttribute("messageType", "error");
        }
        return REDIRECT_NEWS_TYPES;

    }

}
