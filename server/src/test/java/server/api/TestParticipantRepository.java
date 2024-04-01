package server.api;

import commons.Participant;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import server.database.ParticipantRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class TestParticipantRepository implements ParticipantRepository {

    private List<Participant> added = new ArrayList<>();


    /**
     * .
     */
    @Override
    public void flush() {
        // TODO Auto-generated method stub
    }

    /**
     * .
     * @param entity entity to be saved.
     *               Must not be {@literal null}.
     * @return .
     * @param <S> .
     */
    @Override
    public <S extends Participant> S saveAndFlush(S entity) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * .
     * @param entities entities to be saved.
     *                 Must not be {@literal null}.
     * @return .
     * @param <S> .
     */
    @Override
    public <S extends Participant> List<S> saveAllAndFlush(
            Iterable<S> entities) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * .
     * @param entities entities to be deleted.
     *                 Must not be {@literal null}.
     */
    @Override
    public void deleteAllInBatch(Iterable<Participant> entities) {
        // TODO Auto-generated method stub

    }

    /**
     * .
     * @param longs the ids of the entities to be deleted.
     *              Must not be {@literal null}.
     */
    @Override
    public void deleteAllByIdInBatch(Iterable<Long> longs) {
        // TODO Auto-generated method stub

    }

    /**
     * .
     */
    @Override
    public void deleteAllInBatch() {
        // TODO Auto-generated method stub

    }

    /**
     * .
     * @param aLong must not be {@literal null}.
     * @return .
     */
    @Override
    public Participant getOne(Long aLong) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * .
     * @param aLong must not be {@literal null}.
     * @return .
     */
    @Override
    public Participant getById(Long aLong) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * .
     * @param aLong must not be {@literal null}.
     * @return .
     */
    @Override
    public Participant getReferenceById(Long aLong) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * .
     * @param example must not be {@literal null}.
     * @return .
     * @param <S> .
     */
    @Override
    public <S extends Participant> Optional<S> findOne(Example<S> example) {
        // TODO Auto-generated method stub
        return Optional.empty();
    }

    /**
     * .
     * @param example must not be {@literal null}.
     * @return .
     * @param <S> .
     */
    @Override
    public <S extends Participant> List<S> findAll(Example<S> example) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * .
     * @param example must not be {@literal null}.
     * @param sort the {@link Sort} specification to sort the results by,
     *             may be {@link Sort#unsorted()}, must not be
     *          {@literal null}.
     * @return .
     * @param <S> .
     */
    @Override
    public <S extends Participant> List<S> findAll(Example<S> example,
                                                   Sort sort) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * .
     * @param example must not be {@literal null}.
     * @param pageable the pageable to request a paged result,
     *                 can be {@link Pageable#unpaged()}, must not be
     *          {@literal null}.
     * @return .
     * @param <S> .
     */
    @Override
    public <S extends Participant> Page<S> findAll(Example<S> example,
                                                   Pageable pageable) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * .
     * @param example the {@link Example} to count instances for.
     *                Must not be {@literal null}.
     * @return .
     * @param <S> .
     */
    @Override
    public <S extends Participant> long count(Example<S> example) {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * .
     * @param example the {@link Example} to use for the existence check.
     *                Must not be {@literal null}.
     * @return .
     * @param <S> .
     */
    @Override
    public <S extends Participant> boolean exists(Example<S> example) {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * .
     * @param example must not be {@literal null}.
     * @param queryFunction the query function defining projection,
     *                      sorting, and the result type
     * @return .
     * @param <S> .
     * @param <R> .
     */
    @Override
    public <S extends Participant, R> R findBy(
            Example<S> example,
            Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * .
     * @param entity must not be {@literal null}.
     * @return .
     * @param <S> .
     */
    @Override
    public <S extends Participant> S save(S entity) {
        added.add(entity);
        return entity;
    }

    /**
     * .
     * @param entities must not be {@literal null}
     *                 nor must it contain {@literal null}.
     * @return .
     * @param <S> .
     */
    @Override
    public <S extends Participant> List<S> saveAll(Iterable<S> entities) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * .
     * @param aLong must not be {@literal null}.
     * @return .
     */
    @Override
    public Optional<Participant> findById(Long aLong) {
        Optional<Participant> found = Optional.empty();
        for (Participant p : added) {
            if (p.getParticipantId().equals(aLong)) {
                found = Optional.of(p);
                break;
            }
        }
        return found;
    }

    /**
     * .
     * @param aLong must not be {@literal null}.
     * @return .
     */
    @Override
    public boolean existsById(Long aLong) {
        boolean condition = false;
        for (Participant p : added) {
            if (p.getParticipantId().equals(aLong)) {
                condition = true;
                return condition;
            }
        }
        return condition;
    }

    /**
     * .
     * @return .
     */
    @Override
    public List<Participant> findAll() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * .
     * @param longs must not be {@literal null}
     *              nor contain any {@literal null} values.
     * @return .
     */
    @Override
    public List<Participant> findAllById(Iterable<Long> longs) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * .
     * @return .
     */
    @Override
    public long count() {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * .
     * @param aLong must not be {@literal null}.
     */
    @Override
    public void deleteById(Long aLong) {
        for (Participant p : added) {
            if (p.getParticipantId().equals(aLong)) {
                added.remove(p);
                return;
            }
        }

    }

    /**
     * .
     * @param entity must not be {@literal null}.
     */
    @Override
    public void delete(Participant entity) {
        // TODO Auto-generated method stub

    }

    /**
     * .
     * @param longs must not be {@literal null}.
     *              Must not contain {@literal null} elements.
     */
    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {
        // TODO Auto-generated method stub

    }

    /**
     * .
     * @param entities must not be {@literal null}.
     *                 Must not contain {@literal null} elements.
     */
    @Override
    public void deleteAll(Iterable<? extends Participant> entities) {
        // TODO Auto-generated method stub

    }

    /**
     * .
     */
    @Override
    public void deleteAll() {
        // TODO Auto-generated method stub

    }

    /**
     * .
     * @param sort the {@link Sort} specification to sort the results by,
     *             can be {@link Sort#unsorted()}, must not be
     *          {@literal null}.
     * @return .
     */
    @Override
    public List<Participant> findAll(Sort sort) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * .
     * @param pageable the pageable to request a paged result,
     *                 can be {@link Pageable#unpaged()}, must not be
     *          {@literal null}.
     * @return .
     */
    @Override
    public Page<Participant> findAll(Pageable pageable) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<Participant> findByParticipantId(Long id) {
        for (Participant participant : added) {
            if (participant.getParticipantId().equals(id)) {
                return Optional.of(participant);
            }
        }
        return Optional.empty();
    }
}
