package fa.training.frontend.dto.response.customer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomerReportDto {
    private String fullName;
    private LocalDate dateOfBirth;
    private String address;
    private String identityCard;
    private long numberOfInjection;
}
