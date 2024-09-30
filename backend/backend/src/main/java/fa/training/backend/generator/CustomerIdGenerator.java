package fa.training.backend.generator;

import jakarta.annotation.PostConstruct;

public class CustomerIdGenerator extends IdGenerator {
    @PostConstruct
    private void init() {
        ID_PREFIX = "CUS";
        TABLE_NAME = "customer";
    }
}
