package fa.training.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Month;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MonthlyData {
    private Month month;
    private int amount;
}