package br.com.zenon.fraud;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;

/**
 * Cliente envolvido em uma transação (origem ou destinatário),
 * com saldos antes e depois da operação.
 *
 * <p>Corresponde às colunas {@code name*}, {@code oldbalance*} e {@code newbalance*} do CSV PaySim.</p>
 */
public record TransactionCustomer(
        String name,
        BigDecimal oldBalance,
        BigDecimal newBalance
) {

    public TransactionCustomer {
        name = Optional.ofNullable(name)
                .filter(n -> !n.isBlank())
                .orElseThrow(() -> new IllegalArgumentException("name should not be empty"));

        oldBalance = Objects.requireNonNull(oldBalance, "oldBalance should not be null");
        newBalance = Objects.requireNonNull(newBalance, "newBalance should not be null");

        if (oldBalance.signum() < 0) {
            throw new IllegalArgumentException("oldBalance should be positive: " + oldBalance);
        }
        if (newBalance.signum() < 0) {
            throw new IllegalArgumentException("newBalance should be positive: " + newBalance);
        }
    }
}
