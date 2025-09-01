package com.gtalent.commerce.service.services;


import com.gtalent.commerce.service.Responses.LoginResponse;
import com.gtalent.commerce.service.Responses.UserResponse;
import com.gtalent.commerce.service.models.User;
import com.gtalent.commerce.service.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service  //處理商業邏輯 → 呼叫 Repository
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;  //1.在"pom.xml"中注入依賴 2.在configs中加入SecurityConfig並新增@Bean

    //注入建構子
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    //1.取得所有使用者
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(user -> new UserResponse(
                        user.getId(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getHasNewsletter()
                ))
                .collect(Collectors.toList());
    }

    //2.依照 ID 取得單一使用者
    public Optional<User> getUserById(int id) {  //Optional<> :回傳值可能不存在
        return userRepository.findById(id);
    }

    //3.新增使用者
    public User createUser(User user) {
        //所有欄位一定要填!
        if (user.getFirstName() == null || user.getLastName() == null || user.getEmail() == null ||
                user.getPassword() == null || user.getBirthday() == null || user.getAddress() == null ||
                user.getCity() == null || user.getState() == null || user.getZipcode() == null) {
            throw new IllegalArgumentException("所有欄位必填");
        }
        //Email是否重複
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email已存在");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("USER"); //預設為一般使用者
        return userRepository.save(user);
    }

    //4.更新使用者
    public Optional<User> updateUser(int id, User updatedUser) {
        Optional<User> user = userRepository.findById(id);
        if(user.isPresent()) {
            User existingUser = user.get(); //取得資料
            //更新欄位(不為空)
            if (updatedUser.getFirstName() != null) existingUser.setFirstName(updatedUser.getFirstName());
            if (updatedUser.getLastName() != null) existingUser.setLastName(updatedUser.getLastName());
            if (updatedUser.getEmail() != null) {
                if (userRepository.existsByEmailAndIdNot(updatedUser.getEmail(), id)) {
                    throw new IllegalArgumentException("Email 已存在");
                } existingUser.setEmail(updatedUser.getEmail());
            }
            if (updatedUser.getPassword() != null)
                existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
            if (updatedUser.getBirthday() != null) existingUser.setBirthday(updatedUser.getBirthday());
            if (updatedUser.getAddress() != null) existingUser.setAddress(updatedUser.getAddress());
            if (updatedUser.getCity() != null) existingUser.setCity(updatedUser.getCity());
            if (updatedUser.getState() != null) existingUser.setState(updatedUser.getState());
            if (updatedUser.getZipcode() != null) existingUser.setZipcode(updatedUser.getZipcode());
            //會員可以修改訂閱狀態
            if (updatedUser.getHasNewsletter() != null) existingUser.setHasNewsletter(updatedUser.getHasNewsletter());

            //儲存回資料庫
            User savedUser = userRepository.save(existingUser);
            return Optional.of(savedUser);
        } else {
            return Optional.empty();  //找不到使用者
        }
    }

    //5.刪除使用者
    public boolean deleteUserById(int id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;  //刪除成功
        } else {
            return false; // 使用者不存在
        }
    }

    //6.使用 email + password 登入並更新最後登入時間
    public Optional<LoginResponse> login(String email, String password) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent() && passwordEncoder.matches(password, user.get().getPassword())) {
            User existingUser = user.get();
            existingUser.setUpdateLoginTime(LocalDateTime.now());  //更新登入時間
            userRepository.save(existingUser);  //存回資料庫
            return Optional.of(new LoginResponse(
                    existingUser.getId(),
                    existingUser.getEmail(),
                    existingUser.getUpdateLoginTime()
            ));
        }
        return Optional.empty();  //帳號不存在或密碼錯誤
    }
}