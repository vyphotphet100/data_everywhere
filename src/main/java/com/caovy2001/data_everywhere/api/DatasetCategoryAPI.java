package com.caovy2001.data_everywhere.api;

import com.caovy2001.data_everywhere.command.dataset_category.CommandGetListDatasetCategory;
import com.caovy2001.data_everywhere.constant.ExceptionConstant;
import com.caovy2001.data_everywhere.entity.DatasetCategoryEntity;
import com.caovy2001.data_everywhere.entity.DatasetCollectionEntity;
import com.caovy2001.data_everywhere.model.ResponseModel;
import com.caovy2001.data_everywhere.model.pagination.Paginated;
import com.caovy2001.data_everywhere.service.dataset_category.IDatasetCategoryServiceAPI;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dataset_category")
public class DatasetCategoryAPI {
    @Autowired
    private IDatasetCategoryServiceAPI datasetCategoryServiceAPI;

    @PostMapping("/")
    public ResponseModel getPaginatedList(@RequestBody CommandGetListDatasetCategory command) {
        try {
            Paginated<DatasetCategoryEntity> datasetCategoryEntityPaginated = datasetCategoryServiceAPI.getPaginatedList(command);
            return ResponseModel.builder()
                    .payload(datasetCategoryEntityPaginated)
                    .status(ResponseModel.Status.builder().build())
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseModel.builder()
                    .status(ResponseModel.Status.builder()
                            .httpStatus(HttpStatus.EXPECTATION_FAILED)
                            .exceptionCode(StringUtils.isNotBlank(e.getMessage()) ? e.getMessage() : ExceptionConstant.error_occur)
                            .build())
                    .build();
        }
    }
}
