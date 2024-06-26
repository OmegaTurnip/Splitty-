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
    List<Event> findByInviteCodeIn(List<String> inviteCodes);

    /**
     * Finds an event by its invite code
     * @param inviteCode The invite code
     * @return The event
     */
    Event findByInviteCode(String inviteCode);
}
