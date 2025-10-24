import 'package:flutter/material.dart';
import 'package:online_course/src/features/course/domain/entities/course_detail.dart';
import 'package:online_course/src/theme/app_color.dart';

class LessonItem extends StatelessWidget {
  const LessonItem({
    super.key,
    required this.lesson,
    required this.purchased,
  });

  final CourseLesson lesson;
  final bool purchased;

  IconData _iconByType(String type) {
    final t = type.toLowerCase();
    if (t.contains('video')) return Icons.play_circle_fill_rounded;
    if (t.contains('doc') || t.contains('pdf')) return Icons.description_rounded;
    return Icons.menu_book_rounded;
    // fallback
  }

  @override
  Widget build(BuildContext context) {
    final locked = !purchased; // Option A: show all nhưng khóa nếu chưa mua
    final icon = _iconByType(lesson.type);

    return Container(
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
          // Icon theo type thay vì ảnh
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
          // Info
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
                    // progress (nếu có)
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
          // Arrow or Lock
          Icon(
            locked ? Icons.lock_rounded : Icons.arrow_forward_ios_rounded,
            color: AppColor.labelColor,
            size: 18,
          ),
        ],
      ),
    );
  }
}
