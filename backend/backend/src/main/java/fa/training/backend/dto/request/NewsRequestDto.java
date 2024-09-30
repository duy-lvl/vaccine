package fa.training.backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NewsRequestDto {
    private String id;
    private String content;
    private String preview;
    private String title;
    private String newsTypeId;
}