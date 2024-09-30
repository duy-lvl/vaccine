package fa.training.backend.exception;

import fa.training.backend.util.Message;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private final Message description;

    public CustomException(Message description) {
        super(description.getMessage());
        this.description = description;
    }
}
