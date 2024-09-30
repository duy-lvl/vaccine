package fa.training.backend.exception.common;

import fa.training.backend.exception.CustomException;
import fa.training.backend.util.Message;

public class EmailDuplicateException extends CustomException {

    public EmailDuplicateException(Message description) {
        super(description);
    }
}
