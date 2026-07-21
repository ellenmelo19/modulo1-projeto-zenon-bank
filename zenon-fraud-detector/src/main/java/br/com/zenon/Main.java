package br.com.zenon;

import br.com.zenon.fraud.Transaction;
import br.com.zenon.fraud.TransactionIngestor;
import br.com.zenon.fraud.TransactionListRepository;
import br.com.zenon.fraud.TransactionMapRepository;
import br.com.zenon.fraud.TransactionRepository;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Ponto de entrada do detector de fraudes do Zenón Bank.
 *
 * <p>Compara o desempenho da busca por cliente de origem em
 * {@link TransactionListRepository} ({@code O(n)}) versus
 * {@link TransactionMapRepository} ({@code O(1)}).</p>
 */
public class Main {

    private static final String DEFAULT_CSV_PATH =
            "data/PS_20174392719_1491204439457_log.csv";

    private static final String EXISTING_CLIENT = "C1231006815";
    private static final String MISSING_CLIENT = "C12345";

    public static void main(String[] args) {
        String fileName = args.length > 0 ? args[0] : DEFAULT_CSV_PATH;

        TransactionIngestor ingestor = new TransactionIngestor();

        try {
            List<Transaction> transactions = ingestor.ingest(fileName);
            System.out.println("Transações carregadas: " + transactions.size());

            TransactionRepository listRepository = new TransactionListRepository(transactions);

            // Busca com valor inexistente
            printSearchResult(listRepository, MISSING_CLIENT);

            // Busca com valor existente
            printSearchResult(listRepository, EXISTING_CLIENT);

            // Pior caso da lista: cliente de origem da última transação
            String lastOriginName = transactions.get(transactions.size() - 1).origin().name();
            System.out.println();
            System.out.println("=== Benchmark (pior caso da List: " + lastOriginName + ") ===");

            long listStart = System.nanoTime();
            Optional<Transaction> listResult = listRepository.findByOriginName(lastOriginName);
            long listElapsed = System.nanoTime() - listStart;

            System.out.println("List  -> encontrada: " + listResult.isPresent()
                    + " | tempo: " + listElapsed + " ns ("
                    + formatMillis(listElapsed) + " ms)");

            TransactionRepository mapRepository = new TransactionMapRepository(transactions);

            long mapStart = System.nanoTime();
            Optional<Transaction> mapResult = mapRepository.findByOriginName(lastOriginName);
            long mapElapsed = System.nanoTime() - mapStart;

            System.out.println("Map   -> encontrada: " + mapResult.isPresent()
                    + " | tempo: " + mapElapsed + " ns ("
                    + formatMillis(mapElapsed) + " ms)");

            if (listElapsed > 0 && mapElapsed > 0) {
                System.out.printf("Map foi aproximadamente %.1fx mais rápido que List%n",
                        (double) listElapsed / mapElapsed);
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo CSV: " + e.getMessage());
            System.err.println("Caminho tentado: " + fileName);
        }
    }

    private static void printSearchResult(TransactionRepository repository, String nameOrig) {
        Optional<Transaction> result = repository.findByOriginName(nameOrig);
        if (result.isPresent()) {
            System.out.println(result.get());
        } else {
            System.out.println("Transação não encontrada para o cliente " + nameOrig);
        }
    }

    private static String formatMillis(long nanos) {
        return String.format("%.4f", nanos / 1_000_000.0);
    }
}
