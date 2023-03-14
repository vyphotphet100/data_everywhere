package com.caovy2001.data_everywhere.service.dataset_collection;

import com.caovy2001.data_everywhere.command.dataset_collection.CommandGetDatasetCollection;
import com.caovy2001.data_everywhere.command.dataset_collection.CommandGetListDatasetCollection;
import com.caovy2001.data_everywhere.entity.DatasetCollectionEntity;
import com.caovy2001.data_everywhere.model.FileResponse;
import com.caovy2001.data_everywhere.model.pagination.Paginated;
import com.caovy2001.data_everywhere.service.IBaseService;

public interface IDatasetCollectionServiceAPI extends IBaseService {
    Paginated<DatasetCollectionEntity> getPaginatedList(CommandGetListDatasetCollection command) throws Exception;

    DatasetCollectionEntity getById(CommandGetDatasetCollection command) throws Exception;

    FileResponse getPreviewById(String id) throws Exception;
}
