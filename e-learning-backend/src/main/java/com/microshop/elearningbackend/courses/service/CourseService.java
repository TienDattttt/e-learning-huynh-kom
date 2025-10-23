package com.microshop.elearningbackend.courses.service;
import com.microshop.elearningbackend.auth.service.CurrentUserService;

import com.microshop.elearningbackend.categories.repository.CourseCategoryRepository;
import com.microshop.elearningbackend.common.exception.ApiException;
import com.microshop.elearningbackend.courses.dto.*;
import com.microshop.elearningbackend.courses.repository.ChapterRepository;
import com.microshop.elearningbackend.courses.repository.CourseLessonRepository;
import com.microshop.elearningbackend.courses.repository.CourseRepository;
import com.microshop.elearningbackend.users.repository.UserRepository;
import com.microshop.elearningbackend.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepo;
    private final ChapterRepository chapterRepo;
    private final CourseLessonRepository lessonRepo;
    private final CourseCategoryRepository categoryRepo;
    private final UserRepository userRepo;
    private final CurrentUserService current;


    /* =========================
       PUBLIC API
       ========================= */

    @Transactional
    public Integer saveCourse(SaveCourseRequest req) {
        validateCourseInput(req);

        // Lấy user từ JWT
        User teacher = current.getCurrentUser()
                .orElseThrow(() -> new ApiException("UNAUTHORIZED"));

        // bắt buộc role GiangVien
        ensureTeacherRole(teacher);

        Cours entity = (req.courseId() == null) ? new Cours() : requireCourse(req.courseId());

        // Nếu update -> kiểm tra quyền sở hữu
        if (entity.getId() != null) {
            ensureOwner(entity, teacher.getId());
        } else {
            entity.setUsers(teacher); // set owner lần đầu tạo
        }

        mapCourseFields(entity, req);
        ensureCourseRelationsManyToOne(entity, req.categoryId()); // KHÔNG còn teacherId

        if (entity.getDateCreated() == null) {
            entity.setDateCreated(LocalDateTime.now());
        }
        return courseRepo.save(entity).getId();
    }


    @Transactional
    public Integer publishCourse(Integer courseId, Boolean publish) {
        if (courseId == null) throw new ApiException("courseId is required");

        User teacher = current.getCurrentUser()
                .orElseThrow(() -> new ApiException("UNAUTHORIZED"));
        ensureTeacherRole(teacher);

        Cours course = requireCourse(courseId);
        ensureOwner(course, teacher.getId());

        course.setStatus(Boolean.TRUE.equals(publish));
        return courseRepo.save(course).getId();
    }

    public Page<CourseSummaryDto> listPublicCourses(Integer categoryId,
                                                    Integer teacherId,
                                                    String keyword,
                                                    Integer page,
                                                    Integer size) {
        int p = page == null || page < 0 ? 0 : page;
        int s = size == null || size <= 0 || size > 100 ? 20 : size;
        Pageable pageable = PageRequest.of(p, s, Sort.by(Sort.Direction.DESC, "dateCreated"));

        Specification<Cours> spec = (root, q, cb) -> {
            List<Predicate> preds = new ArrayList<>();
            preds.add(cb.isTrue(root.get("status"))); // chỉ Published

            if (categoryId != null) {
                preds.add(cb.equal(root.get("categories").get("id"), categoryId));
            }
            if (teacherId != null) {
                preds.add(cb.equal(root.get("users").get("id"), teacherId));
            }
            if (keyword != null && !keyword.isBlank()) {
                String like = "%" + keyword.trim().toLowerCase() + "%";
                preds.add(cb.like(cb.lower(root.get("name")), like));
            }
            return cb.and(preds.toArray(new Predicate[0]));
        };

        Page<Cours> pageData = courseRepo.findAll(spec, pageable);
        return pageData.map(this::toSummary);
    }

    @Transactional
    public Integer saveChapter(SaveChapterRequest req) {
        validateChapterInput(req);
        User teacher = current.getCurrentUser()
                .orElseThrow(() -> new ApiException("UNAUTHORIZED"));
        ensureTeacherRole(teacher);

        Chapter entity = (req.chapterId() == null) ? new Chapter() : requireChapter(req.chapterId());
        Cours course = requireCourse(req.courseId());

        // course phải thuộc owner hiện tại
        ensureOwner(course, teacher.getId());

        entity.setNameChapter(req.nameChapter());
        entity.setOrderChapter(req.orderChapter());
        entity.setCourse(course);

        return chapterRepo.save(entity).getId();
    }


    @Transactional
    public Integer saveLesson(SaveLessonRequest req) {
        validateLessonInput(req);
        User teacher = current.getCurrentUser()
                .orElseThrow(() -> new ApiException("UNAUTHORIZED"));
        ensureTeacherRole(teacher);

        CourseLesson entity = (req.courseLessonId() == null) ? new CourseLesson() : requireLesson(req.courseLessonId());
        Chapter chapter = requireChapter(req.chapterId());
        Cours course = chapter.getCourse();

        // lesson thuộc chapter -> chapter thuộc course -> course phải thuộc teacher hiện tại
        ensureOwner(course, teacher.getId());

        entity.setName(req.name());
        entity.setSortOrder(req.sortOrder());
        entity.setChapter(chapter);
        entity.setVideoPath(req.videoPath());
        entity.setSlidePath(req.slidePath());
        entity.setTypeDocument(req.typeDocument());

        return lessonRepo.save(entity).getId();
    }


    public CourseDetailDto getPublicCourseDetail(Integer courseId) {
        if (courseId == null) throw new ApiException("courseId is required");

        Cours course = requirePublishedCourse(courseId);         // chỉ cho xem khi đã publish
        List<Chapter> chapters = loadChapters(courseId);         // lấy danh sách chapter theo thứ tự
        Map<Integer, List<CourseLesson>> lessonsByChapter = loadLessonsGroupByChapter(chapters);

        return toCourseDetail(course, chapters, lessonsByChapter);
    }

    /* =========================
       PRIVATE HELPERS
       ========================= */

    private Cours requirePublishedCourse(Integer id) {
        Cours c = requireCourse(id);
        if (!Boolean.TRUE.equals(c.getStatus())) {
            throw new ApiException("Course is not published");
        }
        return c;
    }

    private List<Chapter> loadChapters(Integer courseId) {
        return chapterRepo.findByCourse_IdOrderByOrderChapterAsc(courseId);
    }

    private Map<Integer, List<CourseLesson>> loadLessonsGroupByChapter(List<Chapter> chapters) {
        if (chapters.isEmpty()) return new HashMap<>();
        List<Integer> ids = chapters.stream().map(Chapter::getId).toList();
        List<CourseLesson> all = lessonRepo.findByChapter_IdInOrderBySortOrderAsc(ids);
        return all.stream().collect(Collectors.groupingBy(cl -> cl.getChapter().getId()));
    }

    private CourseDetailDto toCourseDetail(Cours c,
                                           List<Chapter> chapters,
                                           Map<Integer, List<CourseLesson>> lessonsByChapter) {
        Integer categoryId = (c.getCategories() != null) ? c.getCategories().getId() : null;
        Integer teacherId  = (c.getUsers() != null) ? c.getUsers().getId() : null;

        List<ChapterWithLessonsDto> chapterDtos = chapters.stream()
                .map(ch -> new ChapterWithLessonsDto(
                        ch.getId(),
                        ch.getNameChapter(),
                        ch.getOrderChapter(),
                        mapLessons(lessonsByChapter.getOrDefault(ch.getId(), List.of()))
                ))
                .toList();

        return new CourseDetailDto(
                c.getId(),
                c.getName(),
                c.getDescription(),
                c.getImage(),
                c.getContent(),
                c.getPrice(),
                c.getPromotionPrice(),
                categoryId,
                teacherId,
                c.getStatus(),
                c.getDateCreated(),
                chapterDtos
        );
    }

    private List<LessonDto> mapLessons(List<CourseLesson> list) {
        return list.stream()
                .map(ls -> new LessonDto(
                        ls.getId(),
                        ls.getName(),
                        ls.getVideoPath(),
                        ls.getSlidePath(),
                        ls.getTypeDocument(),
                        ls.getSortOrder()
                ))
                .toList();
    }

    private void validateCourseInput(SaveCourseRequest req) {
        if (req.name() == null || req.name().isBlank())
            throw new ApiException("Course name is required");
        // KHÔNG còn check teacherId
    }
    private void ensureTeacherRole(User u) {
        if (u.getRole() == null || !"GiangVien".equalsIgnoreCase(u.getRole().getRoleName())) {
            throw new ApiException("FORBIDDEN: require teacher role");
        }
    }

    private void ensureOwner(Cours c, Integer teacherId) {
        if (c.getUsers() == null || !teacherId.equals(c.getUsers().getId())) {
            throw new ApiException("You are not owner of this course");
        }
    }


    private void mapCourseFields(Cours entity, SaveCourseRequest req) {
        entity.setName(req.name().trim());
        entity.setDescription(req.description());
        entity.setImage(req.image());
        entity.setContent(req.content());
        entity.setPrice(req.price());
        entity.setPromotionPrice(req.promotionPrice());
        if (entity.getStatus() == null) entity.setStatus(Boolean.FALSE); // Draft mặc định
    }

    /** Thiết lập ManyToOne: chỉ còn categories; owner lấy từ JWT ở saveCourse */
    private void ensureCourseRelationsManyToOne(Cours entity, Integer categoryId) {
        if (categoryId != null) {
            CourseCategory cat = new CourseCategory();
            cat.setId(categoryId);
            entity.setCategories(cat);
        } else {
            entity.setCategories(null);
        }
        // KHÔNG set entity.setUsers(...) ở đây (đã set ở saveCourse khi tạo mới).
    }


    private Cours requireCourse(Integer id) {
        return courseRepo.findById(id).orElseThrow(() -> new ApiException("Course not found: id=" + id));
    }

    private void validateChapterInput(SaveChapterRequest req) {
        if (req.courseId() == null) throw new ApiException("courseId is required");
        if (req.nameChapter() == null || req.nameChapter().isBlank())
            throw new ApiException("nameChapter is required");
    }

    private Chapter requireChapter(Integer id) {
        return chapterRepo.findById(id).orElseThrow(() -> new ApiException("Chapter not found: id=" + id));
    }

    private void validateLessonInput(SaveLessonRequest req) {
        if (req.chapterId() == null) throw new ApiException("chapterId is required");
        if (req.name() == null || req.name().isBlank()) throw new ApiException("lesson name is required");
    }

    private CourseLesson requireLesson(Integer id) {
        return lessonRepo.findById(id).orElseThrow(() -> new ApiException("Lesson not found: id=" + id));
    }

    private CourseSummaryDto toSummary(Cours c) {
        Integer categoryId = (c.getCategories() != null) ? c.getCategories().getId() : null;
        Integer teacherId = (c.getUsers() != null) ? c.getUsers().getId() : null;

        return new CourseSummaryDto(
                c.getId(),
                c.getName(),
                c.getDescription(),
                c.getImage(),
                c.getPrice(),
                c.getPromotionPrice(),
                categoryId,
                teacherId,
                c.getStatus(),
                c.getDateCreated()
        );
    }
}
