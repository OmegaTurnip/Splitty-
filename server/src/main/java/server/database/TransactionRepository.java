package server.database;

import commons.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository
        extends JpaRepository<Transaction, Long> {

    /**
     * Finds transaction by the name
     * @param transactionName The transaction name
     * @return The transaction.
     */
    Transaction findTransactionByName(String transactionName);




}
