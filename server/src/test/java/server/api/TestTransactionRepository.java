package server.api;


import commons.Transaction;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import server.database.TransactionRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class TestTransactionRepository implements TransactionRepository {

    private List<Transaction> added = new ArrayList<>();
    /**
     * @param transactionName The transaction name
     * @return
     */
    @Override
    public Transaction findTransactionByTransactionName(
            String transactionName) {

        return null;
    }

    /**
     *
     */
    @Override
    public void flush() {

    }

    /**
     * @param entity
     * @param <S>
     * @return
     */
    @Override
    public <S extends Transaction> S saveAndFlush(S entity) {
        return null;
    }

    /**
     * @param entities
     * @param <S>
     * @return
     */
    @Override
    public <S extends Transaction> List<S> saveAllAndFlush(
            Iterable<S> entities) {

        return null;
    }

    /**
     * @param entities
     */
    @Override
    public void deleteAllInBatch(Iterable<Transaction> entities) {

    }

    /**
     * @param longs
     */
    @Override
    public void deleteAllByIdInBatch(Iterable<Long> longs) {

    }

    /**
     *
     */
    @Override
    public void deleteAllInBatch() {

    }

    /**
     * @param aLong
     * @return
     */
    @Override
    public Transaction getOne(Long aLong) {

        return null;
    }

    /**
     * @param aLong
     * @return
     */
    @Override
    public Transaction getById(Long aLong) {
        return null;
    }

    /**
     * @param aLong
     * @return
     */
    @Override
    public Transaction getReferenceById(Long aLong) {
        return null;
    }

    /**
     * @param example
     * @param <S>
     * @return
     */
    @Override
    public <S extends Transaction> Optional<S> findOne(Example<S> example) {

        return Optional.empty();
    }

    /**
     * @param example
     * @param <S>
     * @return
     */
    @Override
    public <S extends Transaction> List<S> findAll(Example<S> example) {

        return null;
    }

    /**
     * @param example
     * @param sort
     * @param <S>
     * @return
     */
    @Override
    public <S extends Transaction> List<S> findAll(Example<S> example, Sort sort) {

        return null;
    }

    /**
     * @param example
     * @param pageable
     * @param <S>
     * @return
     */
    @Override
    public <S extends Transaction> Page<S> findAll(
            Example<S> example, Pageable pageable) {
        return null;
    }

    /**
     * @param example
     * @param <S>
     * @return
     */
    @Override
    public <S extends Transaction> long count(Example<S> example) {
        return 0;
    }

    /**
     * @param example
     * @param <S>
     * @return
     */
    @Override
    public <S extends Transaction> boolean exists(Example<S> example) {
        return false;
    }

    /**
     * @param example
     * @param queryFunction
     * @param <S>
     * @param <R>
     * @return
     */
    @Override
    public <S extends Transaction, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    /**
     * @param entity
     * @param <S>
     * @return
     */
    @Override
    public <S extends Transaction> S save(S entity) {
        added.add(entity);
        return entity;
    }

    /**
     * @param entities
     * @param <S>
     * @return
     */
    @Override
    public <S extends Transaction> List<S> saveAll(Iterable<S> entities) {
        return null;
    }

    /**
     * @return
     */
    @Override
    public List<Transaction> findAll() {
        return null;
    }

    /**
     * @param longs
     * @return
     */
    @Override
    public List<Transaction> findAllById(Iterable<Long> longs) {
        return null;
    }

    /**
     * @return
     */
    @Override
    public long count() {
        return 0;
    }

    /**
     * @param aLong
     */
    @Override
    public void deleteById(Long aLong) {
        for (Transaction t : added) {
            if (t.getId().equals(aLong)) {
                added.remove(t);
                return;
            }
        }


    }

    /**
     * @param entity
     */
    @Override
    public void delete(Transaction entity) {

    }

    /**
     * @param longs
     */
    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {

    }

    /**
     * @param entities
     */
    @Override
    public void deleteAll(Iterable<? extends Transaction> entities) {

    }

    /**
     *
     */
    @Override
    public void deleteAll() {

    }

    /**
     * @param aLong
     * @return
     */
    @Override
    public Optional<Transaction> findById(Long aLong) {
        Optional<Transaction> found = Optional.empty();
        for (Transaction t : added) {
            if (t.getId().equals(aLong)) {
                found = Optional.of(t);
                break;
            }
        }
        return found;
    }

    /**
     * @param aLong
     * @return
     */
    @Override
    public boolean existsById(Long aLong) {
        return false;
    }

    /**
     * @param sort
     * @return
     */
    @Override
    public List<Transaction> findAll(Sort sort) {
        return null;
    }

    /**
     * @param pageable
     * @return
     */
    @Override
    public Page<Transaction> findAll(Pageable pageable) {
        return null;
    }
}

