import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:online_course/core/utils/app_navigate.dart';
import 'package:online_course/src/features/course/domain/entities/course.dart';
import 'package:online_course/src/features/course/pesentation/bloc/explore/course_bloc.dart';
import 'package:online_course/src/features/course/pesentation/bloc/explore/course_event.dart';
import 'package:online_course/src/features/course/pesentation/bloc/explore/course_state.dart';
import 'package:online_course/src/features/course/pesentation/pages/course_detail/course_detail.dart';
import 'package:online_course/src/features/course/pesentation/pages/explore/widgets/course_item.dart';
import 'package:online_course/src/widgets/custom_progress_indicator.dart';

class ExploreCourseList extends StatefulWidget {
  const ExploreCourseList({super.key});

  @override
  State<ExploreCourseList> createState() => _ExploreCourseListState();
}

class _ExploreCourseListState extends State<ExploreCourseList> {
  @override
  void initState() {
    super.initState();
    Future.microtask(() =>
        context.read<CourseBloc>().add(const LoadCourseEvent()));
  }

  @override
  Widget build(BuildContext context) {
    return BlocBuilder<CourseBloc, CourseState>(
      builder: (context, state) {
        if (state is CourseLoading) {
          return const CustomProgressIndicator();
        } else if (state is CourseError) {
          return Center(child: Text(state.message));
        } else if (state is CourseLoaded) {
          final courses = state.courses;
          return _buildItemList(courses);
        }
        return const SizedBox.shrink();
      },
    );
  }

  Widget _buildItemList(List<Course> courses) {
    return ListView.builder(
      padding: const EdgeInsets.fromLTRB(15, 0, 15, 20),
      physics: const NeverScrollableScrollPhysics(),
      shrinkWrap: true,
      itemCount: courses.length,
      itemBuilder: (context, index) {
        return CourseItem(
          onTap: () {
            AppNavigator.to(
              context,
              CourseDetailPage(
                courseId: courses[index].id,
                course: courses[index],
                isHero: true,
              ),
            );
          },
          course: courses[index],
          width: MediaQuery.of(context).size.width,
        );
      },
    );
  }
}
