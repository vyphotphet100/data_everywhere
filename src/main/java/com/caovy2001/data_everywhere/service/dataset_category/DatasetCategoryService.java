package com.caovy2001.data_everywhere.service.dataset_category;

import com.caovy2001.data_everywhere.command.dataset_category.CommandGetListDatasetCategory;
import com.caovy2001.data_everywhere.entity.DatasetCategoryEntity;
import com.caovy2001.data_everywhere.model.pagination.Paginated;
import com.caovy2001.data_everywhere.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DatasetCategoryService extends BaseService implements IDatasetCategoryServiceAPI {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Paginated<DatasetCategoryEntity> getPaginatedList(CommandGetListDatasetCategory command) throws Exception {
        if (command.getPage() <= 0 || command.getSize() < 0) {
            throw new Exception("invalid_page_or_size");
        }

        if (command.getSize() == 0) {
            command.setSize(10);
        }

        Query query = new Query();
        long total = mongoTemplate.count(query, DatasetCategoryEntity.class);
        if (total == 0) {
            return new Paginated<>(new ArrayList<>(), command.getPage(), command.getSize(), 0);
        }

        PageRequest pageRequest = PageRequest.of(command.getPage() - 1, command.getSize());
        query.with(pageRequest);
        List<DatasetCategoryEntity> datasetCategories = mongoTemplate.find(query, DatasetCategoryEntity.class);
        return new Paginated<>(datasetCategories, command.getPage(), command.getSize(), total);
    }
}
