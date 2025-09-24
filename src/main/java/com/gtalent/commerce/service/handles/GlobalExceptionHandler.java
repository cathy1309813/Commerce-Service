package com.gtalent.commerce.service.handles;

import com.gtalent.commerce.service.responses.ErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice  //@ControllerAdvice -> 全域例外處理器，會自動攔截所有 Controller 中拋出的例外
public class GlobalExceptionHandler {

    /* @ExceptionHandler
       說明 -> 是 Spring MVC 提供的例外處理註解。
       定義 -> 當程式拋出指定例外時，會自動呼叫的方法。
       目的 -> 將程式中發生的例外，統一處理並回傳給前端。
       用法 -> 用 {} 包住同時處理多種例外。*/

    //400 Bad Request -> IllegalArgumentException -> 非法參數或資料驗證失敗
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(IllegalArgumentException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    //404 Not Found -> EntityNotFoundException -> 找不到資源
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(EntityNotFoundException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    //409 Conflict -> DataIntegrityViolationException -> 資料衝突（如重複名稱）
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleConflict(DataIntegrityViolationException ex) {
        return buildErrorResponse("資料衝突: " + ex.getMessage(), HttpStatus.CONFLICT);
    }

    //500 Internal Server Error -> Exception -> 其他未知錯誤
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAll(Exception ex) {
        return buildErrorResponse("系統錯誤: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //建立統一 ErrorResponse
    private ResponseEntity<ErrorResponse> buildErrorResponse(String message, HttpStatus status) {
        //buildErrorResponse → 是用來建立統一錯誤回應
        ErrorResponse error = new ErrorResponse(
                status.value(),  //取得 HTTP 狀態碼的數值
                message,  //將例外訊息填入 message 欄位
                LocalDateTime.now()  //設定當前時間，記錄錯誤發生的時間
        );
        return ResponseEntity.status(status).body(error);  //設定 HTTP 回應狀態碼，將 ErrorResponse 物件放入回應 body
    }
    /* ResponseEntity
       定義 -> 是 Spring 提供的 HTTP 回應封裝類別，可以設定：
       1.HTTP 狀態碼 (status code)
       2.回傳內容 (body)
       3.回傳標頭 (header)*/

}
