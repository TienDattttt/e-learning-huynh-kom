package com.microshop.elearningbackend.categories.service;

import com.microshop.elearningbackend.categories.dto.CategoryNode;
import com.microshop.elearningbackend.categories.repository.CourseCategoryRepository;
import com.microshop.elearningbackend.common.exception.ApiException;
import com.microshop.elearningbackend.entity.CourseCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * CategoryService - SRP + Decomposition trong 1 class:
 * - public API: saveUpsert(...), getTree()
 * - private helpers: validateInput(...), ensureNoCycle(...), requireExisting(...), buildTree(...), sortRecursively(...)
 */
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CourseCategoryRepository repo;

    /* =========================
       Public API (Controller gọi)
       ========================= */

    @Transactional
    public Integer saveUpsert(Integer id, String name, Integer parentId, Integer sortOrder, Boolean status) {
        validateInput(name);
        ensureNoCycle(id, parentId);

        CourseCategory entity = (id == null)
                ? new CourseCategory()
                : repo.findById(id).orElseThrow(() -> new ApiException("Category not found: id=" + id));

        entity.setName(name.trim());
        entity.setSortOrder(sortOrder);
        entity.setStatus(status == null ? Boolean.TRUE : status);

        if (parentId != null) {
            entity.setParent(requireExisting(parentId));
        } else {
            entity.setParent(null);
        }

        return repo.save(entity).getId();
    }

    public List<CategoryNode> getTree() {
        List<CourseCategory> all = repo.findAllByStatusTrueOrderBySortOrderAsc();
        return buildTree(all);
    }

    /* =========================
       Private Decomposed Methods
       ========================= */

    private void validateInput(String name) {
        if (name == null || name.isBlank()) throw new ApiException("Name is required");
    }

    private CourseCategory requireExisting(Integer id) {
        return repo.findById(id).orElseThrow(() -> new ApiException("Category not found: id=" + id));
    }

    /** Chống cycle: id -> parentId -> ancestor... nếu chạm lại id => lỗi */
    private void ensureNoCycle(Integer id, Integer parentId) {
        if (parentId == null || id == null) return;
        if (id.equals(parentId)) throw new ApiException("Parent cannot be self");

        CourseCategory cursor = requireExisting(parentId);
        while (cursor != null) {
            if (Objects.equals(cursor.getId(), id)) {
                throw new ApiException("Cycle detected with parentId=" + parentId);
            }
            cursor = cursor.getParent();
        }
    }

    private List<CategoryNode> buildTree(List<CourseCategory> all) {
        Map<Integer, CategoryNode> map = new HashMap<>();
        List<CategoryNode> roots = new ArrayList<>();

        for (CourseCategory c : all) {
            CategoryNode node = new CategoryNode(
                    c.getId(),
                    c.getName(),
                    c.getSortOrder(),
                    c.getStatus(),
                    c.getParent() == null ? null : c.getParent().getId()
            );
            map.put(node.id, node);
        }
        for (CourseCategory c : all) {
            Integer id = c.getId();
            Integer parentId = c.getParent() == null ? null : c.getParent().getId();
            CategoryNode node = map.get(id);
            if (parentId == null) {
                roots.add(node);
            } else {
                CategoryNode parent = map.get(parentId);
                if (parent != null) parent.children.add(node);
                else roots.add(node); // fallback nếu parent bị ẩn
            }
        }
        sortRecursively(roots);
        return roots;
    }

    private void sortRecursively(List<CategoryNode> nodes) {
        nodes.sort(Comparator
                .comparing((CategoryNode n) -> n.sortOrder == null ? Integer.MAX_VALUE : n.sortOrder)
                .thenComparing(n -> n.name, String.CASE_INSENSITIVE_ORDER));
        for (CategoryNode n : nodes) {
            if (!n.children.isEmpty()) sortRecursively(n.children);
        }
    }
}
