package com.caovy2001.data_everywhere.service.dataset_item;

import com.caovy2001.data_everywhere.entity.DatasetItemEntity;
import com.caovy2001.data_everywhere.repository.DatasetItemRepository;
import com.caovy2001.data_everywhere.service.BaseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DatasetItemService extends BaseService implements IDatasetItemService, IDatasetItemServiceAPI {
    @Autowired
    private DatasetItemRepository datasetItemRepository;

    @Override
    public List<DatasetItemEntity> findByDatasetCollectionId(String datasetCollectionId) {
        if (StringUtils.isBlank(datasetCollectionId)) {
            return null;
        }

        return datasetItemRepository.findAllByDatasetCollectionId(datasetCollectionId);
    }
}
