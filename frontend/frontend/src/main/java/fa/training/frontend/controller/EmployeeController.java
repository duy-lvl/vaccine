package fa.training.frontend.controller;

import fa.training.frontend.dto.request.employee.EmployeeRequest;
import fa.training.frontend.dto.request.employee.EmployeeUpdateDto;
import fa.training.frontend.dto.response.employee.EmployeeListDto;
import fa.training.frontend.dto.response.PageDto;
import fa.training.frontend.service.EmployeeService;
import fa.training.frontend.util.Gender;
import fa.training.frontend.validator.EmployeeValidator;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
@RequestMapping("/employees")
public class EmployeeController {

    // redirect controller
    private static final String REDIRECT_EMPLOYEES = "redirect:/employees"; // Constant for redirecting to employee list
    // redirect view
    private static final String EMPLOYEE_UPDATE_PAGE = "employee/update"; // Constant for the employee update page
    private static final String EMPLOYEE_ADD_PAGE = "employee/add"; // Constant for the employee add page
    //define model attribute
    private static final String EMPLOYEE_REQUEST_MODEL_ATTR = "employeeRequest"; // Constant for employee request model attribute
    private static final String MESSAGE_TYPE_SUCCESS = "success"; // Constant for success message type
    private static final String MESSAGE = "message"; // Constant for message attribute
    private static final String MESSAGE_TYPE = "messageType"; // Constant for message type attribute
    private static final String ERROR = "error"; // Constant for error message type

    private final EmployeeService employeeService; // Service to manage employee-related operations
    private final EmployeeValidator employeeValidator; // Validator to validate employee data

    @Autowired
    public EmployeeController(EmployeeService employeeService, EmployeeValidator employeeValidator) {
        this.employeeService = employeeService;
        this.employeeValidator = employeeValidator;
    }

    // Display the list of employees with pagination and search functionality
    @GetMapping
    public String showEmployeeList(Model model,
                                   @RequestParam(required = false, defaultValue = "1") int page,
                                   @RequestParam(required = false, defaultValue = "10") int size,
                                   @RequestParam(required = false, defaultValue = "") String search) {
        // Retrieve the current page of employees based on the given parameters
        PageDto<EmployeeListDto> currentPage = employeeService.getAllEmployee(page, size, search);
        model.addAttribute("pageDto", currentPage);
        model.addAttribute("size", size);
        model.addAttribute("page", page);
        model.addAttribute("totalPages", currentPage.getTotalPages());
        model.addAttribute("search", search);

        // Calculate the starting and ending index for the current page
        int start = currentPage.getData().isEmpty() ? 0 : (page - 1) * size + 1; // Calculate starting index
        int end = Math.min(page * size, currentPage.getTotalElements()); // Calculate ending index
        model.addAttribute("start", start);
        model.addAttribute("end", end);
        model.addAttribute("totalElements", currentPage.getTotalElements());
        return "employee/list";
    }

    // Handle the deletion of selected employees
    @PostMapping("/delete")
    public String deleteEmployeeList(@RequestParam("deleteIds") String ids,
                                     RedirectAttributes redirectAttributes) {
        if (ids != null && !ids.isEmpty()) {
            if (employeeService.deleteEmployee(ids)) {
                redirectAttributes.addFlashAttribute(MESSAGE, "Employees deleted successfully!");
                redirectAttributes.addFlashAttribute(MESSAGE_TYPE, MESSAGE_TYPE_SUCCESS);
            } else {
                redirectAttributes.addFlashAttribute(MESSAGE, "Deletion failed.");
                redirectAttributes.addFlashAttribute(MESSAGE_TYPE, ERROR);
            }
        } else {
            redirectAttributes.addFlashAttribute(MESSAGE, "No employees selected for deletion.");
            redirectAttributes.addFlashAttribute(MESSAGE_TYPE, ERROR);
        }

        return REDIRECT_EMPLOYEES;
    }

