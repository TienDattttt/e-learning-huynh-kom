import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:online_course/src/features/course/domain/entities/course_detail.dart';
import 'package:online_course/src/features/learning/presentation/pages/learning_player_page.dart';
import 'package:online_course/src/features/learning/presentation/bloc/learning_bloc.dart';
import 'package:online_course/src/theme/app_color.dart';

class LessonItem extends StatelessWidget {
  const LessonItem({
    super.key,
    required this.lesson,
    required this.purchased,
    required this.courseId,
  });

  final CourseLesson lesson;
  final bool purchased;
  final int courseId;

  IconData _iconByType(String type) {
    final t = type.toLowerCase();
    if (t.contains('video')) return Icons.play_circle_fill_rounded;
    if (t.contains('doc') || t.contains('pdf')) return Icons.description_rounded;
    return Icons.menu_book_rounded;
  }

  @override
  Widget build(BuildContext context) {
    final locked = !purchased;
    final icon = _iconByType(lesson.type);

    return InkWell(
      onTap: locked
          ? null
          : () {
        if (lesson.videoPath == null || lesson.videoPath!.isEmpty) {
          ScaffoldMessenger.of(context).showSnackBar(
            const SnackBar(content: Text('Bài học này chưa có video.')),
          );
          return;
        }

        Navigator.push(
          context,
          MaterialPageRoute(
            builder: (_) => BlocProvider.value(
              value: context.read<LearningBloc>(),
              child: LearningPlayerPage(
                courseId: courseId,
                lessonId: lesson.id,
                videoUrl: lesson.videoPath!,
              ),
            ),
          ),
        );
      },
      child: Container(
        margin: const EdgeInsets.all(5),
        padding: const EdgeInsets.all(12),
        decoration: BoxDecoration(
          borderRadius: BorderRadius.circular(16),
          color: Colors.white,
          boxShadow: [
            BoxShadow(
              color: AppColor.shadowColor.withOpacity(0.05),
              spreadRadius: 1,
              blurRadius: 1,
              offset: const Offset(1, 1),
            ),
          ],
        ),
        child: Row(
          children: [
            Container(
              height: 54,
              width: 54,
              decoration: BoxDecoration(
                color: AppColor.primary.withOpacity(.1),
                shape: BoxShape.circle,
              ),
              child: Icon(icon, color: AppColor.primary, size: 28),
            ),
            const SizedBox(width: 12),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    lesson.name,
                    maxLines: 1,
                    overflow: TextOverflow.ellipsis,
                    style: const TextStyle(
                      color: AppColor.textColor,
                      fontSize: 14,
                      fontWeight: FontWeight.w600,
                    ),
                  ),
                  const SizedBox(height: 8),
                  Row(
                    children: [
                      Icon(
                        lesson.type.toLowerCase().contains('video')
                            ? Icons.play_arrow_rounded
                            : Icons.description_outlined,
                        color: AppColor.labelColor,
                        size: 16,
                      ),
                      const SizedBox(width: 4),
                      Text(
                        lesson.type,
                        style: const TextStyle(
                          fontSize: 13,
                          color: AppColor.labelColor,
                        ),
                      ),
                      const Spacer(),
                      if (lesson.progressPercent != null)
                        Text(
                          "${(lesson.progressPercent! * 100).toStringAsFixed(0)}%",
                          style: const TextStyle(
                            fontSize: 12,
                            color: AppColor.labelColor,
                          ),
                        ),
                    ],
                  ),
                ],
              ),
            ),
            const SizedBox(width: 8),
            Icon(
              locked ? Icons.lock_rounded : Icons.play_arrow_rounded,
              color: locked ? Colors.grey : AppColor.primary,
              size: 20,
            ),
          ],
        ),
      ),
    );
  }
}
