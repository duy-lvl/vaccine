package fa.training.backend.generator;

import jakarta.annotation.PostConstruct;

public class NewsTypeIdGenerator extends IdGenerator {
    @PostConstruct
    private void init() {
        ID_PREFIX = "NTP";
        TABLE_NAME = "news_type";
    }
}