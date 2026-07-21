package br.com.zenon.fraud;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Implementação de {@link TransactionRepository} baseada em {@link Map}.
 *
 * <p>As transações são indexadas por nome do cliente de origem no carregamento,
 * permitindo busca com complexidade média {@code O(1)}.</p>
 */
public class TransactionMapRepository implements TransactionRepository {

    private final Map<String, Transaction> transactionsByOrigin;

    public TransactionMapRepository(List<Transaction> transactions) {
        Objects.requireNonNull(transactions, "transactions should not be null");

        this.transactionsByOrigin = new HashMap<>();
        for (Transaction transaction : transactions) {
            // Mantém a primeira ocorrência de cada cliente de origem
            transactionsByOrigin.putIfAbsent(transaction.origin().name(), transaction);
        }
    }

    @Override
    public Optional<Transaction> findByOriginName(String nameOrig) {
        Objects.requireNonNull(nameOrig, "nameOrig should not be null");
        return Optional.ofNullable(transactionsByOrigin.get(nameOrig));
    }
}
