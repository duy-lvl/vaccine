package fa.training.backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Where;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Where(clause = "is_deleted = false")
public class News {
    @Id
    @Column(length = 36)
    @GeneratedValue(generator = "news-id-generator")
    @GenericGenerator(name = "news-id-generator", strategy = "fa.training.backend.generator.NewsIdGenerator")
    private String id;
    @Column(length = 4000)
    private String content;
    @Column(length = 1000)
    private String preview;
    @Column(length = 300)
    private String title;
    @ManyToOne
    private NewsType newsType;
    private boolean isDeleted = false;

    public News(String id, String content, String preview, String title, NewsType newsType) {
        this.id = id;
        this.content = content;
        this.preview = preview;
        this.title = title;
        this.newsType = newsType;
        this.isDeleted = false;
    }
}
