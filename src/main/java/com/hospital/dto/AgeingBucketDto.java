package com.hospital.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for ageing analysis buckets
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgeingBucketDto {
    private String bucket;
    private Long count;
    private Double percentage;
}
