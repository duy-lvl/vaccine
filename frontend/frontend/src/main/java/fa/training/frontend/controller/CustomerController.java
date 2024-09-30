package fa.training.frontend.controller;

import fa.training.frontend.dto.request.customer.CustomerAddRequestDto;
import fa.training.frontend.dto.request.customer.CustomerUpdateRequestDto;
import fa.training.frontend.dto.response.customer.CustomerResponseDto;
import fa.training.frontend.dto.response.PageDto;
import fa.training.frontend.service.CustomerService;
import fa.training.frontend.util.Gender;
import fa.training.frontend.validator.CustomerValidator;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/customers")
public class CustomerController {
    //redirect controller
    private static final String REDIRECT_CUSTOMERS = "redirect:/customers";

    //return view
    private static final String CUSTOMER_UPDATE_VIEW = "customer/update";
    private static final String CUSTOMER_ADD_VIEW = "customer/add";

    private final CustomerValidator customerValidator;
    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService, CustomerValidator customerValidator) {
        this.customerService = customerService;
        this.customerValidator = customerValidator;
    }

    @GetMapping
    public String showListCustomers(Model model,
                                    @RequestParam(required = false, defaultValue = "1") int page,
                                    @RequestParam(required = false, defaultValue = "10") int size,
                                    @RequestParam(required = false, defaultValue = "") String search
    ) {
        PageDto<CustomerResponseDto> currentPage = customerService.getAllCustomers(page, size, search);
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
        return "customer/list";
    }

    @GetMapping("/add")
    public String showAddCustomerForm(Model model) {
        CustomerAddRequestDto customerAddRequestDto = new CustomerAddRequestDto();
        customerAddRequestDto.setGender(Gender.MALE);
        model.addAttribute("customer", customerAddRequestDto);
        return CUSTOMER_ADD_VIEW;
    }

    @PostMapping("/add")
    public String saveCustomer(@Valid @ModelAttribute("customer") CustomerAddRequestDto customerAddRequestDto,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            customerValidator.validate(customerAddRequestDto, bindingResult); // Nếu cần có thể validate bổ sung
            return CUSTOMER_ADD_VIEW;
        }
        String id = customerService.addAndGetId(customerAddRequestDto, bindingResult);
        if (id == null) {
            return CUSTOMER_ADD_VIEW;
        }
        redirectAttributes.addFlashAttribute("successMessage", "Customer created successfully!");
        return "redirect:/customers/update/" + id;
    }


    @GetMapping("/update/{id}")
    public String showUpdateCustomerForm(@PathVariable String id, Model model) {
        model.addAttribute("customer", customerService.getCustomerById(id));
        return CUSTOMER_UPDATE_VIEW;
    }


    @PostMapping("/update/{id}")
    public String updateCustomer(@PathVariable String id,
                                 @Valid @ModelAttribute("customer") CustomerUpdateRequestDto customerUpdateRequestDto,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return CUSTOMER_UPDATE_VIEW;
        }
        boolean isUpdated = customerService.updateCustomer(customerUpdateRequestDto, id, bindingResult);
        if (!isUpdated) {
            return CUSTOMER_UPDATE_VIEW;
        }
        redirectAttributes.addFlashAttribute("successMessage", "Customer update successfully!");
        return "redirect:/customers/update/" + id;
    }

    @PostMapping("/delete/{id}")
    public String softDeleteCustomer(@PathVariable String id) {
        customerService.deleteCustomer(id);
        return REDIRECT_CUSTOMERS;
    }
}
