package fa.training.frontend.service.endpoint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class VaccineEndpoint {
    @Autowired
    private Endpoint endpoint;


    private static final String VACCINE_TYPE = "vaccines.vaccine-type.name";
    private static final String VACCINE_GET_ALL = "vaccines.get-all";
    private static final String VACCINE_ADD = "vaccines.add";
    //for function in injection schedule, take id and name
    private static final String VACCINE_GET_ALL_FOR_INJECTION_SCHEDULE = "vaccines.get-all-for-injection-schedule";
    private static final String VACCINE_MAKE_INACTIVE = "vaccines.make-inactive";

    private static final String VACCINE_GET_BY_ID = "vaccines.get-by-id";
    private static final String VACCINE_UPDATE = "vaccines.update";
    private static final String VACCINE_TEMPLATE = "vaccines.template";
    private static final String VACCINE_IMPORT = "vaccines.import";

    public String getAll(int page, int size, String query) {
        return String.format(endpoint.getUrl(VACCINE_GET_ALL), page, size, query);
    }

    public String getVaccineGetAllForInjectionSchedule() {
        return endpoint.getUrl(VACCINE_GET_ALL_FOR_INJECTION_SCHEDULE);
    }

    public String add() {
        return endpoint.getUrl(VACCINE_ADD);
    }

    public String vaccineType() {
        return endpoint.getUrl(VACCINE_TYPE);
    }
    public String makeInActive() {
        return endpoint.getUrl(VACCINE_MAKE_INACTIVE);
    }

    public String getVaccineByIdForUpdate(String id) {

        return String.format(endpoint.getUrl(VACCINE_GET_BY_ID), id);
    }

    public String update(String id) {
        return String.format(endpoint.getUrl(VACCINE_UPDATE), id);
    }
    public String template() {
        return endpoint.getUrl(VACCINE_TEMPLATE);
    }

    public String importVaccine() {
        return endpoint.getUrl(VACCINE_IMPORT);
    }

}
