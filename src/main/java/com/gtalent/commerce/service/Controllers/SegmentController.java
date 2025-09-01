package com.gtalent.commerce.service.Controllers;

import com.gtalent.commerce.service.models.Segment;
import com.gtalent.commerce.service.repositories.SegmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController  //物件轉成 JSON
@RequestMapping("/commerce-service/segments")
public class SegmentController {
    @Autowired
    private SegmentRepository segmentRepository;

    @GetMapping
    public ResponseEntity<List<Segment>> getAllSegments() {
        List<Segment> segments = segmentRepository.findAll();
        return ResponseEntity.ok(segments);
    }
}
