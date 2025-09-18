package com.gtalent.commerce.service.configs;

import com.gtalent.commerce.service.models.User;
import com.gtalent.commerce.service.repositories.SegmentRepository;
import com.gtalent.commerce.service.repositories.UserRepository;
import com.gtalent.commerce.service.services.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    //過濾請求/賦予權限
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserRepository userRepository;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        // 從 http headers 中獲取 Authorization 欄位 -> Bearer ......
        String authHeader = request.getHeader("Authorization");
        // 檢查 Authorization 格式是否正確
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // 該次請求過濾器結束生命週期 -> 將請求繼續往下傳遞...
            filterChain.doFilter(request, response);
            return;
        }
        // 解析 JWT 取得 email -> 若開頭格式 (Bearer...)正確，則擷取第七字元開始的字串 (實際jwt)
        String jwtToken = authHeader.substring(7);
        String email = jwtService.getEmailFromToken(jwtToken);
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // 從資料庫查使用者並建立 Authentication -> db裡面找到對應的 username
            Optional<User> user = userRepository.findByEmail(email);
            if (user.isPresent()) {
                // *** 若使用 Spring Security (library) 必須包含 授權 (Authorization) 邏輯 -> "該用戶能做什麼?" ***
                List<? extends GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(user.get().getRole()));
                // 該 token 並非 jwt token，而是 Spring Security 內部使用的 token (包含 user & authorities)
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        user.get(), null, authorities);
                // 將 內部使用的 token 投進 Spring Security 令牌認證箱 (SecurityContextHolder)
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        // Filter 放行 -> 無論有沒有驗證成功，最後都會繼續傳遞給下一個 Filter
        filterChain.doFilter(request, response);
    }
}
