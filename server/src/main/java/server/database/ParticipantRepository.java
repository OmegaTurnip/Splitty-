package server.database;

import commons.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import commons.Participant;

import java.util.List;
import java.util.Optional;

public interface ParticipantRepository
        extends JpaRepository<Participant, Long> {
    /**
     * Finds the participant with the id
     * @param id The id
     * @return The participant
     */
    Optional<Participant> findByParticipantId(Long id);
}

