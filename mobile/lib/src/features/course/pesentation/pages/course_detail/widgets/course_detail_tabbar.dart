import 'package:flutter/material.dart';
import 'package:online_course/src/features/course/domain/entities/course_detail.dart';
import 'package:online_course/src/features/course/pesentation/pages/course_detail/widgets/course_detail_lesson_list.dart';
import 'package:online_course/src/theme/app_color.dart';

class CourseDetailTabBar extends StatefulWidget {
  const CourseDetailTabBar({
    super.key,
    required this.detail,
    required this.purchased,
  });

  final CourseDetail detail;
  final bool purchased;

  @override
  State<CourseDetailTabBar> createState() => _CourseDetailTabBarState();
}

class _CourseDetailTabBarState extends State<CourseDetailTabBar>
    with SingleTickerProviderStateMixin {
  late TabController tabController;

  @override
  void initState() {
    super.initState();
    tabController = TabController(length: 2, vsync: this);
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [_buildTabBar(), _buildTabBarPages()],
    );
  }

  Widget _buildTabBar() {
    return TabBar(
      controller: tabController,
      isScrollable: false,
      indicatorColor: AppColor.primary,
      labelColor: AppColor.primary,
      unselectedLabelColor: AppColor.darker,
      tabs: const [
        Tab(child: Text("Bài học", style: TextStyle(fontSize: 16))),
        Tab(child: Text("Bài tập", style: TextStyle(fontSize: 16))),
      ],
    );
  }

  Widget _buildTabBarPages() {
    return SizedBox(
      // Để nội dung tràn theo chiều dọc tự nhiên
      height: 430, // cao giống UI cũ; có thể tinh chỉnh
      width: double.infinity,
      child: TabBarView(
        physics: const NeverScrollableScrollPhysics(),
        controller: tabController,
        children: [
          CourseDetailLessonList(
            chapters: widget.detail.chapters,
            purchased: widget.purchased,
            courseId: widget.detail.id,
          ),
          const Center(
            child: Text(
              "Sắp ra mắt!",
              style: TextStyle(fontSize: 16),
            ),
          ),
        ],
      ),
    );
  }
}
