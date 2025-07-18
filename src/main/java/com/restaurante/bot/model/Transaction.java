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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Long transactionId;

    @Column(name = "table_id")
    private Integer tableId;

    @Column(name = "customer_id")
    private Integer customerId;

    @Column(name = "payment_id")
    private Integer paymentId;

    @Column(name = "rating_id")
    private Integer ratingId;

    @Column(name = "transaction_total")
    private Double transactionTotal;

    @Column(name = "status")
    private Long status;


}
