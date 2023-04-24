package com.caovy2001.data_everywhere.command.dataset_category;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommandGetListDatasetCategory {
    private int page = 0;
    private int size = 0;
}
