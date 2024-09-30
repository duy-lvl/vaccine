package fa.training.backend.exception.common;

import fa.training.backend.exception.CustomException;
import fa.training.backend.util.Message;

public class UsernameDuplicateException extends CustomException {

    public UsernameDuplicateException(Message description) {
        super(description);
    }
}
