package br.com.zenon.fraud;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Transação financeira imutável modelada a partir do CSV PaySim.
 *
 * <p>Os clientes de origem e destino são representados pelo record {@link TransactionCustomer},
 * em vez de campos soltos do CSV ({@code nameOrig}, {@code oldbalanceOrg}, etc.).</p>
 */
public record Transaction(
        int step,
        TransactionType type,
        BigDecimal amount,
        TransactionCustomer origin,
        TransactionCustomer recipient,
        boolean isFraud,
        boolean isFlaggedFraud
) {

    public Transaction {
        type = Objects.requireNonNull(type, "type should not be null");
        amount = Objects.requireNonNull(amount, "amount should not be null");
        origin = Objects.requireNonNull(origin, "origin should not be null");
        recipient = Objects.requireNonNull(recipient, "recipient should not be null");

        if (step < 1) {
            throw new IllegalArgumentException("step should be positive: " + step);
        }

        if (amount.signum() < 0) {
            throw new IllegalArgumentException("amount should be positive: " + amount);
        }
    }
}
