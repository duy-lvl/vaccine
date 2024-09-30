package fa.training.frontend.controller;

import fa.training.frontend.dto.request.injection_reasult.InjectionResultAddRequest;
import fa.training.frontend.dto.request.injection_reasult.InjectionResultRequest;
import fa.training.frontend.dto.request.injection_reasult.InjectionResultDto;
import fa.training.frontend.dto.response.injection_schedule.InjectionScheduleDto;
import fa.training.frontend.dto.response.PageDto;
import fa.training.frontend.service.InjectionResultService;
import fa.training.frontend.service.VaccineTypeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/injection-results")
public class InjectionResultController {

    //redirect controller
    private static final String INJECTION_RESULT_ADD_VIEW = "injection-result/add";

    //define attribute name
    private static final String CUSTOMERS_ATTRIBUTE = "customers";
    private static final String VACCINE_TYPES_ATTRIBUTE = "vaccineTypes";
    private static final String MESSAGE_TYPE_ATTRIBUTE = "messageType";
    private static final String MESSAGE_ATTRIBUTE = "message";

    private final InjectionResultService injectionResultService;
    private final VaccineTypeService vaccineTypeService;



    @Autowired
    public InjectionResultController(InjectionResultService injectionResultService, VaccineTypeService vaccineTypeService) {
        this.injectionResultService = injectionResultService;
        this.vaccineTypeService = vaccineTypeService;
           }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("injectionResultRequest", new InjectionResultRequest());
        model.addAttribute("request", new InjectionResultAddRequest());
        model.addAttribute(CUSTOMERS_ATTRIBUTE, injectionResultService.getCustomerList());
        model.addAttribute(VACCINE_TYPES_ATTRIBUTE, vaccineTypeService.getVaccineTypes(false));
        return INJECTION_RESULT_ADD_VIEW;
    }

    @PostMapping("/create")
    public String createInjectionResult(@Valid @ModelAttribute("request") InjectionResultAddRequest request,
                                        BindingResult bindingResult,
                                        @ModelAttribute("injectionResultRequest") InjectionResultRequest injectionResultRequest,
                                        Model model,
                                        @RequestParam(defaultValue = "1") int page,
                                        @RequestParam(defaultValue = "10") int size,
                                        RedirectAttributes redirectAttributes) {
        model.addAttribute("injectionResultRequest", injectionResultRequest);
        model.addAttribute(CUSTOMERS_ATTRIBUTE, injectionResultService.getCustomerList());
        model.addAttribute(VACCINE_TYPES_ATTRIBUTE, vaccineTypeService.getVaccineTypes(false));
        if (bindingResult.hasErrors()) {
            return INJECTION_RESULT_ADD_VIEW;
        }

        PageDto<InjectionScheduleDto> result = injectionResultService.createAndGetSchedule(request, page, size);
        model.addAttribute("result", result);
        redirectAttributes.addFlashAttribute("message", "Result create successfully!");
        redirectAttributes.addFlashAttribute("messageType", "success");
        return INJECTION_RESULT_ADD_VIEW;

    }

    @PostMapping("/add")
    public String add(
            @Valid @ModelAttribute("injectionResultRequest") InjectionResultRequest injectionResultRequest,
            BindingResult bindingResult,
            @ModelAttribute("request") InjectionResultAddRequest request,
            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("request", request);
            model.addAttribute(CUSTOMERS_ATTRIBUTE, injectionResultService.getCustomerList());
            model.addAttribute(VACCINE_TYPES_ATTRIBUTE, vaccineTypeService.getVaccineTypes(true));
            return INJECTION_RESULT_ADD_VIEW;
        }
        injectionResultService.add(injectionResultRequest);

        return "redirect:/injection-results";
    }

    @GetMapping
    public String showEmployeeList(Model model,
                                   @RequestParam(required = false, defaultValue = "1") int page,
                                   @RequestParam(required = false, defaultValue = "10") int size,
                                   @RequestParam(required = false, defaultValue = "") String search
    ) {
        PageDto<InjectionResultDto> currentPage = injectionResultService.getAllInjectionResult(page, size, search);
        model.addAttribute("pageDto", currentPage);
        model.addAttribute("size", size); // Add size to the model
        model.addAttribute("page", page);
        model.addAttribute("totalPages", currentPage.getTotalPages()); // Giả sử bạn có phương thức getTotalPages() trong PageDto
        model.addAttribute("search", search);
        // Calculate the starting and ending index for the current page
        int start;
        if (currentPage.getData().isEmpty()) {
            start = 0;
        } else {
            start = (page - 1) * size + 1;
        }
        int end = Math.min(page * size, currentPage.getTotalElements());
        model.addAttribute("start", start);
        model.addAttribute("end", end);
        model.addAttribute("totalElements", currentPage.getTotalElements());
        return "injection-result/list";
    }

    @PostMapping("/delete")
    public String deleteInjectionResultList(@RequestParam("resultId") String ids,
                                            RedirectAttributes redirectAttributes) {
        if (ids != null && !ids.isEmpty()) {

            if (injectionResultService.deleteInjectionResults(ids)) {
                redirectAttributes.addFlashAttribute(MESSAGE_ATTRIBUTE, "Injection result deleted successfully!");
                redirectAttributes.addFlashAttribute(MESSAGE_TYPE_ATTRIBUTE, "success");
            } else {
                redirectAttributes.addFlashAttribute(MESSAGE_ATTRIBUTE, "delete fail.");
                redirectAttributes.addFlashAttribute(MESSAGE_TYPE_ATTRIBUTE, "error");
            }

        } else {
            // Thêm thông báo lỗi nếu không có ID được chọn
            redirectAttributes.addFlashAttribute(MESSAGE_ATTRIBUTE, "No Injection result selected for deletion.");
            redirectAttributes.addFlashAttribute(MESSAGE_TYPE_ATTRIBUTE, "error");
        }

        return "redirect:/injection-results";
    }

    @PostMapping("/search")
    public String searchForAdd(@ModelAttribute @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                               @ModelAttribute String vaccineTypeId,
                               Model model) {
        model.addAttribute(VACCINE_TYPES_ATTRIBUTE, vaccineTypeService.getVaccineTypes(true));
        return null;
    }
}
