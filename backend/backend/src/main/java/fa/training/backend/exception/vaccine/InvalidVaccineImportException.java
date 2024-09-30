package fa.training.backend.exception.vaccine;

import fa.training.backend.exception.CustomException;
import fa.training.backend.util.Message;
import lombok.Getter;

@Getter
public class InvalidVaccineImportException extends CustomException {
    private String errorData;
    public InvalidVaccineImportException(Message description, String errorData) {
        super(description);
        this.errorData = errorData;
    }
}
