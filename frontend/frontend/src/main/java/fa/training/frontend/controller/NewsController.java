package fa.training.frontend.controller;

import fa.training.frontend.dto.request.news.NewsAddRequest;
import fa.training.frontend.dto.request.news.NewsRequestDto;
import fa.training.frontend.dto.request.news.NewsResponseDto;
import fa.training.frontend.dto.request.news_type.NewsTypeDto;
import fa.training.frontend.dto.response.PageDto;
import fa.training.frontend.service.NewsService;
import fa.training.frontend.service.NewsTypeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/news")
public class NewsController {

    //redirect controller
    private static final String REDIRECT_NEWS = "redirect:/news";

    //define attribute name
    private static final String NEWS_TYPE_DTO_LIST_ATTRIBUTE = "newsTypeDtoList";

    private final NewsService newsService;
    private final NewsTypeService newsTypeService;

    @Autowired
    public NewsController(NewsService newsService, NewsTypeService newsTypeService) {
        this.newsService = newsService;
        this.newsTypeService = newsTypeService;
    }


    @GetMapping
    public String getList(Model model,
                          @RequestParam(value = "page", defaultValue = "1") int page,
                          @RequestParam(value = "size", defaultValue = "5") int size,
                          @RequestParam(value = "search", defaultValue = "") String search) {

        PageDto<NewsResponseDto> newsResponseDtoPageDto = newsService.getAll(page, size, search);
        model.addAttribute("pageDto", newsResponseDtoPageDto);
        model.addAttribute("size", size);
        model.addAttribute("page", page);
        model.addAttribute("search", search);
        model.addAttribute("totalPages", newsResponseDtoPageDto.getTotalPages());

        int start;
        if (newsResponseDtoPageDto.getData().isEmpty()) {
            start = 0;
        } else {
            start = (page - 1) * size + 1;
        }
        int end = Math.min(page * size, newsResponseDtoPageDto.getTotalElements());
        model.addAttribute("start", start);
        model.addAttribute("end", end);
        model.addAttribute("totalElements", newsResponseDtoPageDto.getTotalElements());

        return "news/list";
    }

    @GetMapping("/add")
    public String create(Model model) {
        List<NewsTypeDto> newsTypeDtoList = newsTypeService.getAllNewsType();
        model.addAttribute("newsAddRequest", new NewsAddRequest());
        model.addAttribute(NEWS_TYPE_DTO_LIST_ATTRIBUTE, newsTypeDtoList);
        return "news/add";
    }

    @PostMapping("/add")
    public String addNews(@ModelAttribute @Valid NewsAddRequest newsAddRequest,
                          BindingResult bindingResult,
                          Model model,
                          RedirectAttributes redirectAttributes) {
        if(bindingResult.hasErrors()) {
            model.addAttribute(NEWS_TYPE_DTO_LIST_ATTRIBUTE, newsTypeService.getAllNewsType());
            return "news/add";
        }
        newsService.create(newsAddRequest);
        redirectAttributes.addFlashAttribute("message", "News added successfully!");
        redirectAttributes.addFlashAttribute("messageType", "success");
        return REDIRECT_NEWS;
    }

    @GetMapping("{id}")
    @ResponseBody
    public ResponseEntity<NewsRequestDto> getNews(@PathVariable String id) {
        NewsRequestDto newsUpdateRequestDto = newsService.getById(id);
        return ResponseEntity.ok(newsUpdateRequestDto);
    }

    @GetMapping("/update/{id}")
    public String edit(Model model, @PathVariable String id) {
        NewsRequestDto newsUpdateRequestDto = newsService.getById(id);
        List<NewsTypeDto> newsTypeDtoList = newsTypeService.getAllNewsType();
        model.addAttribute("newsUpdateRequestDto", newsUpdateRequestDto);
        model.addAttribute(NEWS_TYPE_DTO_LIST_ATTRIBUTE, newsTypeDtoList);
        return "news/edit";
    }

    @PostMapping("/update/{id}")
    public String update(@ModelAttribute("newsUpdateRequestDto") @Valid NewsRequestDto newsUpdateRequestDto,
                         BindingResult bindingResult,
                         Model model,
                         @PathVariable String id,
                         RedirectAttributes redirectAttributes) {
        if(bindingResult.hasErrors()) {
            model.addAttribute(NEWS_TYPE_DTO_LIST_ATTRIBUTE, newsTypeService.getAllNewsType());
            return "news/edit";
        }
        newsService.update(newsUpdateRequestDto, id);
        redirectAttributes.addFlashAttribute("message", "News update successfully!");
        redirectAttributes.addFlashAttribute("messageType", "success");
        return REDIRECT_NEWS;
    }

    @PostMapping("/delete")
    public String delete(@RequestParam("deleteNewsId") String id, RedirectAttributes redirectAttributes) {
        if (id != null && !id.isEmpty()) {
            newsService.delete(id);
            redirectAttributes.addFlashAttribute("message", "News deleted successfully");
        } else {
            redirectAttributes.addFlashAttribute("message", "News deleted failed");
        }
        redirectAttributes.addFlashAttribute("message", "News delete successfully!");
        redirectAttributes.addFlashAttribute("messageType", "success");
        return REDIRECT_NEWS;
    }
}
