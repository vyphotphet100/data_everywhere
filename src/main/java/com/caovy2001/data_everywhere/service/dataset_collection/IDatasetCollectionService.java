package com.caovy2001.data_everywhere.service.dataset_collection;

import com.caovy2001.data_everywhere.command.dataset_collection.CommandGetListDatasetCollection;
import com.caovy2001.data_everywhere.entity.DatasetCollectionEntity;
import com.caovy2001.data_everywhere.service.IBaseService;

import java.util.List;

public interface IDatasetCollectionService extends IBaseService {
    long countById(String id);

    List<DatasetCollectionEntity> getList(CommandGetListDatasetCollection command);

    DatasetCollectionEntity getById(String datasetCollectionId);
}
