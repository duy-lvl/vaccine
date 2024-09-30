package fa.training.backend.dto.request.news;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NewsRequest {
    @NotNull(message = "Content is required")
    @NotBlank(message = "Content is required")
    private String content;

    @NotNull(message = "Preview is required")
    @NotBlank(message = "Preview is required")
    private String preview;

    @NotNull(message = "Title is required")
    @NotBlank(message = "Title is required")
    private String title;

    @NotNull(message = "News type is required")
    @NotBlank(message = "News type is required")
    private String newsTypeId;
}
