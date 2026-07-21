package br.com.zenon.fraud;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Responsável pela ingestão de transações do dataset PaySim a partir de um arquivo CSV.
 *
 * <p>Utiliza a API clássica {@code java.io} para leitura sequencial do arquivo
 * e faz o parsing manual de cada linha.</p>
 */
public class TransactionIngestor {

    private static final int MAX_LINES = 1_000;
    private static final int EXPECTED_COLUMNS = 11;

    /**
     * Lê as primeiras 1.000 linhas de dados do CSV (após o cabeçalho)
     * e as transforma em uma lista de {@link Transaction}.
     *
     * @param fileName caminho do arquivo CSV do PaySim
     * @return lista com até 1.000 transações importadas
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
                    transactions.add(parseLine(line));
                    linesRead++;
                }
            }
        }

        return transactions;
    }

    /**
     * Converte uma linha bruta do CSV em um {@link Transaction}.
     *
     * <p>Formato esperado:
     * {@code step,type,amount,nameOrig,oldbalanceOrg,newbalanceOrig,nameDest,oldbalanceDest,newbalanceDest,isFraud,isFlaggedFraud}</p>
     */
    private Transaction parseLine(String line) {
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

        return new Transaction(step, type, amount, origin, recipient, isFraud, isFlaggedFraud);
    }

    private boolean parseFlag(String value) {
        return "1".equals(value.trim());
    }
}
