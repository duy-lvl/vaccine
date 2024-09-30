package fa.training.backend.exception.common;

import fa.training.backend.exception.CustomException;
import fa.training.backend.util.Message;
import lombok.Getter;

@Getter
public class NotFoundException extends CustomException {
    private String id;

    public NotFoundException(Message description, String id) {
        super(description);
        this.id = id;
    }
}
