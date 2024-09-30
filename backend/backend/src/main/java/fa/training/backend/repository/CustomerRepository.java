package fa.training.backend.repository;

import fa.training.backend.model.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, String> {
     Optional<Customer> findByUsername(String username);
     Page<Customer> findByFullNameContainsIgnoreCase(Pageable pageable, String search);
     boolean existsByIdentityCard(String identityCard);
     boolean existsByEmail(String email);
     boolean existsByUsername(String username);
     boolean existsByPhone(String phone);

}
