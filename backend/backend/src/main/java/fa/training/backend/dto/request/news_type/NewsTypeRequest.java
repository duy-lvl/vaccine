package fa.training.backend.dto.request.news_type;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewsTypeRequest {
    @Size(max = 10, message = "Length of description's news type must not exceed 10 characters!")
    private String description;

    @NotNull(message = "Name's news type must not be null!")
    @NotBlank(message = "Name's news type must not be blank!")
    @Size(max = 50, message = "Length of name's news type must not exceed 50 characters! ")
    private String name;
}
