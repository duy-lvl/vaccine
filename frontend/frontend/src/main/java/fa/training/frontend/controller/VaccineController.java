package fa.training.frontend.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import fa.training.frontend.dto.request.vaccine.VaccineAddRequestDto;
import fa.training.frontend.dto.response.*;
import fa.training.frontend.dto.request.vaccine.VaccineUpdateDto;
import fa.training.frontend.dto.response.employee.EmployeeListDto;
import fa.training.frontend.dto.response.vaccine.VaccineResponseDto;
import fa.training.frontend.dto.response.vaccine_type.VaccineTypeNameDto;
import fa.training.frontend.dto.response.vaccine_type.VaccineTypeResponseDto;
import fa.training.frontend.service.InjectionResultService;
import fa.training.frontend.service.VaccineService;
import fa.training.frontend.service.VaccineTypeService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/vaccines")

public class VaccineController {

    private final VaccineService vaccineService;

    private final VaccineTypeService vaccineTypeService;

    @Autowired
    public VaccineController(VaccineService vaccineService,  VaccineTypeService vaccineTypeService) {
        this.vaccineService = vaccineService;
        this.vaccineTypeService = vaccineTypeService;

    }

    @GetMapping("/add")
    public String addVaccineForm(Model model) {
        VaccineAddRequestDto vaccine = new VaccineAddRequestDto();
        vaccine.setNumberOfInjection(1);
        model.addAttribute("vaccine", vaccine);
        model.addAttribute("vaccineTypes", vaccineService.getVaccineTypes());
        return "vaccine/add";
    }

