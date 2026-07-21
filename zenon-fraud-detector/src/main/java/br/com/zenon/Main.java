package br.com.zenon;

import br.com.zenon.fraud.Transaction;
import br.com.zenon.fraud.TransactionIngestor;

import java.io.IOException;
import java.util.List;

/**
 * Ponto de entrada do detector de fraudes do Zenón Bank.
 *
 * <p>Testa a ingestão das primeiras 1.000 linhas do CSV PaySim
 * e imprime as 10 primeiras transações importadas.</p>
 */
public class Main {

    private static final String DEFAULT_CSV_PATH =
            "data/PS_20174392719_1491204439457_log.csv";

    public static void main(String[] args) {
        String fileName = args.length > 0 ? args[0] : DEFAULT_CSV_PATH;

        TransactionIngestor ingestor = new TransactionIngestor();

        try {
            List<Transaction> transactions = ingestor.ingest(fileName);

            System.out.println("Total de transações importadas: " + transactions.size());
            System.out.println();

            int limit = Math.min(10, transactions.size());
            for (int i = 0; i < limit; i++) {
                System.out.println(transactions.get(i));
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo CSV: " + e.getMessage());
            System.err.println("Caminho tentado: " + fileName);
        }
    }
}
