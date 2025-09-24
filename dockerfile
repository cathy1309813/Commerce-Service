# 1. 使用 Maven 官方提供的 OpenJDK 17 映像作為「建置階段（Builder）」基礎
#    這個映像裡面有完整的 JDK + Maven，可用來編譯 Java 專案
FROM maven:3.8-openjdk-17 AS builder

# 2. 設定建置階段的工作目錄為 /app
#    後續 COPY、RUN 指令都以這個目錄為基準
WORKDIR /app

# 3. 複製 Maven 專案的 pom.xml 到 /app 目錄
#    先複製 pom.xml 可以利用 Docker layer cache，減少重複下載依賴
COPY pom.xml .

# 4. 複製整個 src 目錄到 /app/src
COPY src ./src

# 5. 在容器內執行 Maven 打包命令
#    mvn clean package -DskipTests：清理舊 build，打包 JAR，跳過測試
RUN mvn clean package -DskipTests

# -----------------------------------------------------------------------------------------------------------

# 6. 使用 Eclipse Temurin 官方提供的 Java 17 JRE 映像作為「運行階段（Runtime）」基礎
#    只包含執行 Java 程式所需環境，不包含完整 JDK → 鏡像更小
FROM eclipse-temurin:17-jre

# 7. 設定運行階段的工作目錄為 /app
WORKDIR /app

# 8. 將建置階段生成的 JAR 複製到運行階段的 /app 目錄，並重新命名為 commerce-service.jar
#    --from=builder 表示從前面的 builder 階段取得檔案
COPY --from=builder /app/target/commerce.service-0.0.1-SNAPSHOT.jar /app/commerce-service.jar

# 9. 告訴 Docker 容器會監聽 8080 端口
EXPOSE 8080

# 10. 設定容器啟動時要執行的預設命令
#     java -jar commerce-service.jar：啟動 Spring Boot 應用
CMD ["java", "-jar", "commerce-service.jar"]

