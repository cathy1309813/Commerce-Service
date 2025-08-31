# E-Commerce Admin Dashboard API系統開發
根據https://marmelab.com/react-admin-demo，使用 Springboot+MySQL 開發管理後台  API 系統，並以 Swagger 呈現結果。

## 模組規劃
- 銷售模組
  - 訂單管理
  - 發票管理
- 產品模組
  - 產品管理
  - 產品類別管理
- 用戶模組
  - 用戶管理
  - 用戶標籤管理
- 評論模組


## 初始化專案

### SQL連線設定

#### 1. 預先建立本地資料庫
```
CREATE DATABASE commerce_db;
```
#### DB連線資訊
```
spring.datasource.url=jdbc:mysql://localhost:3306/inventory_db
spring.datasource.username=<username>
spring.datasource.password=<password>
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.hibernate.ddl-auto=update
spring.http.encoding.force=true
```


## 資料表設計

### 1. ***users***
| 欄位名稱     | 資料型態                           | 說明                           |
|--------------|------------------------------------|--------------------------------|
| id           | INT AUTO_INCREMENT PRIMARY KEY     | 使用者唯一識別碼               |
| first_name   | VARCHAR(50)                        | 用戶名稱                       |
| last_name    | VARCHAR(50)                        | 姓氏                           |
| email        | VARCHAR(100) UNIQUE NOT NULL       | 信箱                           |
| birthday     | DATE                               | 生日 (年/月/日)                |
| address      | VARCHAR(100)                       | 地址                           |
| city         | VARCHAR(50)                        | 城市                           |
| state        | VARCHAR(50)                        | 州/省                          |
| zipcode      | VARCHAR(20)                        | 郵遞區號                       |
| password     | VARCHAR(255) NOT NULL              | 密碼 (加密儲存)                |
| created_at   | DATETIME DEFAULT CURRENT_TIMESTAMP | 帳號建立時間                   |
| updated_at   | DATETIME DEFAULT CURRENT_TIMESTAMP | 用戶資料最後更新時間           |

### 2. ***user_segments***
| 欄位名稱       | 資料型態  | 說明                              |
|----------------|-----------|-----------------------------------|
| user_id        | INT       | 使用者 ID，對應 `users(id)`       |
| segment_id     | INT       | 區段 ID，對應 `segments(id)`      |
| has_newsletter | BOOLEAN DEFAULT FALSE | 是否訂閱電子報            |

🔑 主鍵 (PRIMARY KEY)： (user_id, segment_id)  
🔗 外鍵 (FOREIGN KEY)：
- user_id → users(id)
- segment_id → segments(id)  