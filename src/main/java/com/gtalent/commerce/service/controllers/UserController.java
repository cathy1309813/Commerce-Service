package com.gtalent.commerce.service.controllers;

import com.gtalent.commerce.service.models.UserSegment;
import com.gtalent.commerce.service.requests.CreateUserRequest;
import com.gtalent.commerce.service.requests.UpdateUserRequest;
import com.gtalent.commerce.service.responses.CreateUserResponse;
import com.gtalent.commerce.service.responses.UpdateUserResponse;
import com.gtalent.commerce.service.responses.UserResponse;
import com.gtalent.commerce.service.models.User;
import com.gtalent.commerce.service.responses.UserSegmentResponse;
import com.gtalent.commerce.service.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Tag(name = "使用者功能-第一版", description = "提供使用者功能 API")
@RestController
@RequestMapping("/commerce-service/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    //1.取得所有使用者(不分頁)
    @GetMapping
    @Operation(summary = "取得所有使用者 (不分頁)",
            description = "一次回傳系統中所有已註冊的使用者清單 (適合資料量少的情境)。")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功取得使用者清單"),
            @ApiResponse(responseCode = "400", description = "輸入錯誤"),
            @ApiResponse(responseCode = "500", description = "伺服器內部錯誤")
    })
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        //從 Service 取得
        List<UserResponse> userList = userService.getAllUsers();
        return ResponseEntity.ok(userList);
    }

    //1.1 取得所有使用者(分頁)
    @GetMapping
    @Operation(summary = "取得使用者清單（分頁 + 搜尋）",
            description = "可依名字、Email、是否訂閱電子報、Segment 過濾")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功取得分頁使用者清單"),
            @ApiResponse(responseCode = "400", description = "輸入錯誤"),
            @ApiResponse(responseCode = "500", description = "伺服器內部錯誤")
    })
    public Page<UserResponse> getAllUserPages(@RequestParam(defaultValue = "0") int page,  //page -> 要查詢的頁碼
                                              @RequestParam(defaultValue = "10") int size,  //size -> 每頁要顯示的筆數
                                              @RequestParam(defaultValue = "") String query,  //搜尋 名字 關鍵字
                                              @RequestParam(required = false) Boolean hasNewsletter,  //
                                              @RequestParam(required = false) Integer segmentId
    ) {
        /*@RequestParam
          用途:過濾、排序、條件查詢；
          常見HTTP方法:GET;
         */
        //建立 Pageable 物件，用於分頁查詢
        Pageable pageRequest = PageRequest.of(page, size);  //建立一個分頁請求物件，指定要查哪一頁和每頁筆數
        Page<User> usersPage = userService.getAllUserPages(query, hasNewsletter, segmentId, pageRequest);  //把這個分頁請求傳給 service，取得對應的分頁資料
        /*Pageable
          是一個介面 (interface)，用來描述 分頁資訊。
          例如:想要查第幾頁、第幾筆、排序方式等等，都會用 Pageable 來封裝。*/
        /* PageRequest
           是 Pageable 的一個實作類別，專門用來建立"分頁資訊"。 */
        /* of
           是它的靜態方法，用來創建一個"分頁物件"。 */
        /* Page
           是 Spring Data JPA 提供的介面，用來表示"分頁結果"。 */

        List<UserResponse> userResponses = new ArrayList<>();
        for (User user : usersPage.getContent()) {
            UserResponse ur = new UserResponse();
            ur.setId(user.getId());
            ur.setFirstName(user.getFirstName());
            ur.setLastName(user.getLastName());
            ur.setHasNewsletter(user.getHasNewsletter());

            // 填充 segments
            List<UserSegmentResponse> segmentResponses = new ArrayList<>();
            for (UserSegment us : user.getUserSegments()) {
                segmentResponses.add(new UserSegmentResponse(us));
            }
            ur.setSegments(segmentResponses);
            userResponses.add(ur);
        }
        return new PageImpl<>(userResponses, pageRequest, usersPage.getTotalElements());
        /* PageImpl<>
           是 Spring Data JPA 提供的一個"分頁實作類別"，實作了 Page<T> 介面。 */
        /* UserResponse
           是分頁裡的內容列表，也就是剛剛用 for-loop 轉換好的使用者資料。 */
        /* Pageable
           描述"分頁請求的資訊"，如:第幾頁 (page)、每頁幾筆 (size)、排序方式(如果有的話)。*/
        /* usersPage.getTotalElements()
           即"從資料庫取得的分頁結果"中的"符合條件的總筆數"。*/
    }

    //2.依照 ID 取得單一使用者
    @GetMapping("/{id}")
    @Operation(summary = "依照 ID 取得單一使用者",description = "依照使用者 ID 取得單一使用者資訊。")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功取得使用者"),
            @ApiResponse(responseCode = "404", description = "使用者不存在"),
            @ApiResponse(responseCode = "500", description = "伺服器內部錯誤")
    })
    public ResponseEntity<UserResponse> getUserById(@PathVariable int id) {
        Optional<User> optionalUser = userService.getUserById(id);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404 找不到請求的資源
        }
        User user = optionalUser.get(); // 取得 Optional 裡面的 User
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setHasNewsletter(user.getHasNewsletter());
        return ResponseEntity.ok(response);  //200
    }

