package fa.training.backend.exception.common;

import fa.training.backend.exception.CustomException;
import fa.training.backend.util.Message;
import lombok.Getter;

import java.util.List;

@Getter
public class InvalidListException extends CustomException {
    private List<String> ids;

    public InvalidListException(Message description, List<String> ids) {
        super(description);
        this.ids = ids;
    }
}
