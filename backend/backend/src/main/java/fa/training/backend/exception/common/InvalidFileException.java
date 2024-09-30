package fa.training.backend.exception.common;

import fa.training.backend.exception.CustomException;
import fa.training.backend.util.Message;
import lombok.Getter;

@Getter
public class InvalidFileException extends CustomException {
    public InvalidFileException(Message description) {
        super(description);
    }
}
