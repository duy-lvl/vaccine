package fa.training.backend.dto.request.customer;

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
public class CustomerReportRequest {
    private LocalDate dobFrom;
    private LocalDate dobTo;
    private String fullName;
    private String address;
    private int page;
    private int size;

    public String fullName() {
        return fullName == null || fullName.isBlank() ? null : fullName;
    }

    public String address() {
        return address == null || address.isBlank() ? null : address;
    }

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
