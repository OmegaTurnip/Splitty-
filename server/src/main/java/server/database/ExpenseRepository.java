package server.database;

import commons.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    /**
     * Finds expense by the name
     * @param expenseName The expense name
     * @return The expense.
     */
    Expense findExpenseByExpenseName(String expenseName);




}
