package br.com.zenon.fraud;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Analisa padrões de fraude em coleções de {@link Transaction}
 * utilizando a Stream API de forma funcional e declarativa.
 *
 * <p>Cada método cria um novo stream a partir da lista de entrada,
 * pois streams não podem ser reutilizados.</p>
 */
public class FraudAnalyzer {

    /**
     * Conta quantas transações são fraude ({@code isFraud == true}).
     */
    public long countFrauds(List<Transaction> transactions) {
        return transactions.stream()
                .filter(Transaction::isFraud)
                .count();
    }

    /**
     * Retorna os 3 maiores valores ({@code amount}) entre as fraudes,
     * em ordem decrescente.
     */
    public List<BigDecimal> top3FraudAmounts(List<Transaction> transactions) {
        return transactions.stream()
                .filter(Transaction::isFraud)
                .map(Transaction::amount)
                .sorted(Comparator.reverseOrder())
                .limit(3)
                .toList();
    }

    /**
     * Identifica os 5 clientes de origem mais suspeitos: nomes únicos
     * associados às fraudes de maior valor, sem repetições.
     */
    public List<String> top5SuspiciousClients(List<Transaction> transactions) {
        return transactions.stream()
                .filter(Transaction::isFraud)
                .sorted(Comparator.comparing(Transaction::amount).reversed())
                .map(t -> t.origin().name())
                .distinct()
                .limit(5)
                .toList();
    }

    /**
     * Calcula o prejuízo total causado pelas fraudes (soma dos {@code amount}).
     */
    public BigDecimal totalFraudLoss(List<Transaction> transactions) {
        return transactions.stream()
                .filter(Transaction::isFraud)
                .map(Transaction::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Conta quantas fraudes ocorreram por tipo de transação
     * ({@code CASH_OUT}, {@code TRANSFER}, etc.).
     */
    public Map<TransactionType, Long> countFraudsByType(List<Transaction> transactions) {
        return transactions.stream()
                .filter(Transaction::isFraud)
                .collect(Collectors.groupingBy(
                        Transaction::type,
                        LinkedHashMap::new,
                        Collectors.counting()
                ));
    }
}
