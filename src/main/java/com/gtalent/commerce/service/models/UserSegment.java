package com.gtalent.commerce.service.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_segments")
@Data
@NoArgsConstructor
@AllArgsConstructor
//users 和 segments 是多對多，中間由 user_segments 表格具體實現
//user_segments 有額外欄位 has_newsletter
public class UserSegment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // 關聯到 User
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 關聯到 Segment
    @ManyToOne
    @JoinColumn(name = "segment_id", nullable = false)
    private Segment segment;

    // 額外欄位
    @Column(name = "has_newsletter", nullable = false)
    private boolean hasNewsletter = false;
}
