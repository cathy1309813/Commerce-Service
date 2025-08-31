package com.gtalent.commerce.service.Controllers;

import com.gtalent.commerce.service.Requests.CreateUserRequest;
import com.gtalent.commerce.service.Requests.UpdateUserRequest;
import com.gtalent.commerce.service.Responses.CreateUserResponse;
import com.gtalent.commerce.service.Responses.UpdateUserResponse;
import com.gtalent.commerce.service.Responses.UserResponse;
import com.gtalent.commerce.service.models.User;
import com.gtalent.commerce.service.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/commerce-service/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    //1.取得所有使用者(查詢)
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        //從 Service 取得
        List<User> userList = userService.getAllUsers();
        //建立一空List回傳給前端
        List<UserResponse> response = new ArrayList<>();
        for (User user : userList) {
            UserResponse dto = new UserResponse(user.getId(), user.getFirstName(), user.getLastName());
            response.add(dto);
        }
        return ResponseEntity.ok(response);
    }

    //2.依照 ID 取得單一使用者
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable int id) {
        Optional<User> optionalUser = userService.getUserById(id);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); //404
        }
        User user = optionalUser.get(); // 取得 Optional 裡面的 User
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        return ResponseEntity.ok(response);  //200
    }

//    //練習lambda
//    @GetMapping("/{id}")
//    public ResponseEntity<UserResponse> getUserById(@PathVariable int id) {
//        return userService.getUserById(id)
//                .map(user -> new UserResponse(user.getId(), user.getFirstName(), user.getLastName()))
//                .map(ResponseEntity::ok)
//                .orElse(ResponseEntity.notFound().build());
//    }

    //3.新增使用者
    @PostMapping
    public ResponseEntity<CreateUserResponse> createUser(@RequestBody @Valid CreateUserRequest request) {
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
        user.setHasNewsletter(request.isHasNewsletter());
        //呼叫 Service 儲存方法
        User savedUser = userService.createUser(user);
        //建立 response (排除email等機密資料外洩疑慮)
        CreateUserResponse response = new CreateUserResponse(
                savedUser.getId(),
                savedUser.getFirstName(),
                savedUser.getLastName()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //4.更新使用者
    @PutMapping("/{id}")
    public ResponseEntity<UpdateUserResponse> updateUser(@PathVariable int id,
                                                         @RequestBody @Valid UpdateUserRequest request) {
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
        User updatedUser;
        Optional<User> updatedUserOptional = userService.updateUser(id, userToUpdate);
        if (updatedUserOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        updatedUser = updatedUserOptional.get();

        // 建立回傳 DTO
        UpdateUserResponse response = new UpdateUserResponse(
                updatedUser.getId(),
                updatedUser.getFirstName(),
                updatedUser.getLastName(),
                updatedUser.getEmail(),
                updatedUser.getBirthday(),
                updatedUser.getAddress(),
                updatedUser.getCity(),
                updatedUser.getState(),
                updatedUser.getZipcode(),
                updatedUser.isHasNewsletter()
        );

        return ResponseEntity.ok(response);
    }

    //5.刪除使用者
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable int id) {
        if (id <= 0) {
            //回傳400 Bad Request: ID 為負數或不合法
            return ResponseEntity.badRequest().build();
        }

        // 403

        boolean deleted = userService.deleteUserById(id); //true → 使用者存在且已刪除；false → 使用者不存在，沒辦法刪除
        if (!deleted) {
            //回傳404 Not Found: 找不到該用戶
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.noContent().build(); //204 無回應內容，代表刪除成功
    }

}
