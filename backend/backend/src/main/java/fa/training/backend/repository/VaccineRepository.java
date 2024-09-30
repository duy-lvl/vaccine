package fa.training.backend.repository;

import fa.training.backend.model.Vaccine;
import fa.training.backend.util.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VaccineRepository extends JpaRepository<Vaccine, String> {
    Optional<Vaccine> findByName(String name);

    Page<Vaccine> findByNameContainsIgnoreCase(Pageable pageable, String query);

    List<Vaccine> findAllByStatus(Status status);

    List<Vaccine> findByVaccineTypeIdAndStatus(String id, Status status);
    List<Vaccine> findByVaccineTypeId(String vaccineTypeId);
}
