package antifraud.repository;

import antifraud.entity.SuspiciousIP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SuspiciousIPRepository extends JpaRepository<SuspiciousIP, Long> {
    Optional<SuspiciousIP> findByIp(String ip);
    boolean existsByIp(String ip);
}