    @PostMapping("/add")
    public String saveVaccineSuccess(@ModelAttribute("vaccine") @Valid VaccineAddRequestDto vaccineAddRequestDto,
                                     BindingResult bindingResult,
                                     Model model,
                                     RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "vaccine/add";
        }
        String id = vaccineService.addVaccine(vaccineAddRequestDto, bindingResult);
        if (id == null) {
            model.addAttribute("vaccineTypes", vaccineService.getVaccineTypes());
            return "vaccine/add";
        }
        redirectAttributes.addFlashAttribute("successMessage", "Customer created successfully!");
        return "redirect:/vaccines/update/" + id;
    }

    @GetMapping
    public String getAllListVaccine(Model model,
                                    @RequestParam(value = "size", defaultValue = "5") int size,
                                    @RequestParam(value = "page", defaultValue = "1") int page,
                                    @RequestParam(value = "query", defaultValue = "") String query
    ) {
        // Retrieve the current page of vaccines based on the given parameters
        PageDto<VaccineResponseDto> currentPage = vaccineService.getAllVaccines(page, size, query);
        model.addAttribute("vaccines", currentPage);
        model.addAttribute("size", size);
        model.addAttribute("page", page);
        model.addAttribute("totalPages", currentPage.getTotalPages());
        model.addAttribute("query", query);

        // Calculate the starting and ending index for the current page
        int start = currentPage.getData().isEmpty() ? 0 : (page - 1) * size + 1; // Calculate starting index
        int end = Math.min(page * size, currentPage.getTotalElements()); // Calculate ending index
        model.addAttribute("start", start);
        model.addAttribute("end", end);
        model.addAttribute("totalElements", currentPage.getTotalElements());
        model.addAttribute("dataVaccine", 1);
        return "vaccine/list";
    }

    @GetMapping("/update/{id}")
    public String updateVaccineById(@PathVariable String id, Model model) {
        List<VaccineTypeNameDto> vaccineTypeNameList = vaccineTypeService.getVaccineTypes(true);
        VaccineUpdateDto vaccineUpdateDto = vaccineService.getVaccineByIdForUpdate(id);

        // Kiểm tra nếu vaccineTypeId không có trong danh sách active
        boolean isVaccineTypeInactive = vaccineTypeNameList.stream()
                .noneMatch(vaccineType -> vaccineType.getId().equals(vaccineUpdateDto.getVaccineTypeId()));


        String inactiveVaccineTypeName = null;
        if (isVaccineTypeInactive) {
            // Lấy tên vaccine type inactive từ vaccineUpdateDto hoặc từ service khác
            VaccineTypeResponseDto vaccineTypeResponseDto = vaccineTypeService.getVaccineTypeById(vaccineUpdateDto.getVaccineTypeId());
            inactiveVaccineTypeName = vaccineTypeResponseDto.getName();
        }
        model.addAttribute("vaccineTypeNameList", vaccineTypeNameList);
        model.addAttribute("vaccineUpdateDto", vaccineUpdateDto);
        model.addAttribute("isVaccineTypeInactive", isVaccineTypeInactive);
        model.addAttribute("inactiveVaccineTypeName", inactiveVaccineTypeName);
        return "vaccine/update";
    }

    @PostMapping("/update")
    public String updateVaccine(@ModelAttribute("vaccineUpdateDto") @Valid VaccineUpdateDto vaccineUpdateDto,
                                BindingResult bindingResult,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            // If there are validation errors, reload the vaccine types and return to the update page
            List<VaccineTypeNameDto> vaccineTypeNameList = vaccineTypeService.getVaccineTypes(true);
            model.addAttribute("vaccineTypeNameList", vaccineTypeNameList);
            model.addAttribute("isVaccineTypeInactive", vaccineUpdateDto.getVaccineTypeId() != null &&
                    vaccineTypeNameList.stream().noneMatch(vaccineType -> vaccineType.getId().equals(vaccineUpdateDto.getVaccineTypeId())));
            model.addAttribute("inactiveVaccineTypeName", vaccineTypeNameList.stream()
                    .filter(vaccineType -> vaccineType.getId().equals(vaccineUpdateDto.getVaccineTypeId()))
                    .map(VaccineTypeNameDto::getName)
                    .findFirst()
                    .orElse(null));
            return "vaccine/update";
        }
        boolean updateSuccessful = vaccineService.updateVaccine(vaccineUpdateDto, bindingResult);
        if (!updateSuccessful) {
            List<VaccineTypeNameDto> vaccineTypeNameList = vaccineTypeService.getVaccineTypes(true);
            model.addAttribute("vaccineTypeNameList", vaccineTypeNameList);
            model.addAttribute("isVaccineTypeInactive", vaccineUpdateDto.getVaccineTypeId() != null &&
                    vaccineTypeNameList.stream().noneMatch(vaccineType -> vaccineType.getId().equals(vaccineUpdateDto.getVaccineTypeId())));
            model.addAttribute("inactiveVaccineTypeName", vaccineTypeNameList.stream()
                    .filter(vaccineType -> vaccineType.getId().equals(vaccineUpdateDto.getVaccineTypeId()))
                    .map(VaccineTypeNameDto::getName)
                    .findFirst()
                    .orElse(null));
            // Add an error message if the update failed
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update vaccine. Please try again.");
        } else {
            // Redirect back to the update page with the appropriate message
            redirectAttributes.addFlashAttribute("successMessage", "Vaccine updated successfully!");
        }

        return "redirect:/vaccines/update/" + vaccineUpdateDto.getId();
    }

    @GetMapping("/template")
    public void get(HttpServletResponse response) {
        byte[] file = vaccineService.downloadTemplate();
        if (file != null) {
            try {
                response.setCharacterEncoding("UTF-8");
                response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                response.setHeader("Content-Disposition", "attachment; filename=\"Vaccines.xlsx\"");
                response.getOutputStream().write(file);
                response.getOutputStream().close();
            } catch (IOException e) {
                //exception handle
            }
        }
    }

    @GetMapping("/import")
    public String getImport() {
        return "vaccine/import";
    }

    @PostMapping("/import")
    public String importVaccine(MultipartFile file, Model model) throws IOException {
        Response response = vaccineService.importVaccine(file);
        if (response.isSuccessful()) {
            model.addAttribute("message", response.getDescription());
            model.addAttribute("messageType", "success");
        } else {
            model.addAttribute("message", response.getData());
            model.addAttribute("messageType", "fail");
        }
        return "vaccine/import";
    }
}
