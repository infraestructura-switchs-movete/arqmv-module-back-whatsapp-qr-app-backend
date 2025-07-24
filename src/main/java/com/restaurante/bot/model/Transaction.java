package com.restaurante.bot.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "transaction")
public class Transaction {

    @Id
    @SequenceGenerator(name = "TRANSACTION-SEQ", sequenceName = "TRANSACTION_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TRANSACTION-SEQ")
    @Column(name = "transaction_id")
    private Long transactionId;

    @Column(name = "table_id")
    private Integer tableId;

    @Column(name = "customer_id")
    private Long customerId;

    @Column(name = "payment_id")
    private Integer paymentId;

    @Column(name = "rating_id")
    private Integer ratingId;

    @Column(name = "transaction_total")
    private Double transactionTotal;

    @Column(name = "status")
    private Long status;


}
