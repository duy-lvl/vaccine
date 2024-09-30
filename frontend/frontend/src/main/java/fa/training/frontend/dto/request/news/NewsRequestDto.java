package fa.training.frontend.dto.request.news;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewsRequestDto {

    @NotNull(message = "Id must not be null")
    @NotBlank(message = "Id must not be blank")
    private String id;

    @NotNull(message = "Title must not be null")
    @NotBlank(message = "Title must not be blank")
    private String title;

    @NotNull(message = "Content must not be null")
    @NotBlank(message = "Content must not be blank")
    private String content;

    @NotNull(message = "Preview must not be null")
    @NotBlank(message = "Preview must not be blank")
    private String preview;

    @NotNull(message = "News type must not be null")
    @NotBlank(message = "News type must not be blank")
    private String newsTypeId;
}
