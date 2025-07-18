package com.restaurante.bot.model;

import jakarta.persistence.*;
import jdk.jfr.Unsigned;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "customerorder")
public class CustomerOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "table_id", nullable = false)
    private Integer tableId;

    @Column(name = "date", nullable = false)
    private LocalDateTime date;

    @Column(name = "order_status")
    private String orderStatus;

    @Column(name = "total")
    private BigDecimal total;

    @ManyToOne
    @JoinColumn(name = "table_id", referencedColumnName = "table_id", insertable = false, updatable = false)
    private RestaurantTable restaurantTable;


    public CustomerOrder() {}

    public CustomerOrder (Long orderId, Integer tableId, LocalDateTime date, String orderStatus, BigDecimal total) {
        this.orderId = orderId;
        this.tableId = tableId;
        this.date = date;
        this.orderStatus = orderStatus;
        this.total = total;

    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Integer getTableId() {
        return tableId;
    }

    public void setTableId(Integer tableId) {
        this.tableId = tableId;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public RestaurantTable getRestaurantTable() {
        return restaurantTable;
    }

    public void setRestaurantTable(RestaurantTable restaurantTable) {
        this.restaurantTable = restaurantTable;
    }

}