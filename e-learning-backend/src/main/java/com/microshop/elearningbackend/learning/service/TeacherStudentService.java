package com.microshop.elearningbackend.learning.service;

import com.microshop.elearningbackend.common.ApiPage;
import com.microshop.elearningbackend.common.ApiResponse;
import com.microshop.elearningbackend.common.exception.ApiException;
import com.microshop.elearningbackend.entity.Cours;
import com.microshop.elearningbackend.entity.User;
import com.microshop.elearningbackend.learning.dto.*;
import com.microshop.elearningbackend.learning.repository.AccountLockRequestRepository;
import com.microshop.elearningbackend.learning.repository.LearningProgressRepository;
import com.microshop.elearningbackend.learning.repository.TeacherStudentRepository;
import com.microshop.elearningbackend.users.repository.UserRepository;
import com.microshop.elearningbackend.courses.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeacherStudentService {

    private final TeacherStudentRepository teacherStudentRepo;
    private final LearningProgressRepository lpRepo;
    private final UserRepository userRepo;
    private final CourseRepository courseRepo;
    private final AccountLockRequestRepository lockRepo;

    /* ==========================
       QUẢN LÝ YÊU CẦU KHÓA TK
       ========================== */

    @Transactional
    public ApiResponse<?> createLockRequest(Integer teacherId, Integer studentId, String reason) {
        validateLockRequestInput(teacherId, studentId);
        ensureTeacherRole(requireUser(teacherId));
        ensureStudentRole(requireUser(studentId));

        var result = lockRepo.createLockRequest(teacherId, studentId, reason);
        return ApiResponse.ok(result);
    }

    @Transactional(readOnly = true)
    public ApiResponse<?> listPendingLockRequests() {
        var list = lockRepo.findPendingRequests();
        return ApiResponse.ok(list);
    }

    @Transactional
    public ApiResponse<?> updateLockStatus(long requestId, String status) {
        if (!"APPROVED".equalsIgnoreCase(status) && !"REJECTED".equalsIgnoreCase(status)) {
            throw new ApiException("Invalid status: " + status);
        }
        var result = lockRepo.updateStatus(requestId, status);
        return ApiResponse.ok(result);
    }

    /* =====================
       DANH SÁCH HỌC VIÊN
       ===================== */

    @Transactional(readOnly = true)
    public ApiPage<StudentListItemDto> listStudents(Integer teacherId, Integer courseId, int page, int size) {
        ensureTeacherRole(requireUser(teacherId));
        if (courseId != null) validateCourseBelongsToTeacher(courseId, teacherId);

        var p = teacherStudentRepo.findStudentsOfTeacher(teacherId, courseId, PageRequest.of(page, size));
        List<User> students = p.getContent();

        Map<Integer, AggregatedProgress> agg = (courseId != null && !students.isEmpty())
                ? aggregateProgressFor(courseId, students.stream().map(User::getId).toList())
                : Collections.emptyMap();

        String courseName = (courseId != null) ? requireCourse(courseId).getName() : null;

        List<StudentListItemDto> items = new ArrayList<>();
        for (User u : students) {
            AggregatedProgress ap = agg.get(u.getId());
            Integer percent = (ap != null) ? ap.overallPercent() : 0;
            LocalDateTime last = (ap != null) ? ap.lastUpdated() : null;
            items.add(new StudentListItemDto(
                    u.getId(), u.getFullName(), u.getEmail(),
                    courseId, courseName, percent, last
            ));
        }
        return new ApiPage<>(items, p.getNumber(), p.getSize(), p.getTotalElements());
    }

    @Transactional(readOnly = true)
    public StudentProgressDetailDto studentProgressDetail(Integer teacherId, Integer courseId, Integer studentId) {
        ensureTeacherRole(requireUser(teacherId));
        if (courseId == null) throw new ApiException("courseId is required");
        validateCourseBelongsToTeacher(courseId, teacherId);
        if (!teacherStudentRepo.studentBelongsToTeacher(teacherId, studentId, courseId)) {
            throw new ApiException("Student not found in your course");
        }

        var course = requireCourse(courseId);
        var raw = lpRepo.findLessonProgress(courseId, studentId);

        List<StudentProgressDetailDto.LessonProgressItem> lessons = new ArrayList<>();
        int sum = 0, cnt = 0;
        LocalDateTime last = null;

        for (Object[] row : raw) {
            Integer lessonId = (Integer) row[0];
            String  lessonName = (String)  row[1];
            Integer percent = (Integer) row[2];
            Boolean completed = (Boolean) row[3];
            LocalDateTime updatedAt = (LocalDateTime) row[4];

            lessons.add(new StudentProgressDetailDto.LessonProgressItem(
                    lessonId, lessonName, percent, completed, updatedAt
            ));
            if (percent != null) { sum += percent; cnt++; }
            if (last == null || (updatedAt != null && updatedAt.isAfter(last))) last = updatedAt;
        }

        int overall = (cnt == 0) ? 0 : Math.min(100, Math.round(sum / (float)cnt));
        return new StudentProgressDetailDto(studentId, courseId, course.getName(), overall, last, lessons);
    }

    /* =========================
       PRIVATE HELPERS
       ========================= */

    private void validateLockRequestInput(Integer teacherId, Integer studentId) {
        if (teacherId == null) throw new ApiException("teacherId is required");
        if (studentId == null) throw new ApiException("studentId is required");
        if (teacherId.equals(studentId)) throw new ApiException("teacherId and studentId must be different");
    }

    private User requireUser(Integer userId) {
        return userRepo.findById(userId)
                .orElseThrow(() -> new ApiException("User not found: " + userId));
    }

    private void ensureTeacherRole(User u) {
        if (u.getRole() == null || !"GiangVien".equalsIgnoreCase(u.getRole().getRoleName())) {
            throw new ApiException("User is not a teacher: " + u.getId());
        }
    }

    private void ensureStudentRole(User u) {
        if (u.getRole() == null || !"HocVien".equalsIgnoreCase(u.getRole().getRoleName())) {
            throw new ApiException("User is not a student: " + u.getId());
        }
    }

    private void validateCourseBelongsToTeacher(Integer courseId, Integer teacherId) {
        var c = courseRepo.findById(courseId).orElseThrow(() -> new ApiException("Course not found: " + courseId));
        if (c.getUsers() == null || !Objects.equals(c.getUsers().getId(), teacherId)) {
            throw new ApiException("Course does not belong to teacher");
        }
    }

    private Cours requireCourse(Integer id) {
        return courseRepo.findById(id).orElseThrow(() -> new ApiException("Course not found: " + id));
    }

    private Map<Integer, AggregatedProgress> aggregateProgressFor(Integer courseId, List<Integer> studentIds) {
        Map<Integer, AggregatedProgress> map = new HashMap<>();
        var rows = lpRepo.aggregateCourseProgress(courseId, studentIds);
        for (Object[] r : rows) {
            Integer userId = (Integer) r[0];
            Double  avg    = (Double)  r[1];
            LocalDateTime last   = (LocalDateTime) r[2];
            int overall = (avg == null) ? 0 : Math.min(100, Math.round(avg.floatValue()));
            map.put(userId, new AggregatedProgress(overall, last));
        }
        return map;
    }

    private record AggregatedProgress(int overallPercent, LocalDateTime lastUpdated) {}
}
