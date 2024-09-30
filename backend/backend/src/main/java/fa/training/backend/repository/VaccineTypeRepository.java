package fa.training.backend.repository;

import fa.training.backend.model.VaccineType;
import fa.training.backend.util.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VaccineTypeRepository extends JpaRepository<VaccineType, String> {
    List<VaccineType> findByStatus(Status status);
    List<VaccineType> findAllById(Iterable<String> ids);
    Page<VaccineType> findByCodeContainingOrNameContainingOrDescriptionContaining(String code, String name, String description, Pageable pageable);
    Boolean existsByCode(String code);
    Boolean existsByName(String name);

}
