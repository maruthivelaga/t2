package com.hospital.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for category-wise distribution counts
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDistributionDto {
    private String category;
    private Long count;
    private Double percentage;
}
