package fa.training.frontend.service.endpoint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EmployeeEndpoint {

    @Autowired
    private Endpoint endpoint;

    private static final String EMPLOYEES_GET_ALL = "employees.get-all";
    private static final String EMPLOYEES_GET_BY_ID = "employees.get-by-id";
    private static final String EMPLOYEES_ADD = "employees.add";
    private static final String EMPLOYEES_UPDATE_BY_ID = "employees.update-by-id";
    private static final String EMPLOYEES_DELETE_BY_ID = "employees.delete-by-id";
    private static final String EMPLOYEES_UPDATE_AVATAR_BY_ID = "employees.update-avatar-by-id";


    public String getAll(int page, int size, String search) {
        return String.format(endpoint.getUrl(EMPLOYEES_GET_ALL), page, size, search);
    }

    public String add() {
        return endpoint.getUrl(EMPLOYEES_ADD);
    }

    public String getById(String id) {
        return String.format(endpoint.getUrl(EMPLOYEES_GET_BY_ID), id);
    }

    public String update(String id) {
        return String.format(endpoint.getUrl(EMPLOYEES_UPDATE_BY_ID), id);
    }

    public String updateAvatarById(String id) {
        return String.format(endpoint.getUrl(EMPLOYEES_UPDATE_AVATAR_BY_ID), id);
    }

    public String delete(String idList) {
        return String.format(endpoint.getUrl(EMPLOYEES_DELETE_BY_ID), idList);
    }
}
