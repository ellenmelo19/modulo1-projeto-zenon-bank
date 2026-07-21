package br.com.zenon;

import br.com.zenon.fraud.FraudAnalyzer;
import br.com.zenon.fraud.Transaction;
import br.com.zenon.fraud.TransactionIngestor;
import br.com.zenon.fraud.TransactionType;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Ponto de entrada do detector de fraudes do Zenón Bank.
 *
 * <p>Carrega as transações do CSV e executa as análises de fraude
 * via {@link FraudAnalyzer} (Stream API).</p>
 */
public class Main {

    private static final String DEFAULT_CSV_PATH =
            "data/PS_20174392719_1491204439457_log.csv";

    public static void main(String[] args) {
        String fileName = args.length > 0 ? args[0] : DEFAULT_CSV_PATH;

        TransactionIngestor ingestor = new TransactionIngestor();
        FraudAnalyzer analyzer = new FraudAnalyzer();

        try {
            List<Transaction> transactions = ingestor.ingest(fileName);

            long totalFrauds = analyzer.countFrauds(transactions);
            System.out.println("1. Total de Fraudes: " + totalFrauds);

            System.out.println("2. Top 3 Fraudes de Maior Valor:");
            List<BigDecimal> top3 = analyzer.top3FraudAmounts(transactions);
            top3.forEach(amount -> System.out.printf(Locale.US, "%.2f%n", amount));

            System.out.println("3. Clientes Suspeitos:");
            List<String> suspects = analyzer.top5SuspiciousClients(transactions);
            suspects.forEach(System.out::println);

            BigDecimal totalLoss = analyzer.totalFraudLoss(transactions);
            System.out.printf(Locale.US, "4. Prejuízo Total: %.2f%n", totalLoss);

            System.out.println("5. Fraudes por Tipo:");
            Map<TransactionType, Long> byType = analyzer.countFraudsByType(transactions);
            byType.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(entry ->
                            System.out.println(" - " + entry.getKey() + ": " + entry.getValue())
                    );
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo CSV: " + e.getMessage());
            System.err.println("Caminho tentado: " + fileName);
        }
    }
}
