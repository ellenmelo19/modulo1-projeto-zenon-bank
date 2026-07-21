package br.com.zenon.fraud;

import java.util.Optional;

/**
 * Contrato de busca de {@link Transaction} por nome do cliente de origem.
 */
public interface TransactionRepository {

    /**
     * Busca a primeira transação cujo cliente de origem tenha o nome informado.
     *
     * @param nameOrig nome do cliente de origem ({@code nameOrig} no CSV PaySim)
     * @return {@link Optional} com a transação, ou vazio se não houver correspondência
     */
    Optional<Transaction> findByOriginName(String nameOrig);
}
