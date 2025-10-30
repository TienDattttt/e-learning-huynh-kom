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
import 'package:online_course/src/features/course/data/order_api.dart';


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
  Future<void> _buyCourse(BuildContext context, int courseId) async {
    try {
      final api = OrderApi();
      final result = await api.buyCourse(courseId);

      if (result['status'] == 'SUCCESS') {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Mua khóa học thành công!')),
        );
        // Reload lại chi tiết khóa để cập nhật trạng thái purchased = true
        _loadDetail();
      } else if (result['status'] == 'ALREADY_OWNED') {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Bạn đã sở hữu khóa học này.')),
        );
        _loadDetail();
      } else {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Lỗi mua khóa học: ${result['status']}')),
        );
      }
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('Mua thất bại: $e')),
      );
    }
  }

  void _learnNow(BuildContext context, int courseId) {
    // TODO: Điều hướng sang màn học (CoursePlayerPage)
    ScaffoldMessenger.of(context).showSnackBar(
      const SnackBar(content: Text('Chức năng học sẽ được thêm sau.')),
    );
  }

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
              onBuy: () => _buyCourse(context, state.detail.id),
              onLearnNow: () => _learnNow(context, state.detail.id),
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

