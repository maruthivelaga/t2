package com.hospital.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for numeric aggregates (min, max, avg, sum)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NumericAggregatesDto {
    private Double average;
    private Double minimum;
    private Double maximum;
    private Double sum;
    private Long recordsWithValues;  // Count of non-null values
}
