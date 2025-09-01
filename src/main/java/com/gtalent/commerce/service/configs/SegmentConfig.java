package com.gtalent.commerce.service.configs;

import com.gtalent.commerce.service.enums.SegmentType;
import com.gtalent.commerce.service.models.Segment;
import com.gtalent.commerce.service.repositories.SegmentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SegmentConfig {
    @Bean
    //CommandLineRunner -> 是 Spring Boot 提供的一個接口，啟動完成後自動執行方法
    public CommandLineRunner initSegments(SegmentRepository segmentRepository) {
        return args -> {
            for (SegmentType type : SegmentType.values()) {  //遍歷 Enum SegmentType 的所有常數
                if (!segmentRepository.existsByName(type.getName())) {
                    Segment segment = new Segment();
                    segment.setName(type.getName());  //檢查資料庫是否已有該 segment
                    segmentRepository.save(segment);  //如果沒有就建立新的 Segment 實體並存入資料庫
                }
            }
        };
    }
}
