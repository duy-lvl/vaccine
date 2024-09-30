package fa.training.backend.dto.request.vaccine;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VaccineReportRequest {
    private String vaccineTypeId;
    private LocalDate nextInjectionFrom;
    private LocalDate nextInjectionTo;
    private String origin;
    private int page;
    private int size;

    public Pageable getPageable() {
        return PageRequest.of(getPage() - 1, getSize());
    }
    public int getPage() {
        return page > 1 ? page : 1;
    }
    public int getSize() {
        return size > 0 ? size : 5;
    }
}
