package com.caovy2001.data_everywhere.api;

import com.caovy2001.data_everywhere.entity.DatasetCategoryEntity;
import com.caovy2001.data_everywhere.repository.DatasetCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tool")
public class Tool {
    @Autowired
    private DatasetCategoryRepository datasetCategoryRepository;

    @PostMapping("/add_fake_dataset_category")
    public boolean addFakeDatasetCategory(@RequestBody DatasetCategoryEntity datasetCategory) {
        try {
            datasetCategoryRepository.insert(datasetCategory);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }
}
