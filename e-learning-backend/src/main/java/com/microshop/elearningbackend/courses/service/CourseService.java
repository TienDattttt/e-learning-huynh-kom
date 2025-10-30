package com.microshop.elearningbackend.courses.service;

import com.microshop.elearningbackend.auth.service.CurrentUserService;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import com.microshop.elearningbackend.categories.repository.CourseCategoryRepository;
import com.microshop.elearningbackend.common.exception.ApiException;
import com.microshop.elearningbackend.courses.dto.*;
import com.microshop.elearningbackend.courses.repository.ChapterRepository;
import com.microshop.elearningbackend.courses.repository.CourseLessonRepository;
import com.microshop.elearningbackend.courses.repository.CourseRepository;
import com.microshop.elearningbackend.learning.repository.LearningProgressRepository;
import com.microshop.elearningbackend.users.repository.UserRepository;
import com.microshop.elearningbackend.entity.*;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
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
    private final LearningProgressRepository learningProgressRepo;
    private final Cloudinary cloudinary;

    /* =========================
       PUBLIC API
       ========================= */

    @Transactional
    public Integer saveCourse(SaveCourseRequest req) {
        validateCourseInput(req);

        User teacher = current.getCurrentUser()
                .orElseThrow(() -> new ApiException("UNAUTHORIZED"));
        ensureTeacherRole(teacher);

        final Cours entity = (req.courseId() == null) ? new Cours() : requireCourse(req.courseId());

        if (entity.getId() != null) {
            // update -> check owner
            ensureOwner(entity, teacher.getId());
        } else {
            // create -> set owner
            entity.setUsers(teacher);
        }

        mapCourseFields(entity, req);
        ensureCourseRelationsManyToOne(entity, req.categoryId());

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

        Cours course = requireCourse(req.courseId());
        ensureOwner(course, teacher.getId());

        Chapter entity;
        if (req.chapterId() != null) {
            // UPDATE
            entity = requireChapter(req.chapterId());
            // đảm bảo chapter này thuộc đúng course
            if (!entity.getCourse().getId().equals(course.getId())) {
                throw new ApiException("Chapter does not belong to this course");
            }
        } else {
            // CREATE
            entity = new Chapter();
            entity.setCourse(course);
        }

        entity.setNameChapter(req.nameChapter());
        entity.setOrderChapter(req.orderChapter());
        return chapterRepo.save(entity).getId();
    }

    @Transactional
    public Integer saveLesson(SaveLessonRequest req) {
        validateLessonInput(req);

        User teacher = current.getCurrentUser()
                .orElseThrow(() -> new ApiException("UNAUTHORIZED"));
        ensureTeacherRole(teacher);

        Chapter chapter = requireChapter(req.chapterId());
        Cours course = chapter.getCourse();
        ensureOwner(course, teacher.getId());

        CourseLesson entity;
        if (req.courseLessonId() != null) {
            // UPDATE
            entity = requireLesson(req.courseLessonId());
            // đảm bảo lesson thuộc đúng chapter
            if (!entity.getChapter().getId().equals(chapter.getId())) {
                throw new ApiException("Lesson does not belong to this chapter");
            }
        } else {
            // CREATE
            entity = new CourseLesson();
            entity.setChapter(chapter);
        }

        entity.setName(req.name());
        entity.setSortOrder(req.sortOrder());
        entity.setVideoPath(req.videoPath());
        entity.setSlidePath(req.slidePath());
        entity.setTypeDocument(req.typeDocument());

        return lessonRepo.save(entity).getId();
    }

    public CourseDetailDto getPublicCourseDetail(Integer courseId) {
        if (courseId == null) throw new ApiException("courseId is required");

        Cours course = requirePublishedCourse(courseId);
        List<Chapter> chapters = loadChapters(courseId);
        Map<Integer, List<CourseLesson>> lessonsByChapter = loadLessonsGroupByChapter(chapters);

        return toCourseDetail(course, chapters, lessonsByChapter);
    }

    /* =========================
       FULL SAVE (UPSERT + CLEANUP ORPHANS)
       ========================= */

    @Transactional
    public Integer saveFullCourse(SaveFullCourseRequest req,
                                  MultipartFile imageFile,
                                  List<MultipartFile> videoFiles) {

        User teacher = current.getCurrentUser()
                .orElseThrow(() -> new ApiException("UNAUTHORIZED"));
        ensureTeacherRole(teacher);

        // 1) Upsert Course (giữ owner)
        Integer courseId = saveCourse(new SaveCourseRequest(
                req.courseId(),
                req.name(),
                req.description(),
                req.image(),          // có thể bị override sau khi upload imageFile
                req.content(),
                req.price(),
                req.promotionPrice(),
                req.categoryId()
        ));

        Cours course = requireCourse(courseId);
        ensureOwner(course, teacher.getId());

        // 2) Upload image (optional)
        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = uploadCourseImage(imageFile);
            course.setImage(imageUrl);
            courseRepo.save(course);
        }

        // 3) Build optional video map từ videoFiles (nếu backend nhận mảng files)
        //    Mặc định UI đã upload từng bài → đã có videoPath trong req.
        //    Đoạn này chỉ làm fallback nếu bạn vẫn gửi videoFiles cùng lúc.
        Map<Integer, String> fallbackVideoMap = new HashMap<>();
        if (videoFiles != null && !videoFiles.isEmpty()) {
            int idx = 0;
            for (MultipartFile vf : videoFiles) {
                String url = uploadLessonVideo(vf);
                fallbackVideoMap.put(++idx, url); // key = 1..N theo thứ tự
            }
        }

        // 4) Upsert Chapters & Lessons, đồng thời gom ID để cleanup orphan
        Set<Integer> keptChapterIds = new HashSet<>();

        if (req.chapters() != null && !req.chapters().isEmpty()) {
            for (int chIdx = 0; chIdx < req.chapters().size(); chIdx++) {
                ChapterRequest chReq = req.chapters().get(chIdx);

                Integer chapterId = saveChapter(new SaveChapterRequest(
                        chReq.chapterId(),     // giữ nguyên ID nếu có -> UPDATE
                        courseId,
                        chReq.nameChapter(),
                        chIdx + 1
                ));
                keptChapterIds.add(chapterId);

                // Lấy entity Chapter sau khi upsert
                Chapter persistedChapter = requireChapter(chapterId);

                // Upsert lessons
                Set<Integer> keptLessonIds = new HashSet<>();
                if (chReq.lessons() != null && !chReq.lessons().isEmpty()) {
                    for (int lsIdx = 0; lsIdx < chReq.lessons().size(); lsIdx++) {
                        LessonRequest lsReq = chReq.lessons().get(lsIdx);

                        // Ưu tiên videoPath từ request; nếu rỗng thì lấy từ fallbackVideoMap (nếu có)
                        String videoPath = lsReq.videoPath();
                        if ((videoPath == null || videoPath.isBlank()) && fallbackVideoMap.containsKey(lsIdx + 1)) {
                            videoPath = fallbackVideoMap.get(lsIdx + 1);
                        }

                        Integer lessonId = saveLesson(new SaveLessonRequest(
                                lsReq.courseLessonId(), // giữ ID -> UPDATE
                                chapterId,
                                lsReq.name(),
                                videoPath,
                                lsReq.slidePath(),
                                lsReq.typeDocument(),
                                lsIdx + 1
                        ));
                        keptLessonIds.add(lessonId);
                    }
                }

                // Cleanup orphan Lessons trong chapter này (những lesson không còn trong req)
                List<CourseLesson> currentLessons =
                        lessonRepo.findByChapter_IdInOrderBySortOrderAsc(List.of(persistedChapter.getId()));
                for (CourseLesson ls : currentLessons) {
                    if (!keptLessonIds.contains(ls.getId())) {
                        lessonRepo.delete(ls);
                    }
                }
            }
        }

        // 5) Cleanup orphan Chapters (những chapter đã tồn tại nhưng không còn trong req)
        List<Chapter> currentChapters = chapterRepo.findByCourse_IdOrderByOrderChapterAsc(courseId);
        for (Chapter ch : currentChapters) {
            if (!keptChapterIds.contains(ch.getId())) {
                // Do Lesson có @OnDelete(CASCADE) với Chapter, xóa Chapter sẽ xóa Lesson liên quan
                chapterRepo.delete(ch);
            }
        }

        // 6) Publish nếu cần
        if (Boolean.TRUE.equals(req.publish())) {
            publishCourse(courseId, true);
        }

        return courseId;
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
        String categoryName = (c.getCategories() != null) ? c.getCategories().getName() : null;
        Integer teacherId = (c.getUsers() != null) ? c.getUsers().getId() : null;

        long studentsCount = learningProgressRepo.countDistinctUsersByCourseId(c.getId());

        return new CourseSummaryDto(
                c.getId(),
                c.getName(),
                c.getDescription(),
                c.getImage(),
                c.getPrice(),
                c.getPromotionPrice(),
                categoryId,
                categoryName,
                teacherId,
                c.getStatus(),
                c.getDateCreated(),
                (int) studentsCount
        );
    }

    public Page<CourseSummaryDto> listTeacherCourses(Integer teacherId, Integer page, Integer size) {
        int p = page == null || page < 0 ? 0 : page;
        int s = size == null || size <= 0 || size > 100 ? 20 : size;
        Pageable pageable = PageRequest.of(p, s, Sort.by(Sort.Direction.DESC, "dateCreated"));

        Specification<Cours> spec = (root, q, cb) -> cb.equal(root.get("users").get("id"), teacherId);

        Page<Cours> pageData = courseRepo.findAll(spec, pageable);
        return pageData.map(this::toSummary);
    }

    @Transactional(readOnly = true)
    public CourseDetailDto getTeacherCourseDetail(Integer courseId) {
        if (courseId == null) throw new ApiException("courseId is required");

        User teacher = current.getCurrentUser().orElseThrow(() -> new ApiException("UNAUTHORIZED"));
        ensureTeacherRole(teacher);

        Cours course = requireCourse(courseId);
        ensureOwner(course, teacher.getId());

        List<Chapter> chapters = loadChapters(courseId);
        Map<Integer, List<CourseLesson>> lessonsByChapter = loadLessonsGroupByChapter(chapters);

        return toCourseDetail(course, chapters, lessonsByChapter);
    }

    @Transactional
    public void deleteCourse(Integer id) {
        User teacher = current.getCurrentUser().orElseThrow(() -> new ApiException("UNAUTHORIZED"));
        ensureTeacherRole(teacher);

        Cours course = requireCourse(id);
        ensureOwner(course, teacher.getId());
        courseRepo.delete(course);
    }

    @Transactional
    public void deleteChapter(Integer id) {
        User teacher = current.getCurrentUser().orElseThrow(() -> new ApiException("UNAUTHORIZED"));
        ensureTeacherRole(teacher);

        Chapter chapter = requireChapter(id);
        Cours course = chapter.getCourse();
        ensureOwner(course, teacher.getId());

        chapterRepo.delete(chapter);
    }

    @Transactional
    public void deleteLesson(Integer id) {
        User teacher = current.getCurrentUser().orElseThrow(() -> new ApiException("UNAUTHORIZED"));
        ensureTeacherRole(teacher);

        CourseLesson lesson = requireLesson(id);
        Chapter chapter = lesson.getChapter();
        Cours course = chapter.getCourse();
        ensureOwner(course, teacher.getId());

        lessonRepo.delete(lesson);
    }

    /* =========================
       Cloudinary helpers
       ========================= */

    public String uploadCourseImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ApiException("No image file provided");
        }
        try {
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new ApiException("File must be an image");
            }
            if (file.getSize() > 5L * 1024 * 1024) {
                throw new ApiException("Image size exceeds 5MB");
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", "courses",
                            "resource_type", "image"
                    )
            );
            return (String) uploadResult.get("secure_url");
        } catch (IOException e) {
            throw new ApiException("Image upload failed: " + e.getMessage());
        }
    }

    public String uploadLessonVideo(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ApiException("No video file provided");
        }
        try {
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("video/")) {
                throw new ApiException("File must be a video");
            }
            if (file.getSize() > 500L * 1024 * 1024) { // 500MB
                throw new ApiException("Video file too large (max 500MB)");
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", "courses/videos",
                            "resource_type", "video"
                    )
            );
            return (String) uploadResult.get("secure_url");
        } catch (IOException e) {
            throw new ApiException("Video upload failed: " + e.getMessage());
        }
    }
}
