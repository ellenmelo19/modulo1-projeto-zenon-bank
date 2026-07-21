package br.com.zenon.fraud;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Implementação de {@link TransactionRepository} baseada em {@link List}.
 *
 * <p>A busca percorre a lista linearmente — complexidade {@code O(n)} no pior caso
 * (cliente no final da lista ou inexistente).</p>
 */
public class TransactionListRepository implements TransactionRepository {

    private final List<Transaction> transactions;

    public TransactionListRepository(List<Transaction> transactions) {
        this.transactions = Objects.requireNonNull(transactions, "transactions should not be null");
    }

    @Override
    public Optional<Transaction> findByOriginName(String nameOrig) {
        Objects.requireNonNull(nameOrig, "nameOrig should not be null");

        for (Transaction transaction : transactions) {
            if (transaction.origin().name().equals(nameOrig)) {
                return Optional.of(transaction);
            }
        }
        return Optional.empty();
    }
}
