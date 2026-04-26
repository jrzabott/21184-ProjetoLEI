package pt.uab.musicaltrainer.dao;

import pt.uab.musicaltrainer.dto.SessionRecord;
import java.util.Optional;
import java.util.List;

/**
 * Data Access Object for Session entity.
 * Contract for session persistence operations.
 */
public interface SessionDao {
    /**
     * Save a new session to database.
     * @param session SessionRecord to save
     * @return saved SessionRecord with generated ID
     */
    SessionRecord save(SessionRecord session);

    /**
     * Find session by ID.
     * @param id session ID
     * @return Optional containing SessionRecord if found
     */
    Optional<SessionRecord> findById(Long id);

    /**
     * Update existing session.
     * @param session SessionRecord with updated values
     * @return updated SessionRecord
     */
    SessionRecord update(SessionRecord session);

    /**
     * Get all sessions.
     * @return List of all SessionRecords
     */
    List<SessionRecord> findAll();
}