//    //練習lambda
//    @GetMapping("/{id}")
//    public ResponseEntity<UserResponse> getUserById(@PathVariable int id) {
//        return userService.getUserById(id)
//                .map(user -> new UserResponse(user.getId(), user.getFirstName(), user.getLastName(),
//                        user.getHasNewsletter()))
//                .map(ResponseEntity::ok)
//                .orElse(ResponseEntity.notFound().build());
//    }

    //3.新增使用者
    @PostMapping
    @Operation(summary = "新增使用者",
               description = "新增一筆使用者資料<br />" +
                             "注意：目前尚未加入權限檢查。")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "成功新增使用者"),
            @ApiResponse(responseCode = "400", description = "輸入欄位驗證失敗"),
            @ApiResponse(responseCode = "500", description = "伺服器內部錯誤")
    })
    public ResponseEntity<CreateUserResponse> createUser(@RequestBody @Valid CreateUserRequest request) {
        try {
            //DTO CreateUserRequest
            User user = new User();
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setEmail(request.getEmail());
            user.setPassword(request.getPassword());
            user.setBirthday(request.getBirthday());
            user.setAddress(request.getAddress());
            user.setCity(request.getCity());
            user.setState(request.getState());
            user.setZipcode(request.getZipcode());
            user.setHasNewsletter(request.getHasNewsletter());
            //呼叫 Service 儲存方法
            User savedUser = userService.createUser(user);
            //建立 response DTO(排除email等機密資料外洩疑慮)
            CreateUserResponse response = new CreateUserResponse(
                    savedUser.getId(),
                    savedUser.getFirstName(),
                    savedUser.getLastName(),
                    savedUser.getHasNewsletter()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(response); //201 新增成功
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();  //400
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();  //500
        }
    }
    //@Valid = 「請幫我驗證這個物件的內容」-> 針對 CreateUserRequest 及 UpdateUserRequest 裡面的物件進行驗證。

    //4.更新使用者
    @PutMapping("/{id}")
    @Operation(
            summary = "更新使用者資料",
            description = "依照使用者 ID 更新使用者資訊。可修改姓名、Email、密碼、生日、地址、城市、州/省、郵遞區號及訂閱狀態。<br />" +
                          "注意：目前尚未加入權限檢查。")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功更新使用者"),
            @ApiResponse(responseCode = "400", description = "輸入欄位驗證失敗，例如 Email 已存在"),
            @ApiResponse(responseCode = "404", description = "找不到指定的使用者"),
            @ApiResponse(responseCode = "500", description = "伺服器內部錯誤")
    })
    public ResponseEntity<UpdateUserResponse> updateUser(@PathVariable int id,
                                                         @RequestBody @Valid UpdateUserRequest request) {
        try {
            //將前端送來的欄位放入新的 User 物件
            User userToUpdate = new User();
            userToUpdate.setFirstName(request.getFirstName());
            userToUpdate.setLastName(request.getLastName());
            userToUpdate.setEmail(request.getEmail());
            userToUpdate.setPassword(request.getPassword());
            userToUpdate.setBirthday(request.getBirthday());
            userToUpdate.setAddress(request.getAddress());
            userToUpdate.setCity(request.getCity());
            userToUpdate.setState(request.getState());
            userToUpdate.setZipcode(request.getZipcode());
            userToUpdate.setHasNewsletter(request.isHasNewsletter());

            // 呼叫 Service 層更新使用者資料
            Optional<User> updatedUser = userService.updateUser(id, userToUpdate);
            if (updatedUser.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();  //404
            }
            User newUpdatedUser = updatedUser.get();

            // 建立回傳 DTO
            UpdateUserResponse response = new UpdateUserResponse(
                    newUpdatedUser.getId(),
                    newUpdatedUser.getFirstName(),
                    newUpdatedUser.getLastName(),
                    newUpdatedUser.getEmail(),
                    newUpdatedUser.getBirthday(),
                    newUpdatedUser.getAddress(),
                    newUpdatedUser.getCity(),
                    newUpdatedUser.getState(),
                    newUpdatedUser.getZipcode(),
                    newUpdatedUser.getHasNewsletter()
            );
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();  //400
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();  //500
        }
    }

    //5.刪除使用者
    @DeleteMapping("/{id}")
    @Operation(
            summary = "刪除使用者",
            description = "依照使用者 ID 刪除該使用者。<br />" +
                          "注意：目前尚未加入權限檢查。。")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "使用者刪除成功"),
            @ApiResponse(responseCode = "400", description = "ID 不合法"),
            @ApiResponse(responseCode = "403", description = "沒有刪除權限"),
            @ApiResponse(responseCode = "404", description = "找不到指定的使用者"),
            @ApiResponse(responseCode = "500", description = "伺服器內部錯誤")
    })
    public ResponseEntity<Void> deleteUser(@PathVariable int id) {
        if (id <= 0) {
            return ResponseEntity.badRequest().build();  //回傳400 Bad Request: ID 為負數或不合法
        }
        // TODO: 403 權限檢查，例如只允許管理者刪除

        boolean deleted = userService.deleteUserById(id);  //true → 使用者存在且已刪除；false → 使用者不存在，沒辦法刪除
        if (!deleted) {
            //回傳404 Not Found: 找不到該用戶
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.noContent().build();  //204 無回應內容，代表刪除成功
    }

}
