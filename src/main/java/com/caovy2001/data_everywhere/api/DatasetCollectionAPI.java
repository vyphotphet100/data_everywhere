package com.caovy2001.data_everywhere.api;

import com.caovy2001.data_everywhere.command.dataset_collection.CommandGetDatasetCollection;
import com.caovy2001.data_everywhere.command.dataset_collection.CommandGetListDatasetCollection;
import com.caovy2001.data_everywhere.constant.ExceptionConstant;
import com.caovy2001.data_everywhere.entity.DatasetCollectionEntity;
import com.caovy2001.data_everywhere.entity.UserEntity;
import com.caovy2001.data_everywhere.model.ResponseModel;
import com.caovy2001.data_everywhere.model.pagination.Paginated;
import com.caovy2001.data_everywhere.service.dataset_collection.IDatasetCollectionServiceAPI;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@RestController
@RequestMapping("/dataset_collection")
public class DatasetCollectionAPI extends BaseAPI {
    @Autowired
    private IDatasetCollectionServiceAPI datasetCollectionServiceAPI;

    @PostMapping("/")
    public ResponseModel getPaginatedList(@RequestBody CommandGetListDatasetCollection command) {
        try {
            Paginated<DatasetCollectionEntity> datasetCollectionEntitiesPaginated = datasetCollectionServiceAPI.getPaginatedList(command);
            return ResponseModel.builder()
                    .payload(datasetCollectionEntitiesPaginated)
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

    @GetMapping("/{id}")
    public ResponseModel getById(@PathVariable String id) {
        try {
            CommandGetDatasetCollection command = CommandGetDatasetCollection.builder()
                    .id(id)
                    .build();
            UserEntity userEntity = this.getUser();
            if (userEntity != null) {
                command.setUserId(userEntity.getId());
                command.setCheckPurchased(true);
            }
            command.setHasDatasetItems(true);

            return ResponseModel.builder()
                    .payload(datasetCollectionServiceAPI.getById(command))
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

    @GetMapping(value = "/preview/{id}", produces = MediaType.APPLICATION_PDF_VALUE)
    public @ResponseBody byte[] getPreview(@PathVariable String id) {
        try {
            DatasetCollectionEntity datasetCollectionEntity = datasetCollectionServiceAPI.getById(CommandGetDatasetCollection.builder()
                    .id(id)
                    .build());
            if (datasetCollectionEntity == null) {
                throw new Exception("dataset_collection_not_exist");
            }

            if (StringUtils.isBlank(datasetCollectionEntity.getPreview())) {
                throw new Exception("dataset_preview_path_null");
            }

            InputStream in = this.getClass().getResourceAsStream(datasetCollectionEntity.getPreview());
            if (in == null) {
                return null;
            }
            return in.readAllBytes();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @GetMapping("/purchased")
    @PreAuthorize("hasAnyAuthority('USER')")
    public ResponseModel getPurchasedPaginatedList(@RequestBody CommandGetListDatasetCollection command) {
        try {
            UserEntity userEntity = this.getUser();
            if (userEntity == null) {
                throw new Exception(ExceptionConstant.auth_invalid);
            }
            command.setUserId(userEntity.getId());
            command.setPurchased(true);

            Paginated<DatasetCollectionEntity> datasetCollectionEntitiesPaginated = datasetCollectionServiceAPI.getPaginatedList(command);
            return ResponseModel.builder()
                    .payload(datasetCollectionEntitiesPaginated)
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

    @PostMapping("/dataset_item/download_children/{dataset_item_id}")
    @PreAuthorize("hasAnyAuthority('USER')")
    public ResponseModel downloadChildren() {
        return null;
    }

}










