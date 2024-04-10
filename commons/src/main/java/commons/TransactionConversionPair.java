package commons;


/**
 * A pair of a transaction and the amount of the transaction in another
 * currency. This makes it easier to work with transactions and their
 * converted amounts.
 *
 * @param   transaction
 *          The transaction.
 * @param   convertedAmount
 *          The amount of the transaction in another currency.
 */
public record TransactionConversionPair(Transaction transaction,
                                        Money convertedAmount) {
}
