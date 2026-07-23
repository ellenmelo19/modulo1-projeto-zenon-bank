package br.com.zenon;

import br.com.zenon.fraud.TransactionReport;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Ponto de entrada do relatório lazy do Zenón Bank.
 *
 * <p>Processa o CSV PaySim completo (~493&nbsp;MB) com
 * {@link TransactionReport} (NIO.2 / {@code Files.lines}), sem carregar
 * todas as transações em memória. O relatório é internacionalizado
 * (português ou inglês) via {@link ResourceBundle} e {@link NumberFormat}.</p>
 *
 * <p>Uso:
 * <ul>
 *   <li>{@code java ... br.com.zenon.ReportMain}</li>
 *   <li>{@code java ... br.com.zenon.ReportMain en}</li>
 *   <li>{@code java ... br.com.zenon.ReportMain pt arquivo.csv}</li>
 *   <li>{@code java ... br.com.zenon.ReportMain arquivo.csv}
 *       (mantém português e usa o caminho informado)</li>
 * </ul>
 * Idiomas: {@code pt} (padrão) ou {@code en}.</p>
 */
public class ReportMain {

    private static final String DEFAULT_CSV_PATH =
            "data/PS_20174392719_1491204439457_log.csv";
    private static final String DEFAULT_LANGUAGE = "pt";
    private static final String BUNDLE_BASE_NAME = "report";

    public static void main(String[] args) {
        configureUtf8Console();

        String language = DEFAULT_LANGUAGE;
        String fileName = DEFAULT_CSV_PATH;

        if (args.length == 1) {
            // Um único arg: idioma (pt/en) ou caminho do CSV
            if (resolveLocale(args[0]) != null) {
                language = args[0];
            } else if (looksLikePath(args[0])) {
                fileName = args[0];
            } else {
                language = args[0]; // ex.: "fr" → erro de idioma
            }
        } else if (args.length >= 2) {
            language = args[0];
            fileName = args[1];
        }

        Locale locale = resolveLocale(language);
        if (locale == null) {
            ResourceBundle fallback = ResourceBundle.getBundle(BUNDLE_BASE_NAME, Locale.of("pt", "BR"));
            System.err.println(fallback.getString("error.language") + " (" + language + ")");
            return;
        }

        ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_BASE_NAME, locale);
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(locale);
        Path path = Path.of(fileName);

        TransactionReport report = new TransactionReport();

        try {
            TransactionReport.Summary summary = report.generate(path);

            System.out.println(bundle.getString("total.lines") + ": " + summary.totalLines());
            System.out.println(bundle.getString("total.frauds") + ": " + summary.totalFrauds());
            System.out.println(bundle.getString("total.amount") + ": "
                    + currencyFormat.format(summary.totalAmount()));
        } catch (IOException e) {
            System.err.println(bundle.getString("error.read") + ": " + e.getMessage());
            System.err.println(bundle.getString("error.path") + ": " + fileName);
        }
    }

    /**
     * Garante saída UTF-8 no console (acentos e formatação de moeda no Windows).
     */
    private static void configureUtf8Console() {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        System.setErr(new PrintStream(System.err, true, StandardCharsets.UTF_8));
    }

    /**
     * Indica se o argumento parece um caminho de arquivo (e não um código de idioma).
     */
    private static boolean looksLikePath(String value) {
        if (value == null || value.isBlank()) {
            return false;
        }
        String normalized = value.trim().toLowerCase(Locale.ROOT);
        return normalized.contains("/")
                || normalized.contains("\\")
                || normalized.endsWith(".csv");
    }

    /**
     * Converte o código de idioma informado pelo usuário em um {@link Locale}.
     *
     * @param language {@code pt}, {@code en} (aceita também {@code pt-BR}, {@code en-US})
     * @return locale correspondente, ou {@code null} se não for suportado
     */
    private static Locale resolveLocale(String language) {
        if (language == null || language.isBlank()) {
            return null;
        }

        String normalized = language.trim().toLowerCase(Locale.ROOT);
        return switch (normalized) {
            case "pt", "pt-br", "pt_br" -> Locale.of("pt", "BR");
            case "en", "en-us", "en_us" -> Locale.US;
            default -> null;
        };
    }
}
