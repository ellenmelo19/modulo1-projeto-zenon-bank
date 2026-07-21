package br.com.zenon;

import br.com.zenon.fraud.Transaction;
import br.com.zenon.fraud.TransactionIngestor;

import java.io.IOException;
import java.util.List;

/**
 * Ponto de entrada do detector de fraudes do Zenón Bank.
 *
 * <p>Testa a ingestão do CSV com dados sujos, imprimindo erros em
 * {@link System#err} e as transações válidas em {@link System#out}.</p>
 */
public class Main {

    private static final String DEFAULT_CSV_PATH =
            "data/paysim_with_bad_data.csv";

    public static void main(String[] args) {
        String fileName = args.length > 0 ? args[0] : DEFAULT_CSV_PATH;

        TransactionIngestor ingestor = new TransactionIngestor();

        try {
            List<Transaction> transactions = ingestor.ingest(fileName);

            System.out.println(transactions.size());
            for (Transaction transaction : transactions) {
                System.out.println(transaction);
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo CSV: " + e.getMessage());
            System.err.println("Caminho tentado: " + fileName);
        }
    }
}
