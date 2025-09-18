package com.gtalent.commerce.service.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Entity
@Table(name = "users")
@Data                 //Lombok: 自動產生所有欄位的 getter、setter、toString()
@NoArgsConstructor    //Lombok: 產生 無參數建構子
@AllArgsConstructor   //Lombok: 產生 全參數建構子
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //自動生成ID
    private int id;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(name = "email", unique = true, nullable = false, length = 100)
    private String email;

    @Column(name = "birthday", nullable = false)
    private LocalDate birthday;

    @Column(name = "address", nullable = false , length = 100)
    private String address;

    @Column(name = "city", nullable = false , length = 50)
    private String city;

    @Column(name = "state", nullable = false , length = 50)
    private String state;

    @Column(name = "zipcode", nullable = false , length = 20)
    private String zipcode;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "role")
    private String role;

    @Column(name = "has_newsletter", nullable = false)
    private Boolean hasNewsletter = false;  //默認為未訂閱 (false)

    @CreationTimestamp //自動填充實體的建立時間
    @Column(name = "first_seen", updatable = false)
    private LocalDateTime firstLoginTime;

    @Column(name = "last_seen")  //使用者登入或活動時間
    private LocalDateTime updateLoginTime;

    @UpdateTimestamp
    @Column(name = "updated_at")  //資料異動時間
    private LocalDateTime updateTime;

    // 一個 User 對應到多個 UserSegment
    /* cascade = CascadeType.ALL:
       當你對 父實體 做某些操作時，對應的 子實體 也會自動執行相同操作。
       PERSIST、MERGE、REMOVE、REFRESH、DETACH。*/
    /* fetch = FetchType.LAZY:
       1.關聯資料不會在主實體查詢時立即被讀取，而是等到你真正 訪問關聯屬性 時才去資料庫查詢。
       2.減少不必要的資料庫查詢以提高效能。*/
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<UserSegment> userSegments;
    /* fetch = FetchType.EAGER:
       1.立即載入：只要查 User，JPA 會自動同時抓出所有關聯的 UserSegment。
       2.適用情境：關聯資料量小，幾乎每次都需要用到時方便。*/

}
