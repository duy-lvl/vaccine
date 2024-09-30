package fa.training.backend.dto.response;

import fa.training.backend.model.NewsType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NewsDto {
    private String id;
    private String content;
    private String preview;
    private String title;
    private String newsTypeId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NewsDto newsDto)) return false;
        return Objects.equals(id, newsDto.id) && Objects.equals(content, newsDto.content) && Objects.equals(preview, newsDto.preview) && Objects.equals(title, newsDto.title) && Objects.equals(newsTypeId, newsDto.newsTypeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, content, preview, title, newsTypeId);
    }
}
