package fa.training.backend.generator;

import jakarta.annotation.PostConstruct;

public class VaccineTypeIdGenerator extends IdGenerator {
    @PostConstruct
    private void init() {
        ID_PREFIX = "VTP";
        TABLE_NAME = "vaccine_type";
    }
}