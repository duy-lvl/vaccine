package fa.training.frontend.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Response <T>{
    private int code;
    private String description;
    private Object data;

    public boolean isSuccessful() {
        return code / 100 == 2;
    }
}