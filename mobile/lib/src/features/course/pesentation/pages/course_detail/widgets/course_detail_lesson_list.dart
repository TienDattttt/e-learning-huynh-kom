import 'package:flutter/material.dart';
import 'package:online_course/src/features/course/domain/entities/course_detail.dart';
import 'package:online_course/src/features/course/pesentation/pages/course_detail/widgets/lesson_item.dart';
import 'package:online_course/src/theme/app_color.dart';

class CourseDetailLessonList extends StatelessWidget {
  const CourseDetailLessonList({
    super.key,
    required this.chapters,
    required this.purchased,
    required this.courseId, // ✅ thêm dòng này
  });

  final List<CourseChapter> chapters;
  final bool purchased;
  final int courseId; // ✅ thêm dòng này

  @override
  Widget build(BuildContext context) {
    if (chapters.isEmpty) {
      return const Center(
        child: Text(
          "No lessons yet.",
          style: TextStyle(color: AppColor.labelColor),
        ),
      );
    }

    return ListView.builder(
      padding: EdgeInsets.zero,
      itemCount: chapters.length,
      itemBuilder: (context, index) {
        final ch = chapters[index];
        return Padding(
          padding: const EdgeInsets.only(top: 8.0, bottom: 8.0),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              // Header Chapter
              Padding(
                padding: const EdgeInsets.symmetric(horizontal: 8.0, vertical: 4),
                child: Text(
                  "${ch.order}. ${ch.name}",
                  style: const TextStyle(
                    fontSize: 16,
                    fontWeight: FontWeight.w600,
                    color: AppColor.textColor,
                  ),
                ),
              ),

              // Lessons
              if (ch.lessons.isEmpty)
                const Padding(
                  padding: EdgeInsets.symmetric(horizontal: 12.0, vertical: 6),
                  child: Text(
                    "No lessons in this chapter.",
                    style: TextStyle(color: AppColor.labelColor),
                  ),
                )
              else
                ...ch.lessons.map((lesson) => Padding(
                  padding: const EdgeInsets.symmetric(horizontal: 4.0, vertical: 4),
                  child: LessonItem(
                    lesson: lesson,
                    purchased: purchased,
                    courseId: courseId, // ✅ truyền đúng ID khóa học
                  ),
                )),
            ],
          ),
        );
      },
    );
  }
}
