package br.com.zenon.fraud;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Responsável pela ingestão de transações do dataset PaySim a partir de um arquivo CSV.
 *
 * <p>Utiliza a API clássica {@code java.io} para leitura sequencial do arquivo
 * e faz o parsing manual de cada linha. Linhas inválidas são registradas em
 * {@link System#err} e ignoradas, sem abortar o processamento.</p>
 */
public class TransactionIngestor {

    private static final int MAX_LINES = 1_000;
    private static final int EXPECTED_COLUMNS = 11;

    /**
     * Lê as primeiras 1.000 linhas de dados do CSV (após o cabeçalho)
     * e as transforma em uma lista de {@link Transaction} válidas.
     *
     * @param fileName caminho do arquivo CSV do PaySim
     * @return lista com até 1.000 transações válidas importadas
     * @throws IOException se ocorrer erro de leitura do arquivo
     */
    public List<Transaction> ingest(String fileName) throws IOException {
        List<Transaction> transactions = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String header = reader.readLine();
            if (header == null) {
                return transactions;
            }

            String line;
            int linesRead = 0;
            while (linesRead < MAX_LINES && (line = reader.readLine()) != null) {
                if (!line.isBlank()) {
                    parseLine(line).ifPresent(transactions::add);
                    linesRead++;
                }
            }
        }

        return transactions;
    }

    /**
     * Tenta converter uma linha bruta do CSV em um {@link Transaction}.
     *
     * @return {@link Optional} com a transação, ou vazio se a linha for inválida
     */
    private Optional<Transaction> parseLine(String line) {
        try {
            String[] columns = line.split(",", -1);

            if (columns.length != EXPECTED_COLUMNS) {
                throw new IllegalArgumentException(
                        "Linha com número inválido de colunas (" + columns.length + "): " + line
                );
            }

            int step = Integer.parseInt(columns[0].trim());
            TransactionType type = TransactionType.valueOf(columns[1].trim());
            BigDecimal amount = new BigDecimal(columns[2].trim());

            TransactionCustomer origin = new TransactionCustomer(
                    columns[3].trim(),
                    new BigDecimal(columns[4].trim()),
                    new BigDecimal(columns[5].trim())
            );

            TransactionCustomer recipient = new TransactionCustomer(
                    columns[6].trim(),
                    new BigDecimal(columns[7].trim()),
                    new BigDecimal(columns[8].trim())
            );

            boolean isFraud = parseFlag(columns[9]);
            boolean isFlaggedFraud = parseFlag(columns[10]);

            return Optional.of(
                    new Transaction(step, type, amount, origin, recipient, isFraud, isFlaggedFraud)
            );
        } catch (RuntimeException e) {
            System.err.println("Erro: " + line + " | " + e);
            return Optional.empty();
        }
    }

    private boolean parseFlag(String value) {
        return Optional.ofNullable(value)
                .map(String::trim)
                .filter("1"::equals)
                .isPresent();
    }
}
