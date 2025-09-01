package com.gtalent.commerce.service.enums;


public enum SegmentType {  //Enum -> 固定常數集合

    //定義每個 Enum 常數，同時指定 id 和 name
    SEGMENT1(1, "SEGMENT1"),
    SEGMENT2(2, "SEGMENT2"),
    SEGMENT3(3, "SEGMENT3"),
    SEGMENT4(4, "SEGMENT4"),
    SEGMENT5(5, "SEGMENT5"),
    SEGMENT6(6, "SEGMENT6");

    private final int id;  //Enum是不可變的 -> 不可修改所以用final
    private final String name;

    SegmentType(int id, String name) {  //final屬性的欄位必須在建構子初始化
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    //靜態方法的核心作用就是 提供反查功能，讓 Enum 可以依照屬性(id、name)快速找到對應的常數
    public static SegmentType fromId(int id) {
        for (SegmentType s : SegmentType.values()) {
            if (s.getId() == id) {
                return s;
            }
        }
        throw new IllegalArgumentException("Invalid id: " + id);
    }

    public static SegmentType fromName(String name) {
        for (SegmentType s : SegmentType.values()) {
            if (s.getName().equals(name)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Invalid name: " + name);
    }
}
