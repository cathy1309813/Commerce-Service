package com.gtalent.commerce.service.services;

import com.gtalent.commerce.service.enums.OrderStatus;
import com.gtalent.commerce.service.exceptions.OrderNotFoundException;
import com.gtalent.commerce.service.models.Order;
import com.gtalent.commerce.service.models.OrderItem;
import com.gtalent.commerce.service.models.Product;
import com.gtalent.commerce.service.models.User;
import com.gtalent.commerce.service.repositories.OrderRepository;
import com.gtalent.commerce.service.repositories.ProductRepository;
import com.gtalent.commerce.service.repositories.UserRepository;
import com.gtalent.commerce.service.requests.OrderRequest;
import com.gtalent.commerce.service.requests.PatchOrderRequest;
import com.gtalent.commerce.service.requests.OrderItemRequest;
import com.gtalent.commerce.service.responses.OrderItemResponse;
import com.gtalent.commerce.service.responses.OrderResponse;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderItemService orderItemService;

    public OrderService(OrderRepository orderRepository, UserRepository userRepository,
                        ProductRepository productRepository, OrderItemService orderItemService) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.orderItemService = orderItemService;
    }

    //1.取得所有訂單列表
    public List<OrderResponse> getAllOrders() {
        List<Order> orders = orderRepository.findAll();  //先取得訂單
        List<OrderResponse> responses = new ArrayList<>();  //建立一個空List
        for (Order order : orders) {
        responses.add(convertToOrderResponse(order));
        }
        return responses;
    }

    //2.取得單筆訂單
    public OrderResponse getOrderById(int id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("找不到 ID 為 " + id + " 的訂單"));
        return convertToOrderResponse(order);
    }

    //3.建立訂單
    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        //1.檢查基本欄位
        if (request.getShippingAddress() == null || request.getShippingAddress().isBlank()) {
            throw new IllegalArgumentException("收貨地址不得為空");
        }
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("訂單中至少要有一項商品");
        }
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("找不到對應的使用者"));  //確認使用者

        //2.建立訂單
        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(request.getShippingAddress());
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus(OrderStatus.ORDERED);
        order.setReturned(false);  // 預設未退貨

        String reference = "ORD" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        order.setOrderReference(reference);

        //3.建立訂單明細
        List<OrderItem> orderItems = new ArrayList<>();
        for (OrderItemRequest itemReq : request.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "找不到商品 ID: " + itemReq.getProductId()
                    ));

            if (itemReq.getQuantity() <= 0) {
                throw new IllegalArgumentException("商品數量必須大於 0");
            }

            //建立訂單項目
            OrderItem item = new OrderItem();
            item.setOrder(order);               //與訂單關聯
            item.setProduct(product);           //與商品關聯（外鍵）
            item.setQuantity(itemReq.getQuantity());
            item.setPrice(product.getPrice());  //從資料庫設定價格
            item.setProductName(product.getReference());
            item.setDate(LocalDate.now());      //系統自動填入日期
            orderItems.add(item);
        }
        order.setItems(orderItems);

        //4.在 createOrder Service 儲存 Order 時，先給 totalAmount、deliveryFee、taxAmount
        //一個非 null 的初值（例如小計、0、0），避免資料庫報錯
        BigDecimal totalAmount = orderItems.stream()
                .map(i -> i.getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setTotalAmount(totalAmount);
        order.setDeliveryFee(BigDecimal.ZERO);  //暫時
        order.setTaxAmount(BigDecimal.ZERO);    //暫時

        //5.儲存訂單
        Order savedOrder = orderRepository.save(order);

        //6.轉換成 Response
        OrderResponse response = new OrderResponse();
        response.setId(savedOrder.getId());
        response.setUserName(savedOrder.getUser().getFirstName() + " " + savedOrder.getUser().getLastName());
        response.setUserEmail(savedOrder.getUser().getEmail());
        response.setOrderReference(savedOrder.getOrderReference());
        response.setShippingAddress(savedOrder.getShippingAddress());
        response.setCreatedAt(savedOrder.getCreatedAt());
        response.setStatus(savedOrder.getStatus());
        response.setReturned(savedOrder.isReturned());

        //套用 mapToDto 將每個 OrderItem 轉成 OrderItemResponse
        List<OrderItemResponse> itemResponses = savedOrder.getItems().stream()
                .map(i -> orderItemService.mapToDto(i, savedOrder.getCreatedAt()))
                .peek(OrderItemResponse::calculateTotal)   //確保每個 item total 先計算
                .toList();
        response.setItems(itemResponses);

        //7.計算 totals
        orderItemService.calculateTotals(response);

        return response;
    }

    //4.部分更新訂單
    public OrderResponse patchOrder(int id, PatchOrderRequest patchRequest) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("找不到 ID 為 " + id + " 的訂單"));

        if (patchRequest.getShippingAddress() != null) {
            order.setShippingAddress(patchRequest.getShippingAddress());
        }
        if (patchRequest.getReturned() != null) {
            order.setReturned(patchRequest.getReturned());  //系統可自行判斷退貨流程
            //當前訂單狀態是 DELIVERED (已送達) ，如果用戶申請退貨 (returned = true) ，則把訂單狀態改成 CANCELLED，
            //這樣用戶退貨時，狀態自動反映 -> 訂單已取消/退貨
            if (patchRequest.getReturned() && order.getStatus() == OrderStatus.DELIVERED) {
                order.setStatus(OrderStatus.CANCELLED);
            }
        }
        //處理狀態更新
        if (patchRequest.getStatus() != null) {  //先判斷前端是否有傳送新的 status -> 如果有就繼續往下走
            OrderStatus newStatus = patchRequest.getStatus();  //newStatus 希望更新的訂單狀態
            OrderStatus current = order.getStatus();  //current 目前訂單的狀態

            boolean canUpdateStatus = false;  //先假設「不允許變更」
            if (current == OrderStatus.ORDERED) {
                canUpdateStatus = (newStatus == OrderStatus.DELIVERED || newStatus == OrderStatus.CANCELLED);
            } else if (current == OrderStatus.DELIVERED) {
                canUpdateStatus = (newStatus == OrderStatus.CANCELLED);
            }
            //如果符合規則 (canUpdateStatus = true) 就更新狀態；不符合就拋出例外
            if (canUpdateStatus) {
                order.setStatus(newStatus);
            } else {
                throw new IllegalArgumentException(
                        "不允許的狀態轉換: " + order.getStatus() + " → " + newStatus);
            }
        }
        Order savedOrder = orderRepository.save(order);

        OrderResponse response = convertToOrderResponse(savedOrder);
        orderItemService.calculateTotals(response);

        return response;
    }

    //5.刪除訂單
    public void deleteOrder(int id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("找不到 ID 為 " + id + " 的訂單"));
        order.setDeletedAt(LocalDateTime.now());  //標記刪除時間
        orderRepository.save(order);
    }

    //將 Order 轉換成 DTO
    private OrderResponse convertToOrderResponse(Order order) {
        OrderResponse response = new OrderResponse();  //創建一個空的 OrderResponse 物件
        response.setId(order.getId());  //將 Order 的基本資料填入 OrderResponse
        response.setOrderReference(order.getOrderReference());
        response.setUserName(order.getUser().getFirstName() + " " + order.getUser().getLastName());
        response.setCreatedAt(order.getCreatedAt());
        response.setStatus(order.getStatus());
        response.setReturned(order.isReturned());
        response.setShippingAddress(order.getUser().getAddress());

        //轉換成 OrderItem
        List<OrderItemResponse> items = new ArrayList<>();
        for (OrderItem item : order.getItems()) {  //遍歷訂單中的每個商品明細
            OrderItemResponse itemResponse = orderItemService.mapToDto(item, order.getCreatedAt());
            //將每個 OrderItem 傳給 orderItemService 的 mapToDto 方法，並帶入 order.getCreatedAt() 作為下單日期。
            items.add(itemResponse);  //每個轉換好的 OrderItemResponse 都放入 items 清單
        }
        response.setItems(items);  //最後將整個商品明細列表設置到 OrderResponse

        //計算總金額
        orderItemService.calculateTotals(response);

        return response;
    }

    /* @Transactional
       用途:管理 資料庫交易 (Transaction)。
       說明:
       1.開啟一個資料庫交易 (Transaction)。
       2.方法內的所有資料庫操作 (Repository.save、update、delete 等) 都在同一個交易中。
       3.如果方法正常執行完畢 → 自動提交 (Commit)。
       4.如果方法發生例外 → 自動回滾 (Rollback) ，資料庫不會留下一半更新的資料如果任一操作失敗，整個訂單與明細都不會被寫入資料庫。
       目的:保證資料一致性和完整性。*/
}
