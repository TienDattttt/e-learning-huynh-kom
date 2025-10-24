import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:online_course/core/services/token_storage.dart';
import 'package:online_course/core/utils/jwt_util.dart';
import 'package:online_course/src/features/course/pesentation/bloc/my_course/my_course_bloc.dart';
import 'package:online_course/src/features/course/pesentation/bloc/my_course/my_course_event.dart';
import 'package:online_course/src/features/course/pesentation/bloc/my_course/my_course_state.dart';

import 'package:online_course/src/features/course/pesentation/pages/my_course/widgets/my_course_appbar.dart';
import 'package:online_course/src/features/course/pesentation/pages/my_course/widgets/my_course_complete_course_list.dart';
import 'package:online_course/src/features/course/pesentation/pages/my_course/widgets/my_course_progress_course_list.dart';
import 'package:online_course/src/theme/app_color.dart';

class MyCoursePage extends StatefulWidget {
  const MyCoursePage({Key? key}) : super(key: key);

  @override
  State<MyCoursePage> createState() => _MyCoursePageState();
}

class _MyCoursePageState extends State<MyCoursePage>
    with SingleTickerProviderStateMixin {
  late TabController tabController;

  @override
  void initState() {
    super.initState();
    tabController = TabController(length: 2, vsync: this);
    _load();
  }

  Future<void> _load() async {
    final token = await TokenStorage.getToken();
    final userId = token == null ? null : JwtUtil.extractUserId(token);
    if (userId == null || !mounted) return;
    context.read<MyCourseBloc>().add(LoadMyCourses(userId));
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: BlocBuilder<MyCourseBloc, MyCourseState>(
        builder: (context, state) {
          int progressCount = 0;
          int completedCount = 0;

          if (state is MyCourseLoaded) {
            progressCount = state.progressCourses.length;
            completedCount = state.completedCourses.length;
          }

          return NestedScrollView(
            headerSliverBuilder: (BuildContext context, bool innerBoxIsScrolled) {
              return <Widget>[
                SliverAppBar(
                  backgroundColor: AppColor.appBarColor,
                  pinned: true,
                  snap: true,
                  floating: true,
                  title: const MyCourseAppBar(),
                  bottom: TabBar(
                    controller: tabController,
                    indicatorColor: AppColor.primary,
                    indicatorWeight: 1,
                    unselectedLabelColor: AppColor.textColor,
                    labelColor: AppColor.primary,
                    labelStyle: const TextStyle(
                      fontSize: 16,
                      fontWeight: FontWeight.w500,
                    ),
                    indicatorSize: TabBarIndicatorSize.tab,
                    tabs: [
                      Tab(text: "Progress ($progressCount)"),
                      Tab(text: "Completed ($completedCount)"),
                    ],
                  ),
                ),
              ];
            },
            body: Padding(
              padding: const EdgeInsets.only(top: 10, bottom: 10),
              child: _buildBodyByState(state),
            ),
          );
        },
      ),
    );
  }

  Widget _buildBodyByState(MyCourseState state) {
    if (state is MyCourseLoading) {
      return const Center(child: CircularProgressIndicator());
    }
    if (state is MyCourseError) {
      return Center(child: Text(state.message));
    }
    if (state is MyCourseLoaded) {
      return TabBarView(
        controller: tabController,
        children: <Widget>[
          // GIỮ UI CŨ — CHỈ ĐỔ DATA THẬT VÀO
          MyCourseProgressCourseList(myProgressCourses: state.progressCourses),
          MyCourseCompleteCourseList(myCompleteCourses: state.completedCourses),
        ],
      );
    }
    return const SizedBox.shrink();
  }
}
