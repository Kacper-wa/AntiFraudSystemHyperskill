package antifraud.repository;

import antifraud.entity.CardLimit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CardLimitRepository extends JpaRepository<CardLimit, Long> {
    Optional<CardLimit> findByNumber(String number);
}
