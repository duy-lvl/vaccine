package fa.training.frontend.service.endpoint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class VaccineTypeEndpoint {
    @Autowired
    private Endpoint endpoint;

    private static final String VACCINE_TYPE_GET_ALL = "vaccine-types.get-all";
    private static final String VACCINE_TYPE_GET_BY_ID = "vaccine-types.get-by-id";
    private static final String VACCINE_TYPE_ADD = "vaccine-types.add";
    private static final String VACCINE_TYPE_UPDATE = "vaccine-types.update";
    private static final String VACCINE_TYPE_DELETE = "vaccine-types.delete ";
    private static final String VACCINE_TYPE_MAKE_INACTIVE = "vaccine-types.make-inactive";
    private static final String VACCINE_TYPE_MAKE_ACTIVE = "vaccine-types.make-active";
    private static final String VACCINE_TYPE_UPDATE_IMAGE = "vaccine-types.update-image";
    private static final String VACCINE_TYPE_GET_NAME = "vaccine-type.get-name";

    public String getAll(int page, int size, String query) {
        return String.format(endpoint.getUrl(VACCINE_TYPE_GET_ALL), page, size, query);
    }
    public String getVaccineTypeName(boolean isActive) {
        return String.format(endpoint.getUrl(VACCINE_TYPE_GET_NAME), isActive);
    }
    public String add() {
        return endpoint.getUrl(VACCINE_TYPE_ADD);
    }

    public String getById(String id) {
        return String.format(endpoint.getUrl(VACCINE_TYPE_GET_BY_ID), id);
    }

    public String update(String id) {
        return String.format(endpoint.getUrl(VACCINE_TYPE_UPDATE), id);
    }

    public String delete(String id) {
        return String.format(endpoint.getUrl(VACCINE_TYPE_DELETE), id);
    }

    public String makeInActive() {
        return endpoint.getUrl(VACCINE_TYPE_MAKE_INACTIVE);
    }

    public String makeActive() {
        return endpoint.getUrl(VACCINE_TYPE_MAKE_ACTIVE);
    }

    public String updateImage(String id) {
        return String.format(endpoint.getUrl(VACCINE_TYPE_UPDATE_IMAGE), id);
    }
}
