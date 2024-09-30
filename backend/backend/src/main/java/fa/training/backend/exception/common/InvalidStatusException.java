package fa.training.backend.exception.common;

import fa.training.backend.exception.CustomException;
import fa.training.backend.util.Message;

public class InvalidStatusException extends CustomException {

    public InvalidStatusException(Message description) {
        super(description);
    }
}
