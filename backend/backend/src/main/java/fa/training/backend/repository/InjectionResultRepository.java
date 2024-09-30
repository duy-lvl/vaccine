package fa.training.backend.repository;

import fa.training.backend.model.InjectionResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface InjectionResultRepository extends JpaRepository<InjectionResult, String> {
    Page<InjectionResult> findByCustomerFullNameContainsIgnoreCaseOrVaccineNameContainsIgnoreCase(String customerName, String vaccineName, Pageable pageable);

    List<InjectionResult> findByCustomerId(String customerId);
    @Query("""
            SELECT MONTH(ir.injectionDate), COUNT(*) 
            FROM InjectionResult ir 
            WHERE YEAR(ir.injectionDate) = :year 
            GROUP BY MONTH(ir.injectionDate)            
    """)
    List<Integer[]> findByYear(@Param("year") int year);

    @Query("""
            SELECT MONTH(ir.injectionDate), SUM(ir.numberOfInjection) 
            FROM InjectionResult ir 
            WHERE YEAR(ir.injectionDate) = :year 
            GROUP BY MONTH(ir.injectionDate)      
    """)
    List<Integer[]> findVaccineInjectedByYear(@Param("year") int year);

    @Query("""
            SELECT ir.vaccine.name, ir.vaccine.vaccineType.name, 
                    SUM(ir.numberOfInjection), ir.injectionDate, 
                    ir.nextInjectionDate, ir.vaccine.origin
            FROM InjectionResult ir
            WHERE (:nextInjectionDateFrom IS NULL OR :nextInjectionDateTo IS NULL OR
                      (ir.nextInjectionDate >= :nextInjectionDateFrom AND ir.nextInjectionDate <= :nextInjectionDateTo)) 
                  AND (:vaccineTypeId IS NULL OR ir.vaccine.id LIKE CONCAT('%', :vaccineTypeId, '%')) 
                  AND(:origin IS NULL OR ir.vaccine.origin LIKE CONCAT('%', :origin, '%'))
            GROUP BY ir.customer.id
     """)
    Page<Object[]> findByNextInjectionDateBetweenAndVaccineTypeIdAndOrigin(
            @Param("nextInjectionDateFrom") LocalDate nextInjectionDateFrom,
            @Param("nextInjectionDateTo") LocalDate nextInjectionDateTo,
            @Param("vaccineTypeId") String vaccineTypeId,
            @Param("origin") String origin,
            Pageable pageable
    );
    @Query("""
            SELECT ir.customer.fullName, ir.customer.dateOfBirth, 
                    ir.customer.address, ir.customer.identityCard, SUM(ir.numberOfInjection)
            FROM InjectionResult ir
            WHERE (:dobFrom IS NULL OR :dobTo IS NULL OR 
                      (ir.customer.dateOfBirth >= :dobFrom AND ir.customer.dateOfBirth <= :dobTo)) 
                  AND (:fullName IS NULL OR ir.customer.fullName LIKE CONCAT('%', :fullName, '%')) 
                  AND (:address IS NULL OR ir.customer.address LIKE CONCAT('%', :address, '%'))
            GROUP BY ir.customer.id
     """)
    Page<Object[]> findByDateOfBirthBetweenAndFullNameAndAddress(
            @Param("dobFrom") LocalDate dobFrom,
            @Param("dobTo") LocalDate dobTo,
            @Param("fullName") String fullName,
            @Param("address") String address,
            Pageable pageable
    );

    @Query("""
            SELECT ir.vaccine.vaccineType.name, ir.vaccine.name, 
                    ir.customer.fullName, ir.injectionDate, ir.numberOfInjection
            FROM InjectionResult ir
            WHERE (:injectionDateFrom IS NULL OR :injectionDateTo IS NULL OR 
                      (ir.injectionDate >= :injectionDateFrom AND ir.injectionDate <= :injectionDateTo)) 
                  AND (:vaccineTypeId IS NULL OR :vaccineTypeId LIKE '' OR ir.vaccine.vaccineType.id = :vaccineTypeId) 
                  AND (:vaccineId IS NULL OR :vaccineId LIKE '' OR ir.vaccine.id LIKE :vaccineId)
     """)
    Page<Object[]> findByInjectionDateBetweenOrVaccineIdOrVaccineVaccineTypeId(
            @Param("injectionDateFrom") LocalDate injectionDateFrom,
            @Param("injectionDateTo") LocalDate injectionDateTo,
            @Param("vaccineTypeId") String vaccineTypeId,
            @Param("vaccineId") String vaccineId,
            Pageable pageable
    );

}
