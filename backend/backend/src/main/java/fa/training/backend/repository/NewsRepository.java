package fa.training.backend.repository;

import fa.training.backend.model.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsRepository extends JpaRepository<News, String> {
    Page<News> findByTitleContainsIgnoreCaseOrContentContainsIgnoreCase(String title, String content, Pageable pageable);

    List<News> findByNewsTypeIdIn(List<String> ids);
}
