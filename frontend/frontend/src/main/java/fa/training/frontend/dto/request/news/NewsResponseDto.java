package fa.training.frontend.dto.request.news;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewsResponseDto {
    @NotBlank(message = "Id must not be blank")
    @NotNull(message = "Id must not be null")
    private String id;
    @NotBlank(message = "Content must not be blank")
    @NotNull(message = "Content must not be null")
    private String content;
    @NotBlank(message = "Preview must not be blank")
    @NotNull(message = "Preview must not be null")
    private String preview;
    @NotBlank(message = "Title must not be blank")
    @NotNull(message = "Title must not be null")
    private String title;
}
