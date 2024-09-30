package fa.training.backend.repository;

import fa.training.backend.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, String> {

    Optional<Employee> findByUsername(String username);

    @Query("SELECT e FROM Employee e WHERE LOWER(e.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(e.id) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Employee> findByNameContainsIgnoreCaseOrIdContainsIgnoreCase(Pageable pageable, String search);

    boolean existsByPhone (String phone);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);

}
