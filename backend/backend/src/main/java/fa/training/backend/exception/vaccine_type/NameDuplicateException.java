package fa.training.backend.exception.vaccine_type;

import fa.training.backend.exception.CustomException;
import fa.training.backend.util.Message;

public class NameDuplicateException extends CustomException {

    public NameDuplicateException(Message description) {
        super(description);
    }
}
