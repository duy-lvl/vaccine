package fa.training.frontend.dto.request.vaccine;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VaccineReportRequest {
    private String vaccineTypeId;
    private String nextInjectionFrom;
    private String nextInjectionTo;
    private String origin;
    private int page;
    private int size;

    public int getPage() {
        return page > 1 ? page : 1;
    }
    public int getSize() {
        return size > 0 ? size : 5;
    }

    public String getVaccineTypeId() {
        return vaccineTypeId;
    }

    public String getNextInjectionFrom() {
        return nextInjectionFrom;
    }

    public String getNextInjectionTo() {
        return nextInjectionTo;
    }

    public String getOrigin() {
        return origin;
    }
}