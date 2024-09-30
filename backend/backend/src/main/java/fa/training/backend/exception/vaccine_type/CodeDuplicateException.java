package fa.training.backend.exception.vaccine_type;

import fa.training.backend.exception.CustomException;
import fa.training.backend.util.Message;

public class CodeDuplicateException extends CustomException {

    public CodeDuplicateException(Message description) {
        super(description);
    }
}
