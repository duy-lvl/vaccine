package fa.training.frontend.service.endpoint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InjectionResultEndpoint {
    @Autowired
    private Endpoint endpoint;
    private final String INJECTION_RESULT_ADD = "injection-results.add";
    private final String INJECTION_RESULT_UPDATE = "injection-results.update";
    private final String INJECTION_RESULT_GET_BY_ID = "injection-results.get-by-id";
    private final String CUSTOMER_NAME = "injection-results.customer.name";

    private final String INJECTION_RESULT_GET_ALL = "injection-results.get-all";
    private final String INJECTION_RESULT_DELETE_BY_ID = "injection-results.delete-by-id";
    private final String INJECTION_RESULT_GET_BY_REQUEST = "injection-results.get-page-by-date-and-vaccineTypeId";

    public String add() {
        return endpoint.getUrl(INJECTION_RESULT_ADD);
    }

    public String update(String id) {
        return String.format(endpoint.getUrl(INJECTION_RESULT_UPDATE), id);
    }

    public String getById(String id) {
        return String.format(endpoint.getUrl(INJECTION_RESULT_GET_BY_ID), id);
    }

    public String customerName() {
        return endpoint.getUrl(CUSTOMER_NAME);
    }


    public String getAll(int page, int size, String search) {
        return String.format(endpoint.getUrl(INJECTION_RESULT_GET_ALL), page, size, search);
    }

    public String delete(String idList) {
        return String.format(endpoint.getUrl(INJECTION_RESULT_DELETE_BY_ID), idList);
    }

    public String getPageScheduleByRequest(int page, int size, String query) {
        return String.format(endpoint.getUrl(INJECTION_RESULT_GET_BY_REQUEST), page, size, query);
    }
}

