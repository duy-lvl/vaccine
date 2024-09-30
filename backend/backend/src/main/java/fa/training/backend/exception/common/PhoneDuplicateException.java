package fa.training.backend.exception.common;

import fa.training.backend.exception.CustomException;
import fa.training.backend.util.Message;

public class PhoneDuplicateException extends CustomException {

    public PhoneDuplicateException(Message description) {
        super(description);
    }
}
