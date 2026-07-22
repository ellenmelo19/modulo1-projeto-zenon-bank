package br.com.zenon.fraud;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Gera um relatório agregado do CSV PaySim com leitura lazy via NIO.2.
 *
 * <p>Utiliza {@link Files#lines(Path)}, que retorna um {@code Stream<String>}
 * processando o arquivo linha a linha — sem carregar todas as transações na RAM.
 * Assim o arquivo inteiro (~493&nbsp;MB / ~6&nbsp;milhões de linhas) pode ser
 * processado com heap limitada (ex.: {@code -Xmx128m}).</p>
 */
public class TransactionReport {

    private static final int AMOUNT_COLUMN = 2;
    private static final int IS_FRAUD_COLUMN = 9;
    private static final int EXPECTED_COLUMNS = 11;

    /**
     * Resumo agregado do processamento do arquivo.
     *
     * @param totalLines     número de linhas de dados (sem o cabeçalho)
     * @param totalFrauds    quantidade de transações com {@code isFraud == 1}
     * @param totalAmount    soma de todos os {@code amount}
     */
    public record Summary(long totalLines, long totalFrauds, BigDecimal totalAmount) {
    }

    /**
     * Processa o arquivo CSV completo de forma lazy e retorna apenas agregados.
     *
     * @param filePath caminho do CSV PaySim
     * @return resumo com totais de linhas, fraudes e valor transacionado
     * @throws IOException se ocorrer erro de leitura do arquivo
     */
    public Summary generate(Path filePath) throws IOException {
        Objects.requireNonNull(filePath, "filePath should not be null");

        long totalLines = 0;
        long totalFrauds = 0;
        BigDecimal totalAmount = BigDecimal.ZERO;

        try (Stream<String> lines = Files.lines(filePath)) {
            // skip(1) descarta o cabeçalho; cada linha é lida sob demanda (lazy)
            var dataLines = lines.skip(1).filter(line -> !line.isBlank()).iterator();

            while (dataLines.hasNext()) {
                String line = dataLines.next();
                totalLines++;

                String[] columns = line.split(",", -1);
                if (columns.length < EXPECTED_COLUMNS) {
                    continue;
                }

                totalAmount = totalAmount.add(new BigDecimal(columns[AMOUNT_COLUMN].trim()));

                if ("1".equals(columns[IS_FRAUD_COLUMN].trim())) {
                    totalFrauds++;
                }
            }
        }

        return new Summary(totalLines, totalFrauds, totalAmount);
    }
}
