package antifraud.repository;

import antifraud.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findAllByNumber(String number);
    List<Transaction> findAllByNumberAndDateBetween(String cardNumber, LocalDateTime from, LocalDateTime to);
}
