package fa.training.backend.exception.customer;

import fa.training.backend.exception.CustomException;
import fa.training.backend.util.Message;

public class IdentityCardDuplicateException extends CustomException {

    public IdentityCardDuplicateException(Message description) {
        super(description);
    }
}
