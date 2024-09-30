package fa.training.frontend.controller;

import fa.training.frontend.dto.request.injection_schedule.InjectionScheduleUpdateRequest;
import fa.training.frontend.dto.request.injection_schedule.InjectionScheduleAddRequest;
import fa.training.frontend.dto.request.vaccine.VaccineInjectionScheduleRequestDto;
import fa.training.frontend.dto.response.PageDto;
import fa.training.frontend.dto.response.customer.CustomerResponseDto;
import fa.training.frontend.dto.response.injection_schedule.InjectionScheduleDto;
import fa.training.frontend.service.InjectionScheduleService;
import fa.training.frontend.service.VaccineService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/injection-schedules")
public class InjectionScheduleController {
    private final InjectionScheduleService injectionScheduleService;
    private final VaccineService vaccineService;

    @Autowired
    public InjectionScheduleController(InjectionScheduleService injectionScheduleService, VaccineService vaccineService) {
        this.injectionScheduleService = injectionScheduleService;
        this.vaccineService = vaccineService;
    }

    @GetMapping("/update/{id}")
    public String updateInjectionScheduleForm(@PathVariable String id, Model model) {
        model.addAttribute("injectionSchedule", injectionScheduleService.getInjectionScheduleById(id));
//        PageDto<VaccineIndividualDto> vaccinesPage = vaccineService.getAllVaccines(1, 1000, "");
//        List<VaccineIndividualDto> vaccines = vaccinesPage.getData();
//        model.addAttribute("vaccines", vaccines);
        model.addAttribute("vaccines", injectionScheduleService.getVaccines());
        return "injection-schedule/update";
    }

    @PostMapping("/update/{id}")
    public String updateInjectionSchedule(@PathVariable String id, @Valid @ModelAttribute("injectionSchedule") InjectionScheduleUpdateRequest injectionScheduleUpdateRequest,
                                          BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "injection-schedule/update";
        }
        injectionScheduleService.updateInjectionSchedule(injectionScheduleUpdateRequest, id);
        return "redirect:/injection-schedules";

    }

    @GetMapping("/create")
    public String createInjectionSchedule(Model model) {
        List<VaccineInjectionScheduleRequestDto> nameVaccineList = vaccineService.getAllVaccineForInjectionSchedule();
        model.addAttribute("vaccines", nameVaccineList);
        model.addAttribute("injectionSchedule", new InjectionScheduleAddRequest());
        return "injection-schedule/add";
    }

    @PostMapping("/create")
    public String processCreateInjection(@ModelAttribute("injectionSchedule") @Valid InjectionScheduleAddRequest injectionScheduleAddRequest,
                                         BindingResult bindingResult,
                                         Model model,
                                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            List<VaccineInjectionScheduleRequestDto> nameVaccineList = vaccineService.getAllVaccineForInjectionSchedule();
            model.addAttribute("vaccines", nameVaccineList);
            return "injection-schedule/add";
        }
        if (injectionScheduleService.saveInjectionSchedule(injectionScheduleAddRequest)) {
            redirectAttributes.addFlashAttribute("message", "injection schedule added successfully!");
            redirectAttributes.addFlashAttribute("alertClass", "alert-success");
        } else {
            redirectAttributes.addFlashAttribute("message", "Failed to add injection schedule. Please try again.");
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
        }
        return "redirect:/injection-schedules";

    }

    @GetMapping
    public String getAll(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "5") int size,
            @RequestParam(required = false, defaultValue = "") String search,
            Model model
    ) {
        PageDto<InjectionScheduleDto> currentPage = injectionScheduleService.getList(page, size, search);
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
        return "injection-schedule/list";
    }
}
