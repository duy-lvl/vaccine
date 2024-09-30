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
public class NewsType {
    @Id
    @Column(length = 36)
    @GeneratedValue(generator = "news-type-id-generator")
    @GenericGenerator(name = "news-type-id-generator", strategy = "fa.training.backend.generator.NewsTypeIdGenerator")
    private String id;
    @Column(length = 10)
    private String description;
    @Column(length = 50)
    private String name;
    private boolean isDeleted = false;

    public NewsType(String id, String description, String name) {
        this.id = id;
        this.description = description;
        this.name = name;
        this.isDeleted = false;
    }
}
