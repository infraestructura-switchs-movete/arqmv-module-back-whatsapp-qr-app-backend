package com.restaurante.bot.business.service;

import com.restaurante.bot.api.dto.ProductDTO;
import com.restaurante.bot.business.call.CallServiceHttp;
import com.restaurante.bot.business.interfaces.IOrderDetailBusiness;
import com.restaurante.bot.dto.*;
import com.restaurante.bot.model.*;
import com.restaurante.bot.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class OrderDetailsDeliveryService implements IOrderDetailBusiness {

    private final OrderDeliveryDetailRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final OrderProductDeliveryRepository orderProductRepository;
    private final CallServiceHttp callServiceHttp;

    @Override
    public OrderDetailDelivery saveOrder(OrderDetailsDeliveryDTO orderDetailsDTO) {
        Optional<Customer> existingCustomerOptional = Optional.ofNullable(customerRepository.findByPhone(orderDetailsDTO.getPhone()));
        Customer customer;

        if (existingCustomerOptional.isPresent()) {
            customer = existingCustomerOptional.get();
            customer.setName(orderDetailsDTO.getNameClient());
            customer.setEmail(orderDetailsDTO.getMail());
            customer.setAddress(orderDetailsDTO.getAddress());
            customer.setNumerIdentification(orderDetailsDTO.getNameIdentification());
            customer.setTypeIdentificationId(orderDetailsDTO.getTypeIdentificationId());
            customer = customerRepository.save(customer);
        } else {
            customer = Customer.builder()
                    .name(orderDetailsDTO.getNameClient())
                    .phone(orderDetailsDTO.getPhone())
                    .email(orderDetailsDTO.getMail())
                    .address(orderDetailsDTO.getAddress())
                    .numerIdentification(orderDetailsDTO.getNameIdentification())
                    .typeIdentificationId(orderDetailsDTO.getTypeIdentificationId())
                    .build();
            customer = customerRepository.save(customer);
        }

        OrderDetailDelivery orderDetail = new OrderDetailDelivery();
        orderDetail.setTotal(orderDetailsDTO.getTotal());
        orderDetail.setStatusOrder("PENDIENTE");
        orderDetail.setMethod(orderDetailsDTO.getMethod());
        orderDetail.setStatus("ACTIVE");
        orderDetail.setPaymentId(orderDetailsDTO.getPaymentId());
        orderDetail.setCustomerId(customer.getCustomer_id());

        orderDetail = orderRepository.save(orderDetail);

        Long companyId = 238L;
        if (companyId == null || companyId <= 0) {
            throw new IllegalArgumentException("Invalid company ID");
        }

        List<ProductDTO> externalProducts = callServiceHttp.getProduct(companyId);
        if (externalProducts == null || externalProducts.isEmpty()) {
            throw new RuntimeException("No products found for the given company ID");
        }

        for (SellProducts productDTO : orderDetailsDTO.getProducts()) {
            if (productDTO.getProductId() == null ) {
                throw new IllegalArgumentException("Invalid product ID");
            }

            ProductDTO externalProduct = externalProducts.stream()
                    .filter(p -> p.getIdProducto().equals(productDTO.getProductId()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Product with ID " + productDTO.getProductId() + " not found in external service"));

            OrderProductDelivery orderProduct = new OrderProductDelivery();
            orderProduct.setOrderTransactionDeliveryId(orderDetail.getOrderTransactionDeliveryId());
            orderProduct.setProductId(String.valueOf(externalProduct.getId()));
            orderProduct.setQuantity(productDTO.getQuantity());
            orderProduct.setName(externalProduct.getData().getDescripcion());
            orderProduct.setUnitPrice(externalProduct.getData().getPrecio());

            orderProductRepository.save(orderProduct);
        }

        return orderDetail;
    }
    @Override
    public List<OrderDeliveryResponseDTO> getOrderDetails() {
        List<OrderDeliveryResponseDTO> orderResponses = orderRepository.getOrderDetail();

        List<Long> orderIds = orderResponses.stream()
                .map(OrderDeliveryResponseDTO::getOrderTransactionDeliveryId)
                .collect(Collectors.toList());

        List<OrderProductDeliveryResponseDTO> allProducts = orderRepository.getOrderDetailProduct(orderIds);

        Map<Long, List<SellProducts>> productsGroupedByOrder = allProducts.stream()
                .collect(Collectors.groupingBy(
                        OrderProductDeliveryResponseDTO::getOrderTransactionDeliveryId,
                        Collectors.mapping(op -> {
                            SellProducts product = new SellProducts();
                            product.setProductId(String.valueOf(op.getProductId()));
                            product.setProductName(op.getName());
                            product.setQuantity(op.getQuantity());
                            product.setPrice(op.getPrice());
                            return product;
                        }, Collectors.toList())
                ));

        for (OrderDeliveryResponseDTO dto : orderResponses) {
            dto.setProducts(productsGroupedByOrder.getOrDefault(dto.getOrderTransactionDeliveryId(), List.of()));
        }

        return orderResponses;
    }
    public OrderDetailDelivery updateOrderStatus(Long orderTransactionDeliveryId, OrderStatusDTO updateOrderStatusDTO) {
        OrderDetailDelivery orderDetail = orderRepository.findById(orderTransactionDeliveryId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        orderDetail.setStatusOrder(updateOrderStatusDTO.getOrderStatus());

        return orderRepository.save(orderDetail);
    }

    @Override
    public Boolean delete(Long id) {
        if (orderRepository.existsById(id)) {
            OrderDetailDelivery orderDetail = orderRepository.findById(id).get();
            orderDetail.setStatus("INACTIVE");
            orderRepository.save(orderDetail);
            return true;
        } else {
            throw new RuntimeException("El pedido no fue encontrado por el id " + id);
        }
    }
}
