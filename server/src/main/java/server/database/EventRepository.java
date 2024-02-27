package server.database;

import commons.Event;
import commons.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    Event findEventByEventName(String eventName);
}
