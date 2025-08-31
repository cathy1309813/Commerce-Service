package com.gtalent.commerce.service.services;


import com.gtalent.commerce.service.models.User;
import com.gtalent.commerce.service.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

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
    public List<User> getAllUsers() {
        return userRepository.findAll();
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
            throw new RuntimeException("All fields must be filled");
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
            if (updatedUser.getEmail() != null) existingUser.setEmail(updatedUser.getEmail());
            if (updatedUser.getPassword() != null)
                existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
            if (updatedUser.getBirthday() != null) existingUser.setBirthday(updatedUser.getBirthday());
            if (updatedUser.getAddress() != null) existingUser.setAddress(updatedUser.getAddress());
            if (updatedUser.getCity() != null) existingUser.setCity(updatedUser.getCity());
            if (updatedUser.getState() != null) existingUser.setState(updatedUser.getState());
            if (updatedUser.getZipcode() != null) existingUser.setZipcode(updatedUser.getZipcode());
            //會員可以修改訂閱狀態
            existingUser.setHasNewsletter(updatedUser.isHasNewsletter());

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
}
