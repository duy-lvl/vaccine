package fa.training.backend.generator;

import jakarta.annotation.PostConstruct;

public class NewsIdGenerator extends IdGenerator {
    @PostConstruct
    private void init() {
        ID_PREFIX = "NEW";
        TABLE_NAME = "news";
    }
}