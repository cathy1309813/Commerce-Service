package com.gtalent.commerce.service.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "user_segments",
        //同一個 user 不能對應同一個 segment 多次
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "segment_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
/*
 * users 和 segments 是多對多關聯，
 * 中間由 user_segments 表格具體實現。
 * user_segments 有額外欄位 has_newsletter
 */
public class UserSegment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    //關聯到 User
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    //關聯到 Segment
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "segment_id", nullable = false)
    private Segment segment;
}
