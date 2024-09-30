package fa.training.frontend.controller;

import fa.training.frontend.dto.request.vaccine_type.NewVaccineTypeRequest;
import fa.training.frontend.dto.response.PageDto;
import fa.training.frontend.dto.response.Response;
import fa.training.frontend.dto.response.vaccine_type.VaccineTypeResponseDto;
import fa.training.frontend.service.VaccineTypeService;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.io.IOException;

@Controller
@RequestMapping("/vaccine-types")
public class VaccineTypeController {

    private final VaccineTypeService vaccineTypeService;

    @Autowired
    public VaccineTypeController(VaccineTypeService vaccineTypeService) {
        this.vaccineTypeService = vaccineTypeService;
    }

    // *** CREATE Methods ***

    // GET method to display the form for adding a new vaccine type
    @GetMapping("/add")
    public String getAddVaccineType(Model model) {
        model.addAttribute("newVaccineTypeRequest", new NewVaccineTypeRequest());
        return "vaccine-type/add";
    }

    // POST method to add a new vaccine type
    @PostMapping("/add")
    public String postAddVaccineType(
            @Valid @ModelAttribute NewVaccineTypeRequest newVaccineTypeRequest,
            BindingResult bindingResult,
            @RequestParam("image") MultipartFile image,
            Model model,
            RedirectAttributes redirectAttributes
    ) throws IOException {

        String response = vaccineTypeService.add(newVaccineTypeRequest, bindingResult, image);
        if (bindingResult.hasErrors() || response.equals("Error in adding vaccine type")) {
            model.addAttribute("newVaccineTypeRequest", newVaccineTypeRequest);
            redirectAttributes.addFlashAttribute("message", "Add failed.");
            redirectAttributes.addFlashAttribute("messageType", "error");
            return "vaccine-type/add";
        }
        return "redirect:/vaccine-types?message=" + response;
    }

    // *** READ Methods ***

    // GET method to fetch and display a paginated list of vaccine types
    @GetMapping
    public String getList(Model model,
                          @RequestParam(value = "size", defaultValue = "5") int size,
                          @RequestParam(value = "page", defaultValue = "1") int page,
                          @RequestParam(value = "query", defaultValue = "") String query) {
        PageDto<VaccineTypeResponseDto> pageDto = vaccineTypeService.getAllVaccineTypes(page, size, query);
        model.addAttribute("totalPages", pageDto.getTotalPages());

        // Calculate the starting and ending index for the current page
        int start = pageDto.getData().isEmpty() ? 0 : (page - 1) * size + 1;
        int end = Math.min(page * size, pageDto.getTotalElements());
        model.addAttribute("vaccineTypes", pageDto);
        model.addAttribute("size", size);
        model.addAttribute("page", page);
        model.addAttribute("query", query);
        model.addAttribute("start", start);
        model.addAttribute("end", end);
        model.addAttribute("totalElements", pageDto.getTotalElements());
        model.addAttribute("dataVaccine", 1);
        return "vaccine-type/list";
    }

    // GET method to fetch the details of a specific vaccine type for update
    @GetMapping("/update/{id}")
    public String showUpdateVaccineTypeForm(@PathVariable String id, Model model) {
        model.addAttribute("vaccineType", vaccineTypeService.getVaccineTypeById(id));
        return "vaccine-type/update";
    }

    // *** UPDATE Methods ***

    // POST method to update an existing vaccine type
    @PostMapping("/update/{id}")
    public String updateVaccineType(@PathVariable String id,
                                    @ModelAttribute("vaccineType") VaccineTypeResponseDto vaccineType,
                                    BindingResult bindingResult,
                                    MultipartFile image,
                                    @ModelAttribute("isUpdatingImage") boolean isUpdatingImage,
                                    Model model) throws IOException {
        Response response = new Response();
        if (bindingResult.hasErrors() || !vaccineTypeService.updateVaccineType(vaccineType, id, image, isUpdatingImage, bindingResult, response)) {
            model.addAttribute("vaccineType", vaccineType);
            return "vaccine-type/update";
        }
        return "redirect:/vaccine-types?message=" + response.getDescription();
    }

    // *** DELETE (Inactivation) Methods ***

    // POST method to make vaccine types inactive
    @PostMapping("/make-inactive")
    public String makeInActiveVaccineTypes(@RequestParam("ids") String ids, RedirectAttributes redirectAttributes) {
        try {
            Response response = vaccineTypeService.makeInActive(ids);
            return "redirect:/vaccine-types?message=" + response.getDescription();
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("message", "An error occurred during the operation.");
            return "redirect:/vaccine-types";
        }
    }

    // POST method to make vaccine types active
    @PostMapping("/make-active")
    public String makeActiveVaccineTypes(@RequestParam("ids") String ids, RedirectAttributes redirectAttributes) {
        try {
            Response response = vaccineTypeService.makeActive(ids);
            return "redirect:/vaccine-types?messageSwitchStatusAndUpdate=" + response.getDescription();
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("message", "An error occurred during the operation.");
            return "redirect:/vaccine-types";
        }
    }
}
