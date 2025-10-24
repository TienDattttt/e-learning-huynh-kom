import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:online_course/src/theme/app_color.dart';
import 'package:online_course/src/widgets/custom_appbar.dart';
import 'package:online_course/core/services/token_storage.dart';
import 'package:online_course/core/utils/jwt_util.dart';

import 'package:online_course/src/features/course/domain/entities/course.dart';
import 'package:online_course/src/features/course/domain/entities/course_detail.dart';

import 'package:online_course/src/features/course/pesentation/bloc/detail/course_detail_bloc.dart';
import 'package:online_course/src/features/course/pesentation/bloc/detail/course_detail_event.dart';
import 'package:online_course/src/features/course/pesentation/bloc/detail/course_detail_state.dart';

import 'widgets/course_detail_bottom_block.dart';
import 'widgets/course_detail_image.dart';
import 'widgets/course_detail_info.dart';
import 'widgets/course_detail_tabbar.dart';

class CourseDetailPage extends StatefulWidget {
  const CourseDetailPage({
    required this.courseId,
    this.course,
    this.isHero = false,
    Key? key,
  }) : super(key: key);

  final int courseId;
  final Course? course;
  final bool isHero;

  @override
  State<CourseDetailPage> createState() => _CourseDetailPageState();
}

class _CourseDetailPageState extends State<CourseDetailPage> {
  @override
  void initState() {
    super.initState();
    _loadDetail();
  }

  Future<void> _loadDetail() async {
    final token = await TokenStorage.getToken();
    final userId = token == null ? null : JwtUtil.extractUserId(token);
    context.read<CourseDetailBloc>().add(
      LoadCourseDetail(courseId: widget.courseId, userId: userId),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppColor.appBgColor,
      appBar: _buildAppBar(),
      body: BlocBuilder<CourseDetailBloc, CourseDetailState>(
        builder: (context, state) {
          if (state is CourseDetailLoading) {
            return const Center(child: CircularProgressIndicator());
          }
          if (state is CourseDetailError) {
            return Center(child: Text(state.message));
          }
          if (state is CourseDetailLoaded) {
            return _buildMainBody(state.detail, state.purchased);
          }
          return const SizedBox.shrink();
        },
      ),
      bottomNavigationBar: BlocBuilder<CourseDetailBloc, CourseDetailState>(
        builder: (context, state) {
          if (state is CourseDetailLoaded) {
            return CourseDetailBottomBlock(
              detail: state.detail,
              purchased: state.purchased,
            );
          }
          return const SizedBox.shrink();
        },
      ),
    );
  }

  AppBar _buildAppBar() {
    return AppBar(
      automaticallyImplyLeading: true,
      backgroundColor: Colors.white,
      elevation: 0,
      iconTheme: const IconThemeData(color: AppColor.textColor),
      title: const Text(
        "Detail",
        style: TextStyle(
          color: AppColor.textColor,
          fontSize: 24,
          fontWeight: FontWeight.w600,
        ),
      ),
    );
  }

  Widget _buildMainBody(CourseDetail detail, bool purchased) {
    return SingleChildScrollView(
      padding: const EdgeInsets.fromLTRB(15, 15, 15, 80),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          CourseDetailImage(detail: detail, isHero: widget.isHero),
          const SizedBox(height: 15),
          CourseDetailInfo(detail: detail, purchased: purchased),
          const SizedBox(height: 5),
          const Divider(),
          CourseDetailTabBar(detail: detail, purchased: purchased),
        ],
      ),
    );
  }
}