    @GetMapping("/create")
    public String showCreateEmployeePage(Model model) {
        EmployeeRequest employeeRequest = new EmployeeRequest();
        employeeRequest.setGender(Gender.MALE); // Set default gender to male  to show in view
        model.addAttribute(EMPLOYEE_REQUEST_MODEL_ATTR, employeeRequest); // Add employee request to the model
        return EMPLOYEE_ADD_PAGE;
    }

    // Handle the creation of a new employee
    @PostMapping("/create")
    public String createEmployee(@Valid @ModelAttribute EmployeeRequest employeeRequest,
                                 BindingResult bindingResult, @RequestParam("image") MultipartFile image,
                                 RedirectAttributes redirectAttributes, Model model) throws IOException {
        employeeValidator.validate(employeeRequest, bindingResult); // Validate employee request

        if (bindingResult.hasErrors()) {
            model.addAttribute(EMPLOYEE_REQUEST_MODEL_ATTR, employeeRequest);
            return EMPLOYEE_ADD_PAGE;
        }
        // Attempt to add the employee
        if (employeeService.addEmployee(employeeRequest, bindingResult, image)) {
            redirectAttributes.addFlashAttribute(MESSAGE, "Employee added successfully!");
            redirectAttributes.addFlashAttribute(MESSAGE_TYPE, MESSAGE_TYPE_SUCCESS);
        } else {
            redirectAttributes.addFlashAttribute(MESSAGE, "Addition failed.");
            redirectAttributes.addFlashAttribute(MESSAGE_TYPE, ERROR);
            model.addAttribute(EMPLOYEE_REQUEST_MODEL_ATTR, employeeRequest);
            return EMPLOYEE_ADD_PAGE;
        }
        return REDIRECT_EMPLOYEES;
    }

    // Get the details of an employee by ID
    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<EmployeeUpdateDto> getEmployeeDetails(@PathVariable String id) {
        EmployeeUpdateDto employeeDto = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(employeeDto);
    }

    // Show the page for updating an employee's information
    @GetMapping("/update/{id}")
    public String editEmployee(@PathVariable String id, Model model) {
        EmployeeUpdateDto employeeUpdateDto = employeeService.getEmployeeById(id);
        model.addAttribute("employeeUpdateDto", employeeUpdateDto);
        return EMPLOYEE_UPDATE_PAGE;
    }

    // Handle the updating of an employee's information
    @PostMapping("/update/{id}")
    public String processUpdate(@PathVariable String id,
                                @ModelAttribute("employeeUpdateDto") @Valid EmployeeUpdateDto employeeUpdateDto,
                                BindingResult bindingResult,
                                @RequestParam("imageFile") MultipartFile imageFile,
                                @RequestParam boolean isUpdating,
                                RedirectAttributes redirectAttributes, Model model) throws IOException {
        employeeValidator.validate(employeeUpdateDto, bindingResult); // Validate employee update
        if (bindingResult.hasErrors()) {
            model.addAttribute("employeeUpdateDto", employeeUpdateDto);
            return EMPLOYEE_UPDATE_PAGE;
        }
        // Attempt to update the employee
        if (employeeService.updateEmployee(id, employeeUpdateDto, imageFile, isUpdating, bindingResult)) {
            redirectAttributes.addFlashAttribute(MESSAGE, "Employee modified successfully!");
            redirectAttributes.addFlashAttribute(MESSAGE_TYPE, MESSAGE_TYPE_SUCCESS);
        } else {
            redirectAttributes.addFlashAttribute(MESSAGE, "Modification failed.");
            redirectAttributes.addFlashAttribute(MESSAGE_TYPE, ERROR);
            model.addAttribute(EMPLOYEE_REQUEST_MODEL_ATTR, employeeUpdateDto);
            return EMPLOYEE_UPDATE_PAGE;
        }
        return REDIRECT_EMPLOYEES;
    }
}
