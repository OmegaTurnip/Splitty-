package server.database;

import commons.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    Expense findExpenseByExpenseName(String expenseName);




}
