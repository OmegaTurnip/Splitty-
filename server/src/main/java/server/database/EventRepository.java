package server.database;

import org.springframework.data.jpa.repository.JpaRepository;

import commons.Event;

import java.util.List;

public interface EventRepository
        extends JpaRepository<Event, Long> {

    /**
     * Finds all the events matching the list of invite codes
     * @param inviteCodes The list of invite codes
     * @return The list of events
     */
    List<Event> findAllByInviteCodeIsIn(List<String> inviteCodes);
}
