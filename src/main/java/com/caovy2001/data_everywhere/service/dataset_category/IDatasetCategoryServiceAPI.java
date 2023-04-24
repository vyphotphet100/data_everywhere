package com.caovy2001.data_everywhere.service.dataset_category;

import com.caovy2001.data_everywhere.command.dataset_category.CommandGetListDatasetCategory;
import com.caovy2001.data_everywhere.entity.DatasetCategoryEntity;
import com.caovy2001.data_everywhere.model.pagination.Paginated;
import com.caovy2001.data_everywhere.service.IBaseService;

public interface IDatasetCategoryServiceAPI extends IBaseService {
    Paginated<DatasetCategoryEntity> getPaginatedList(CommandGetListDatasetCategory command) throws Exception;
}
