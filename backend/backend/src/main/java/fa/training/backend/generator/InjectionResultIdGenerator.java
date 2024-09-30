package fa.training.backend.generator;

import jakarta.annotation.PostConstruct;

public class InjectionResultIdGenerator extends IdGenerator {
    @PostConstruct
    private void init() {
        ID_PREFIX = "IRS";
        TABLE_NAME = "injection_result";
    }
}