import 'package:flutter/material.dart';
import 'package:online_course/core/utils/app_navigate.dart';
import 'package:online_course/src/features/course/domain/entities/my_course.dart';
import 'package:online_course/src/features/course/pesentation/pages/course_detail/course_detail.dart';
import 'my_course_item.dart';

class MyCourseCompleteCourseList extends StatelessWidget {
  const MyCourseCompleteCourseList({required this.myCompleteCourses, super.key});
  final List<MyCourse> myCompleteCourses;

  @override
  Widget build(BuildContext context) {
    return SingleChildScrollView(
      child: Column(
        children: List.generate(
          myCompleteCourses.length,
              (index) {
            final c = myCompleteCourses[index];
            return Padding(
              padding: const EdgeInsets.fromLTRB(15, 5, 15, 5),
              child: MyCourseItem(
                data: c,
                onTap: () {
                  AppNavigator.to(
                    context,
                    CourseDetailPage(courseId: c.courseId),
                  );
                },
              ),
            );
          },
        ),
      ),
    );
  }
}
