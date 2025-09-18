package com.gtalent.commerce.service.configs;

import com.gtalent.commerce.service.models.User;
import com.gtalent.commerce.service.repositories.UserRepository;
import com.gtalent.commerce.service.requests.LoginRequest;
import com.gtalent.commerce.service.services.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Component
public class JwtAuthService {

    @Autowired
    //jwtService -> 負責"產生"與"解析" JWT token
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    public String register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));  //將使用者的原始密碼加密後再存入資料庫
        userRepository.save(user);
        /* 將 User 物件存到資料庫
           1.如果是新使用者 → 會 INSERT
           2.如果已存在 → 會 UPDATE*/
        return jwtService.generateToken(user);  //註冊成功後，立即產生一個 JWT token 給前端作為授權使用
    }

    public String login(LoginRequest request) {
        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());  //根據前端傳來的 Email 查詢資料庫
        if (userOptional.isPresent()) {  //判斷資料庫是否有對應的使用者
            User user = userOptional.get();
            if(passwordEncoder.matches(request.getPassword(), user.getPassword())) {  //用 PasswordEncoder 將前端傳來的密碼與資料庫的加密密碼比對
                return jwtService.generateToken(user);  //密碼正確 -> 產生 JWT token 並回傳
            }
        }
        throw new RuntimeException("無效憑證");
    }
}
