package com.caovy2001.data_everywhere.model.pagination;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class Paginated<T> implements Serializable {

    private List<T> items;

    @JsonProperty("page_number")
    private long pageNumber;

    @JsonProperty("page_size")
    private long pageSize;

    @JsonProperty("total_items")
    private long totalItems;

    @JsonProperty("total_pages")
    private long totalPages;

    @JsonProperty("has_next")
    private boolean hasNext = false;

    @JsonProperty("next_page")
    private long nextPage = 1;

    @JsonProperty("has_previous")
    private boolean hasPrevious = false;

    @JsonProperty("previous_page")
    private long previousPage = 1;

    @JsonProperty("extension")
    private Object extension;

    public Paginated(List<T> items, long pageNumber, long pageSize, long total) {
        this(items, pageNumber, pageSize, total, null);
    }

    public Paginated(List<T> items, long pageNumber, long pageSize, long total, Object extension) {
        this.items = items;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalItems = total;
        if(pageSize == 0L) {
            this.totalPages = 1;
        } else {
            this.totalPages = (int) Math.ceil((double) total / this.pageSize);
        }

        this.hasNext = this.pageNumber < this.totalPages;
        this.hasPrevious = this.pageNumber > 1;
        if(pageNumber == 0L && pageSize == 0L) {
            this.hasNext = false;
        }
        if(total == pageSize) {
            this.hasNext = false;
        }

        if (this.hasNext) {
            this.nextPage = this.pageNumber + 1;
        }

        if (this.hasPrevious) {
            this.previousPage = this.pageNumber - 1;
        }
        this.extension = extension;
    }

    public static <D, E> Paginated<D> from(Page<E> page, EntityMapper<D, E> entityMapper) {
        List<D> items = new ArrayList<>();

        for (E entity : page.items()) {
            items.add(entityMapper.map(entity));
        }

        return new Paginated<>(items, page.pageNumber(), page.pageSize(), page.totalItems());
    }
}
