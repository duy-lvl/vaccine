package fa.training.backend.generator;

import jakarta.annotation.PostConstruct;

public class VaccineIdGenerator extends IdGenerator {
    @PostConstruct
    private void init() {
        ID_PREFIX = "VAC";
        TABLE_NAME = "vaccine";
    }
}