package com.restaurante.bot.business.service;

import com.restaurante.bot.business.interfaces.OrderInterface;
import com.restaurante.bot.dto.*;
import com.restaurante.bot.exception.GenericException;
import com.restaurante.bot.model.*;
import com.restaurante.bot.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class OrderDetailsService implements OrderInterface {

    private final RestaurantTableRepository restaurantTableRepository;
    private final TransactionRepository transactionRepository;
    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private final OrderProductRepository orderProductRepository;
    private final OrderTransactionRepository orderTransactionRepository;
    private final HistoryRepository historyRepository;


    @Override
    public GenericResponse saveOrder(OrderDetailsDTO orderDetailsDTO) {

        RestaurantTable table = restaurantTableRepository.findByTableNumber(orderDetailsDTO.getRestaurantTable());

        if (table == null) {
            throw new GenericException("Mesa no encontrada" , HttpStatus.BAD_REQUEST);
        }

        if (table.getStatus() != null && table.getStatus() == 2) {

            Order newOrder = new Order();
            newOrder.setDate(LocalDateTime.now());
            newOrder.setTotal(orderDetailsDTO.getTotal());
            newOrder.setStatus(1);
            Order setedOrder = orderRepository.save(newOrder);

            List<ItemRequest> items = orderDetailsDTO.getItems();

            for (ItemRequest item : items) {

                OrderProduct orderProduct = new OrderProduct();
                orderProduct.setOrderId(setedOrder.getOrderId());
                orderProduct.setProductId(item.getProductId());
                orderProduct.setName(item.getName());
                orderProduct.setQuantity(item.getQty());
                orderProduct.setUnitePrice(item.getUnitPrice());

                orderProductRepository.save(orderProduct);
            }

            Transaction transaction  = transactionRepository
                    .findTransactionByTableId(orderDetailsDTO.getRestaurantTable());

            if (transaction == null) {

                Customer customer = customerRepository.findByPhone(orderDetailsDTO.getPhone());
                if (customer == null) {
                    customer = new Customer();
                    customer.setPhone(orderDetailsDTO.getPhone());
                    customerRepository.save(customer);
                }

                transaction = new Transaction();
                transaction.setCustomerId(Integer.parseInt(customer.getCustomer_id().toString()));
                transaction.setTableId(table.getTableId());
                transaction.setStatus(1L);
                transactionRepository.save(transaction);
            }

            OrderTransaction orderTransaction = new OrderTransaction();
            orderTransaction.setOrderId(setedOrder.getOrderId());
            orderTransaction.setTransactionId(transaction.getTransactionId());

            orderTransactionRepository.save(orderTransaction);

            transaction
                    .setTransactionTotal(orderTransactionRepository
                            .getTotalOrderAmount(transaction.getTransactionId()));

            transactionRepository.save(transaction);

            History history = new History();
            history.setTransactionId(transaction.getTransactionId());
            history.setDate(LocalDateTime.now());

            historyRepository.save(history);

        }

        return new GenericResponse("Transaccion guardad con exito", 200L);
    }

    @Override
    public List<OrderResponseDTO> getOrders() {
        List<Object[]> resultList = orderTransactionRepository.findAllOrdersGroupedByMesaNative();

        Map<Integer, OrderResponseDTO> mesaMap = new HashMap<>();

        for (Object[] result : resultList) {
            // Manejo de posibles nulos
            Integer mesaId = (result[0] != null) ? (Integer) result[0] : null;
            String statusMesa = (result[1] != null) ? (String) result[1] : "Unknown"; // Default a "Unknown" si es null
            Double totalGeneral = (result[2] != null) ? ((BigDecimal) result[2]).doubleValue() : 0.0;
            Integer orderId = (result[3] != null) ? (Integer) result[3] : null;
            Integer productId = (result[4] != null) ? (Integer) result[4] : null;
            String productName = (result[5] != null) ? (String) result[5] : "Unknown Product";
            Integer qty = (result[6] != null) ? (Integer) result[6] : 0;
            Double unitePrice = (result[7] != null) ? ((Double) result[7]).doubleValue() : 0.0;
            Double totalPrice = (result[8] != null) ? ((Double) result[8]).doubleValue() : 0.0;

            // Verificar si la mesa ya existe en el mapa
            OrderResponseDTO dto = mesaMap.get(mesaId);
            if (dto == null) {
                dto = new OrderResponseDTO(mesaId, statusMesa.equals("2") ? 2 : 1, new ArrayList<>(), totalGeneral);
                mesaMap.put(mesaId, dto);
            }

            // Agregar la orden a la mesa
            dto.getOrders().add(new OrderDTO(orderId, productId.toString(), productName, qty, unitePrice, totalPrice));
        }

        // Devolver el resultado
        return new ArrayList<>(mesaMap.values());

    }

    public void updateTotalTransaction(Integer orderId){

        OrderTransaction orderTransaction = orderTransactionRepository
                .findByOrderId(Long
                        .parseLong(orderId
                                .toString()));

        Transaction transaction = transactionRepository
                .findByTransactionId(orderTransaction
                        .getTransactionId());

        transaction
                .setTransactionTotal(orderTransactionRepository
                        .getTotalOrderAmount(transaction
                                .getTransactionId()));

        transactionRepository.save(transaction);
    }

    @Override
    public GenericResponse sendOrderStatus(Long orderId) {

        Order order = orderRepository.findById(orderId).orElseThrow(() -> new GenericException("Orden no encontrada", HttpStatus.BAD_REQUEST));
        order.setStatus(2);
        orderRepository.save(order);

        updateTotalTransaction(order.getOrderId());

        return new GenericResponse("Orden con id " + order.getOrderId() + " enviada", 200L);
    }

    @Override
    public List<OrderResponseDTO> getSendOrder(Long tableNumber) {
        List<Object[]> resultList = orderTransactionRepository.findAllOrdersEnviadas(tableNumber);

        Map<Integer, OrderResponseDTO> mesaMap = new HashMap<>();

        for (Object[] result : resultList) {
            // Manejo de posibles nulos
            Integer mesaId = (result[0] != null) ? (Integer) result[0] : null;
            String statusMesa = (result[1] != null) ? (String) result[1] : "Unknown"; // Default a "Unknown" si es null
            Double totalGeneral = (result[2] != null) ? ((BigDecimal) result[2]).doubleValue() : 0.0;
            Integer orderId = (result[3] != null) ? (Integer) result[3] : null;
            Integer productId = (result[4] != null) ? (Integer) result[4] : null;
            String productName = (result[5] != null) ? (String) result[5] : "Unknown Product";
            Integer qty = (result[6] != null) ? (Integer) result[6] : 0;
            Double unitePrice = (result[7] != null) ? ((Double) result[7]).doubleValue() : 0.0;
            Double totalPrice = (result[8] != null) ? ((Double) result[8]).doubleValue() : 0.0;

            // Verificar si la mesa ya existe en el mapa
            OrderResponseDTO dto = mesaMap.get(mesaId);
            if (dto == null) {
                dto = new OrderResponseDTO(mesaId, statusMesa.equals("2") ? 2 : 1, new ArrayList<>(), totalGeneral);
                mesaMap.put(mesaId, dto);
            }

            // Agregar la orden a la mesa
            dto.getOrders().add(new OrderDTO(orderId, productId.toString(), productName, qty, unitePrice, totalPrice));
        }

        // Devolver el resultado
        return new ArrayList<>(mesaMap.values());

    }

    @Override
    public CustomerOrderResponseDTO getOrederByPhoneNumber(String phoneNumber) {

        List<Object[]> rows = orderTransactionRepository.getOrderByPhoneNumber(phoneNumber);



        if (rows.isEmpty()) return null;

        List<OrderItemDTO> items = new ArrayList<>();
        String customerPhone = null;
        Double total = null;

        for (Object[] row : rows) {

            OrderItemDTO item = new OrderItemDTO();
            item.setProductId(((Number) row[1]).longValue());
            item.setName((String) row[2]);
            item.setQty(((Number) row[3]).intValue());
            item.setPrice(((Number) row[4]).doubleValue());
            items.add(item);

            customerPhone = (String) row[0];
            total = ((Number) row[5]).doubleValue();
        }
        CustomerOrderResponseDTO response = new CustomerOrderResponseDTO();
        response.setPhone(customerPhone);
        response.setItems(items);
        response.setTotal(total);

        return response;
    }

    @Override
    public CustomerOrderResponseDTO getOrederByTableNumber(Integer tableNumber) {
        List<Object[]> rows = orderTransactionRepository.getOrderByTableNumber(tableNumber);

        if (rows.isEmpty()) return null;

        List<OrderItemDTO> items = new ArrayList<>();
        String customerPhone = null;
        Double total = null;

        for (Object[] row : rows) {

            OrderItemDTO item = new OrderItemDTO();
            item.setProductId(((Number) row[1]).longValue());
            item.setName((String) row[2]);
            item.setQty(((Number) row[3]).intValue());
            item.setPrice(((Number) row[4]).doubleValue());
            items.add(item);

            customerPhone = (String) row[0];
            total = ((Number) row[5]).doubleValue();
        }
        CustomerOrderResponseDTO response = new CustomerOrderResponseDTO();
        response.setPhone(customerPhone);
        response.setItems(items);
        response.setTotal(total);

        return response;
    }
}
