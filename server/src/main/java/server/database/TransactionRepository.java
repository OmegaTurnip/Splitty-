package server.database;

import commons.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TransactionRepository
        extends JpaRepository<Transaction, Long> {

    /**
     * Finds a transaction by its transactionId
     * @param id the transactionId
     * @return the
     */
    Optional<Transaction> findByTransactionId(Long id);


}
