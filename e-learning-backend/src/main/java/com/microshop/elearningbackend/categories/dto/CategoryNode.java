package com.microshop.elearningbackend.categories.dto;

import java.util.ArrayList;
import java.util.List;

public class CategoryNode {
    public Integer id;
    public String name;
    public Integer sortOrder;
    public Boolean status;
    public Integer parentId;
    public List<CategoryNode> children = new ArrayList<>();

    public CategoryNode(Integer id, String name, Integer sortOrder, Boolean status, Integer parentId) {
        this.id = id;
        this.name = name;
        this.sortOrder = sortOrder;
        this.status = status;
        this.parentId = parentId;
    }
}
