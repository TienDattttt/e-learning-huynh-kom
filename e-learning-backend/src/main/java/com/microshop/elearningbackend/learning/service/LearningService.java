package com.microshop.elearningbackend.learning.service;

import com.microshop.elearningbackend.common.exception.ApiException;
import com.microshop.elearningbackend.courses.repository.ChapterRepository;
import com.microshop.elearningbackend.courses.repository.CourseLessonRepository;
import com.microshop.elearningbackend.courses.repository.CourseRepository;
import com.microshop.elearningbackend.entity.*;
import com.microshop.elearningbackend.learning.dto.*;
import com.microshop.elearningbackend.learning.repository.LearningProgressRepository;
import com.microshop.elearningbackend.orders.repository.OrderDetailRepository;
import com.microshop.elearningbackend.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LearningService {

    private final CourseRepository courseRepo;
    private final ChapterRepository chapterRepo;
    private final CourseLessonRepository lessonRepo;
    private final LearningProgressRepository progressRepo;
    private final OrderDetailRepository orderDetailRepo;
    private final UserRepository userRepo;

    /* =====================
       PUBLIC API (GET/POST)
       ===================== */

    @Transactional(readOnly = true)
    public CourseLearningDetailDto getMyCourseDetail(Integer userId, Integer courseId) {
        validateUserCourse(userId, courseId);
        ensurePurchased(userId, courseId);

        Cours course = requireCourse(courseId);
        List<Chapter> chapters = chapterRepo.findByCourse_IdOrderByOrderChapterAsc(courseId);
        Map<Integer, List<CourseLesson>> lessonsByChapter = groupLessons(chapters);
        Map<Integer, LearningProgress> myProgress = progressMap(userId, courseId);

        Double overall = calculateOverallProgress(myProgress);
        return toCourseLearningDetail(course, chapters, lessonsByChapter, myProgress, overall);
    }

    // New: dùng JWT userId truyền vào, thẩm định với req (nếu req.userId có giá trị khác thì báo lỗi)
    @Transactional
    public SaveProgressResponse saveProgressFor(Integer currentUserId, SaveProgressRequest req) {
        validateSaveProgressInputForCurrent(currentUserId, req);
        ensurePurchased(currentUserId, req.courseId());

        CourseLesson lesson = requireLesson(req.lessonId());
        if (!lesson.getChapter().getCourse().getId().equals(req.courseId())) {
            throw new ApiException("Lesson does not belong to the given course");
        }

        LearningProgress lp = upsertProgress(currentUserId, req, lesson);
        return new SaveProgressResponse(lp.getId(), lp.getProgressPercent(), lp.getIsCompleted());
    }

    @Transactional(readOnly = true)
    public Double getProgress(Integer userId, Integer courseId) {
        validateUserCourse(userId, courseId);
        ensurePurchased(userId, courseId);
        Double avg = progressRepo.avgProgressByCourse(userId, courseId);
        return (avg == null) ? 0d : avg;
    }

    /* =====================
       PRIVATE HELPERS (SRP)
       ===================== */

    private void validateSaveProgressInputForCurrent(Integer currentUserId, SaveProgressRequest req) {
        if (req.courseId() == null) throw new ApiException("courseId is required");
        if (req.lessonId() == null) throw new ApiException("lessonId is required");
        // Nếu client gửi req.userId và KHÁC với JWT -> chặn
        if (req.userId() != null && !req.userId().equals(currentUserId)) {
            throw new ApiException("userId in request does not match current token");
        }
    }

    private void validateUserCourse(Integer userId, Integer courseId) {
        if (userId == null) throw new ApiException("userId is required");
        if (courseId == null) throw new ApiException("courseId is required");
        userRepo.findById(userId).orElseThrow(() -> new ApiException("User not found: " + userId));
        courseRepo.findById(courseId).orElseThrow(() -> new ApiException("Course not found: " + courseId));
    }

    private void ensurePurchased(Integer userId, Integer courseId) {
        if (!orderDetailRepo.existsPurchasedCourse(userId, courseId)) {
            throw new ApiException("Access denied: course not purchased");
        }
    }

    private Cours requireCourse(Integer id) {
        return courseRepo.findById(id).orElseThrow(() -> new ApiException("Course not found: " + id));
    }

    private CourseLesson requireLesson(Integer id) {
        return lessonRepo.findById(id).orElseThrow(() -> new ApiException("Lesson not found: " + id));
    }

    private Map<Integer, List<CourseLesson>> groupLessons(List<Chapter> chapters) {
        if (chapters.isEmpty()) return Map.of();
        List<Integer> ids = chapters.stream().map(Chapter::getId).toList();
        List<CourseLesson> all = lessonRepo.findByChapter_IdInOrderBySortOrderAsc(ids);
        return all.stream().collect(Collectors.groupingBy(cl -> cl.getChapter().getId()));
    }

    private Map<Integer, LearningProgress> progressMap(Integer userId, Integer courseId) {
        List<LearningProgress> list = progressRepo.findByUser_IdAndCourse_Id(userId, courseId);
        return list.stream().collect(Collectors.toMap(lp -> lp.getLesson().getId(), lp -> lp));
    }

    private Double calculateOverallProgress(Map<Integer, LearningProgress> myProgress) {
        if (myProgress.isEmpty()) return 0d;
        double sum = myProgress.values().stream().mapToInt(LearningProgress::getProgressPercent).sum();
        return sum / myProgress.size();
    }

    /** Upsert pattern: có thì update, không có thì insert */
    private LearningProgress upsertProgress(Integer currentUserId, SaveProgressRequest req, CourseLesson lesson) {
        int p = normalizePercent(req.progressPercent());
        boolean done = (req.completed() != null && req.completed()) || p >= 100;

        LearningProgress lp = progressRepo
                .findByUser_IdAndLesson_Id(currentUserId, req.lessonId())
                .orElseGet(LearningProgress::new);

        if (lp.getId() == null) {
            // create
            User u = new User(); u.setId(currentUserId);
            Cours c = new Cours(); c.setId(req.courseId());

            lp.setUser(u);
            lp.setCourse(c);
            lp.setLesson(lesson);
        }

        lp.setProgressPercent(p);
        lp.setIsCompleted(done);
        lp.setUpdatedAt(LocalDateTime.now());
        return progressRepo.save(lp);
    }

    private int normalizePercent(Integer percent) {
        if (percent == null) return 0;
        if (percent < 0) return 0;
        if (percent > 100) return 100;
        return percent;
    }

    private CourseLearningDetailDto toCourseLearningDetail(
            Cours c,
            List<Chapter> chapters,
            Map<Integer, List<CourseLesson>> lessonsByChapter,
            Map<Integer, LearningProgress> myProgress,
            Double overall
    ) {
        Integer teacherId = (c.getUsers() != null) ? c.getUsers().getId() : null;

        List<ChapterProgressDto> chapterDtos = chapters.stream()
                .map(ch -> new ChapterProgressDto(
                        ch.getId(),
                        ch.getNameChapter(),
                        ch.getOrderChapter(),
                        mapLessonProgress(lessonsByChapter.getOrDefault(ch.getId(), List.of()), myProgress)
                ))
                .toList();

        return new CourseLearningDetailDto(
                c.getId(),
                c.getName(),
                c.getDescription(),
                c.getImage(),
                teacherId,
                c.getStatus(),
                c.getDateCreated(),
                overall,
                chapterDtos
        );
    }

    private List<LessonProgressDto> mapLessonProgress(List<CourseLesson> lessons,
                                                      Map<Integer, LearningProgress> myProgress) {
        List<LessonProgressDto> result = new ArrayList<>();
        for (CourseLesson ls : lessons) {
            LearningProgress lp = myProgress.get(ls.getId());
            int percent = (lp != null) ? lp.getProgressPercent() : 0;
            boolean completed = (lp != null) && Boolean.TRUE.equals(lp.getIsCompleted());

            result.add(new LessonProgressDto(
                    ls.getId(),
                    ls.getName(),
                    ls.getVideoPath(),
                    ls.getSlidePath(),
                    ls.getTypeDocument(),
                    ls.getSortOrder(),
                    percent,
                    completed
            ));
        }
        return result;
    }
}
