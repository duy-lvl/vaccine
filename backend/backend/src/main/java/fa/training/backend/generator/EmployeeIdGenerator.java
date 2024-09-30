package fa.training.backend.generator;

import jakarta.annotation.PostConstruct;

public class EmployeeIdGenerator extends IdGenerator {
    @PostConstruct
    private void init() {
        ID_PREFIX = "EMP";
        TABLE_NAME = "employee";
    }
}