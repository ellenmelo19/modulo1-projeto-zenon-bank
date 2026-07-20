package br.com.zenon.fraud;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Cliente envolvido em uma transação (origem ou destino),
 * com saldos antes e depois da operação.
 *
 * <p>Corresponde às colunas {@code name*}, {@code oldbalance*} e {@code newbalance*} do CSV PaySim.</p>
 */
public record Customer(
        String name,
        BigDecimal oldBalance,
        BigDecimal newBalance
) {

    public Customer {
        Objects.requireNonNull(name, "name não pode ser nulo");
        Objects.requireNonNull(oldBalance, "oldBalance não pode ser nulo");
        Objects.requireNonNull(newBalance, "newBalance não pode ser nulo");

        if (name.isBlank()) {
            throw new IllegalArgumentException("name não pode ser vazio");
        }
        if (oldBalance.signum() < 0) {
            throw new IllegalArgumentException("oldBalance não pode ser negativo");
        }
        if (newBalance.signum() < 0) {
            throw new IllegalArgumentException("newBalance não pode ser negativo");
        }
    }
}
