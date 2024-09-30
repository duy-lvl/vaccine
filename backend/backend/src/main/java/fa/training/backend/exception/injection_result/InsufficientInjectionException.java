package fa.training.backend.exception.injection_result;

import fa.training.backend.exception.CustomException;
import fa.training.backend.util.Message;

public class InsufficientInjectionException extends CustomException {

    public InsufficientInjectionException(Message description) {
        super(description);
    }
}
