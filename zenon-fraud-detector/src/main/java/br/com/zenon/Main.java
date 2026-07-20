package br.com.zenon;

import br.com.zenon.fraud.Customer;
import br.com.zenon.fraud.Transaction;
import br.com.zenon.fraud.TransactionType;

import java.math.BigDecimal;

/**
 * Ponto de entrada do detector de fraudes do Zenón Bank.
 */
public class Main {

    public static void main(String[] args) {
        Transaction transaction1 = new Transaction(
                1,
                TransactionType.PAYMENT,
                new BigDecimal("9839.64"),
                new Customer("C1231006815", new BigDecimal("170136.0"), new BigDecimal("160296.36")),
                new Customer("M1979787155", new BigDecimal("0.0"), new BigDecimal("0.0")),
                false,
                false
        );

        Transaction transaction2 = new Transaction(
                743,
                TransactionType.CASH_OUT,
                new BigDecimal("850002.52"),
                new Customer("C1280323807", new BigDecimal("850002.52"), new BigDecimal("0.0")),
                new Customer("C873221189", new BigDecimal("6510099.11"), new BigDecimal("7360101.63")),
                true,
                false
        );

        System.out.println("=== Transação 1 ===");
        System.out.println(transaction1);
        System.out.println();
        System.out.println("=== Transação 2 ===");
        System.out.println(transaction2);
    }
}
