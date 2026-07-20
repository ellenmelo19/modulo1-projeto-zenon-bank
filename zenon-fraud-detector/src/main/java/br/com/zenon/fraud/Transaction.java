package br.com.zenon.fraud;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Transação financeira imutável modelada a partir do CSV PaySim.
 *
 * <p>Os clientes de origem e destino são representados pelo record {@link Customer},
 * em vez de campos soltos do CSV ({@code nameOrig}, {@code oldbalanceOrg}, etc.).</p>
 */
public record Transaction(
        int step,
        TransactionType type,
        BigDecimal amount,
        Customer origin,
        Customer destination,
        boolean isFraud,
        boolean isFlaggedFraud
) {

    public Transaction {
        Objects.requireNonNull(type, "type não pode ser nulo");
        Objects.requireNonNull(amount, "amount não pode ser nulo");
        Objects.requireNonNull(origin, "origin não pode ser nulo");
        Objects.requireNonNull(destination, "destination não pode ser nulo");

        if (step < 0) {
            throw new IllegalArgumentException("step não pode ser negativo");
        }
        if (amount.signum() < 0) {
            throw new IllegalArgumentException("amount não pode ser negativo");
        }
    }
}
