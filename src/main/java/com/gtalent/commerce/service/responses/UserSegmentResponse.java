package com.gtalent.commerce.service.responses;

import com.gtalent.commerce.service.enums.SegmentType;
import com.gtalent.commerce.service.models.UserSegment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSegmentResponse {
    private int id;
    private int userId;
    private int segmentId;
    private String segmentName;  //對應 SegmentType Enum 的名稱

    //用 UserSegment 實體建立建構子
    public UserSegmentResponse(UserSegment userSegment) {
        this.id = userSegment.getId();
        this.userId = userSegment.getUser().getId();
        this.segmentId = userSegment.getSegment().getId();

        //用 Enum 反查名稱
        this.segmentName = SegmentType.fromId(this.segmentId).getName();
    }


}
