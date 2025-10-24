import 'package:flutter/material.dart';
import 'package:online_course/src/features/course/domain/entities/course_detail.dart';

class CourseDetailImage extends StatelessWidget {
  const CourseDetailImage({
    super.key,
    required this.detail,
    this.isHero = false,
  });

  final CourseDetail detail;
  final bool isHero;

  @override
  Widget build(BuildContext context) {
    return isHero
        ? Hero(
      tag: '${detail.id}${detail.image}',
      child: Image.asset(
        "${detail.image}",
        width: double.infinity,
        height: 220,
      ),
    )
        : Image.asset(
      "${detail.image}",
      width: double.infinity,
      height: 220,
    );
  }
}
