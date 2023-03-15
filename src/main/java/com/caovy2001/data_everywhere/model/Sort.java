package com.caovy2001.data_everywhere.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Sort {
    private String field;

    @Builder.Default
    private org.springframework.data.domain.Sort.Direction direction = org.springframework.data.domain.Sort.Direction.ASC;
}
