package com.gtalent.commerce.service.controllers;

import com.gtalent.commerce.service.enums.OrderStatus;
import com.gtalent.commerce.service.requests.OrderRequest;
import com.gtalent.commerce.service.requests.PatchOrderRequest;
import com.gtalent.commerce.service.responses.OrderResponse;
import com.gtalent.commerce.service.responses.PendingOrderResponse;
import com.gtalent.commerce.service.services.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Order 功能-第一版", description = "提供訂單列表 CRUD 功能")
@RestController
@RequestMapping("/commerce-service/orders")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    //1.取得所有訂單
    @GetMapping
    @Operation(summary = "取得所有訂單列表", description = "列出所有訂單，含商品明細與金額")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功取得訂單列表")
    })
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        List<OrderResponse> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    //1.1 依訂單狀態篩選取得分頁列表
    @GetMapping("/orders/page")
    @Operation(summary = "取得訂單分頁列表",
            description = "列出所有訂單，支援分頁，可依訂單狀態篩選 (PENDING、DELIVERED、等)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功取得訂單分頁列表"),
            @ApiResponse(responseCode = "400", description = "輸入參數錯誤"),
            @ApiResponse(responseCode = "500", description = "伺服器內部錯誤")
    })
    public Page<OrderResponse> getOrdersByPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) OrderStatus status  //可選狀態
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return orderService.getOrdersByPage(status, pageable);
    }

    //2.取得單筆訂單
    @GetMapping("/{id}")
    @Operation(summary = "取得單筆訂單", description = "依訂單 ID 回傳訂單詳細資訊")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功取得訂單"),
            @ApiResponse(responseCode = "404", description = "找不到指定訂單")
    })
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable int id) {
        OrderResponse order = orderService.getOrderById(id);
        if (order == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();  //找不到返回 404
        }
        return ResponseEntity.status(HttpStatus.OK).body(order);
    }

    //3.建立訂單
    @PostMapping
    @Operation(summary = "建立新訂單", description = "建立新訂單，需包含使用者與至少一項商品")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "訂單建立成功"),
            @ApiResponse(responseCode = "400", description = "訂單資料不合法")
    })
    public ResponseEntity<OrderResponse> createOrder(@RequestBody @Valid OrderRequest orderRequest) {
        OrderResponse created = orderService.createOrder(orderRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    //4.部分更新訂單 -> 考慮到使用者不會去主動更新部分核心資訊，所以使用Patch而非Put
    @PatchMapping("/{id}")
    @Operation(summary = "部分更新訂單", description = "使用者可更新收貨地址或申請退貨")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "訂單更新成功"),
            @ApiResponse(responseCode = "404", description = "找不到指定訂單")
    })
    public ResponseEntity<OrderResponse> patchOrder(@PathVariable int id, @RequestBody PatchOrderRequest patchRequest) {
        OrderResponse updatedOrder = orderService.patchOrder(id, patchRequest);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    //5.刪除訂單
    @DeleteMapping("/orders/{id}")
    @Operation(summary = "刪除訂單", description = "標記訂單刪除時間，不會實體刪除")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "訂單刪除成功"),
            @ApiResponse(responseCode = "404", description = "找不到指定訂單")
    })
    public ResponseEntity<Void> deleteOrder(@PathVariable int id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();  //回傳 204 No Content
    }

    //6.待處理訂單
    @GetMapping("/pending")
    @Operation(summary = "取得待處理訂單列表",
            description = "列出所有待處理訂單 (狀態為 ORDERED)，包含商品明細與金額")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功取得待處理訂單列表"),
            @ApiResponse(responseCode = "500", description = "伺服器內部錯誤")
    })
    public List<PendingOrderResponse> getPendingOrders() {
        return orderService.getPendingOrders();
    }


}
