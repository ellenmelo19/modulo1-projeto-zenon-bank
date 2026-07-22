package br.com.zenon;

import br.com.zenon.fraud.TransactionReport;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Locale;

/**
 * Ponto de entrada do relatório lazy do Zenón Bank.
 *
 * <p>Processa o CSV PaySim completo (~493&nbsp;MB) com
 * {@link TransactionReport} (NIO.2 / {@code Files.lines}), sem carregar
 * todas as transações em memória. Pode ser executado com heap limitada,
 * por exemplo: {@code java -Xmx128m ... br.com.zenon.ReportMain}</p>
 */
public class ReportMain {

    private static final String DEFAULT_CSV_PATH =
            "data/PS_20174392719_1491204439457_log.csv";

    public static void main(String[] args) {
        String fileName = args.length > 0 ? args[0] : DEFAULT_CSV_PATH;
        Path path = Path.of(fileName);

        TransactionReport report = new TransactionReport();

        try {
            TransactionReport.Summary summary = report.generate(path);

            System.out.println("Total de linhas: " + summary.totalLines());
            System.out.println("Total de fraudes: " + summary.totalFrauds());
            System.out.printf(Locale.US, "Valor total transacionado: %.2f%n", summary.totalAmount());
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo CSV: " + e.getMessage());
            System.err.println("Caminho tentado: " + fileName);
        }
    }
}
