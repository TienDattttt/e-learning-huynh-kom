import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:online_course/src/features/course/pesentation/bloc/explore/course_bloc.dart';
import 'package:online_course/src/features/course/pesentation/bloc/explore/course_event.dart';
import 'package:online_course/src/theme/app_color.dart';

class ExploreAppbar extends StatelessWidget {
  const ExploreAppbar({super.key});

  @override
  Widget build(BuildContext context) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        const Expanded(
          child: Text(
            "Khám phá",
            style: TextStyle(
              color: AppColor.textColor,
              fontSize: 24,
              fontWeight: FontWeight.w600,
            ),
          ),
        ),
        // 🟢 Nút refresh
        IconButton(
          icon: const Icon(Icons.refresh, color: AppColor.primary, size: 26),
          tooltip: 'Làm mới danh sách',
          onPressed: () {
            context.read<CourseBloc>().add(const LoadCourseEvent());
          },
        ),
      ],
    );
  }
}
