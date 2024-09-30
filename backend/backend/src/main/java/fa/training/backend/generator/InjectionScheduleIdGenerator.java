package fa.training.backend.generator;

import jakarta.annotation.PostConstruct;

public class InjectionScheduleIdGenerator extends IdGenerator {
    @PostConstruct
    private void init() {
        ID_PREFIX = "ISD";
        TABLE_NAME = "injection_schedule";
    }
}