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
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class OrderDetailsService implements OrderInterface {

    private final RestaurantTableRepository restaurantTableRepository;
    private final TransactionRepository transactionRepository;
    private final CustomerRepository customerRepository;
    private final OrderProductRepository orderProductRepository;
    private final OrderTransactionRepository orderTransactionRepository;
    private final HistoryRepository historyRepository;
    private final CustomerOrderRepository customerOrderRepository;


    @Transactional
    public CustomerOrder saveCustomerOrder(Double total) {
        CustomerOrder newOrder = new CustomerOrder();
        newOrder.setDate(LocalDateTime.now());
        newOrder.setTotal(total);
        newOrder.setStatus(3);
        return customerOrderRepository.save(newOrder);
    }

    @Transactional
    public RestaurantTable findTableByNumber(Long tableNumber) {
        RestaurantTable table = restaurantTableRepository.findByTableNumber(tableNumber);
        if (table == null) {
            throw new GenericException("Mesa no encontrada", HttpStatus.BAD_REQUEST);
        }
        return table;
    }

    @Transactional
    public void saveOrderProducts(List<ItemRequest> items, Long orderId) {
        for (ItemRequest item : items) {
            OrderProduct orderProduct = new OrderProduct();
            orderProduct.setOrderId(orderId);
            orderProduct.setProductId(item.getProductId());
            orderProduct.setName(item.getName());
            orderProduct.setQuantity(item.getQty());
            orderProduct.setUnitePrice(item.getUnitPrice());

            orderProductRepository.save(orderProduct);
        }
    }

    @Transactional
    public Transaction createTransaction(OrderDetailsDTO orderDetailsDTO, RestaurantTable table) {
        Transaction transaction = transactionRepository.findTransactionByTableId(orderDetailsDTO.getRestaurantTable());
        if (transaction == null) {
            Customer customer = findOrCreateCustomer(orderDetailsDTO.getPhone());
            transaction = new Transaction();
            transaction.setCustomerId(customer.getCustomer_id());
            transaction.setTableId(table.getTableId());
            transaction.setStatus(1L);
            transactionRepository.save(transaction);
        }
        return transaction;
    }

    @Transactional
    public Customer findOrCreateCustomer(String phone) {
        Customer customer = customerRepository.findByPhone(phone);
        if (customer == null) {
            customer = new Customer();
            customer.setPhone(phone);
            customerRepository.save(customer);
        }
        return customer;
    }

    @Transactional
    public OrderTransaction createOrderTransaction(Long orderId, Long transactionId) {
        OrderTransaction orderTransaction = new OrderTransaction();
        orderTransaction.setOrderId(orderId);
        orderTransaction.setTransactionId(transactionId);

        orderTransactionRepository.save(orderTransaction);
        return orderTransaction;
    }

    public void updateTransactionTotal(Transaction transaction) {
        transaction.setTransactionTotal(orderTransactionRepository.getTotalOrderAmount(transaction.getTransactionId()));
        transactionRepository.save(transaction);
    }

    @Transactional
    public void saveTransactionHistory(Transaction transaction) {
        History history = new History();
        history.setTransactionId(transaction.getTransactionId());
        history.setDate(LocalDateTime.now());
        historyRepository.save(history);
    }

    @Override
    public GenericResponse saveOrder(OrderDetailsDTO orderDetailsDTO) {
        // Verifica la mesa
        RestaurantTable table = findTableByNumber(orderDetailsDTO.getRestaurantTable());

        // Verifica si la mesa está disponible
        if (table.getStatus() != null && table.getStatus() == 2) {

            // Guarda la orden
            CustomerOrder setOrder = saveCustomerOrder(orderDetailsDTO.getTotal());

            // Guarda los productos de la orden
            saveOrderProducts(orderDetailsDTO.getItems(), setOrder.getOrderId());

            // Crea o recupera la transacción
            Transaction transaction = createTransaction(orderDetailsDTO, table);

            // Crea la relación entre orden y transacción
            OrderTransaction orderTransaction = createOrderTransaction(setOrder.getOrderId(), transaction.getTransactionId());

            // Actualiza el total de la transacción
            updateTransactionTotal(transaction);

            // Guarda el historial de la transacción
            saveTransactionHistory(transaction);

            return new GenericResponse("Transacción guardada con éxito", 200L);
        }

        throw new GenericException("Mesa en estado inválido", HttpStatus.BAD_REQUEST);
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

    public void updateTotalTransaction(Long orderId){

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

        CustomerOrder order = customerOrderRepository.findById(orderId).orElseThrow(() -> new GenericException("Orden no encontrada", HttpStatus.BAD_REQUEST));
        order.setStatus(2);
        customerOrderRepository.save(order);

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
    public List<OrderResponseDTO> noConfirmationOrder(Long tableNumber, String phoneNumber) {
        List<Object[]> resultList = orderTransactionRepository.findAllOrdersNotConfirm(tableNumber, phoneNumber);

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

    @Override
    public GenericResponse confirmationOrder(String phoneNumber) {

        Long transactionId = transactionRepository.getTransactionIdByPhoneNumber(phoneNumber);

        CustomerOrder customerOrder  = customerOrderRepository.findByTransactionIdAndStatusNoConfirm(transactionId);

        customerOrder.setStatus(1);
        customerOrderRepository.save(customerOrder);

        return new GenericResponse("Orden confirmada", 200L);
    }
}
