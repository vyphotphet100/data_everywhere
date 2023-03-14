package com.caovy2001.data_everywhere.model.pagination;

public interface EntityMapper<D, E> {
    D map(E entity);
    void map(E gift, D dto);
}
