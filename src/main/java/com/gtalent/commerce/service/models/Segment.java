package com.gtalent.commerce.service.models;

import jakarta.persistence.*;
import jdk.jfr.DataAmount;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Entity
@Table(name = "segments")
@Data                 //Lombok: 自動產生所有欄位的 getter、setter、toString()
@NoArgsConstructor    //Lombok: 產生 無參數建構子
@AllArgsConstructor   //Lombok: 產生 全參數建構子
public class Segment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //自動生成ID
    private int id;

    @Column(name = "names", nullable = false, unique = true, length = 50)
    private String name;

    @OneToMany(mappedBy = "segment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserSegment> userSegments;
}
