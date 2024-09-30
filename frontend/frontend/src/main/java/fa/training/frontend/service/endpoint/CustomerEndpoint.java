package fa.training.frontend.service.endpoint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CustomerEndpoint {

    private static final String CUSTOMERS_GET_ALL = "customers.get-all";
    private static final String CUSTOMERS_GET_BY_ID = "customers.get-by-id";
    private static final String CUSTOMERS_ADD = "customers.add";
    private static final String CUSTOMERS_UPDATE = "customers.update";
    private static final String CUSTOMERS_DELETE = "customers.delete";

    private final Endpoint endpoint;

    @Autowired
    public CustomerEndpoint(Endpoint endpoint) {
        this.endpoint = endpoint;
    }

    public String getAll(int page, int size, String query) {
        return String.format(endpoint.getUrl(CUSTOMERS_GET_ALL), page, size, query);
    }

    public String add() {
        return endpoint.getUrl(CUSTOMERS_ADD);
    }

    public String getById(String id) {
        return String.format(endpoint.getUrl(CUSTOMERS_GET_BY_ID), id);
    }

    public String update(String id) {
        return String.format(endpoint.getUrl(CUSTOMERS_UPDATE), id);
    }

    public String delete(String id) {
        return String.format(endpoint.getUrl(CUSTOMERS_DELETE), id);
    }
}
