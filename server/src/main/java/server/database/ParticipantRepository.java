package server.database;

import org.springframework.data.jpa.repository.JpaRepository;

import commons.Participant;

public interface ParticipantRepository
        extends JpaRepository<Participant, Long> {}

