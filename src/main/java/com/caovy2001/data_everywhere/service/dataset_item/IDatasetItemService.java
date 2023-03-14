package com.caovy2001.data_everywhere.service.dataset_item;

import com.caovy2001.data_everywhere.entity.DatasetItemEntity;
import com.caovy2001.data_everywhere.service.IBaseService;

import java.util.List;

public interface IDatasetItemService extends IBaseService {
    List<DatasetItemEntity> findByDatasetCollectionId(String datasetCollectionId);
}
