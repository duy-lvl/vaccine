package fa.training.frontend.dto.request.news;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewsAddRequest {
    @NotNull(message = "Title must not be null")
    @NotBlank(message = "Title must not be blank")
    private String title;
    @NotBlank(message = "Content must not be blank")
    @NotNull(message = "Content must not be null")
    private String content;
    @NotBlank(message = "Preview must not be blank")
    @NotNull(message = "Preview must not be null")
    private String preview;
    @NotBlank(message = "News type must not be blank")
    @NotNull(message = "News type must not be null")
    private String newsTypeId;
}
