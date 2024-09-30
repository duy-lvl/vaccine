package fa.training.frontend.service.endpoint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InjectionScheduleEndpoint {
    @Autowired
    private Endpoint endpoint;

    private static final String INJECTION_SCHEDULES_GET_BY_ID = "injection-schedules.get-by-id";
    private static final String INJECTION_SCHEDULES_UPDATE = "injection-schedules.update";
    private static final String INJECTION_SCHEDULES_CREATE = "injection-schedules.create";
    private static final String INJECTION_SCHEDULES_GET_ALL = "injection-schedules.get-all";
    private static final String VACCINE = "injection-schedules.vaccine.name";

    public String getById(String id) {
        return String.format(endpoint.getUrl(INJECTION_SCHEDULES_GET_BY_ID), id);
    }

    public String add() {
        return endpoint.getUrl(INJECTION_SCHEDULES_CREATE);
    }

    public String update(String id) {
        return String.format(endpoint.getUrl(INJECTION_SCHEDULES_UPDATE), id);
    }

    public String getAll(int page, int size, String search) {
        return String.format(endpoint.getUrl(INJECTION_SCHEDULES_GET_ALL), page, size, search);
    }
    public String vaccine() {

        return endpoint.getUrl(VACCINE);
    }
}
