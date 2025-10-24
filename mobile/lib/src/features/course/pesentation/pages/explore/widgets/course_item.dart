import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:online_course/core/utils/app_util.dart';
import 'package:online_course/src/features/course/domain/entities/course.dart';
import 'package:online_course/src/features/course/pesentation/bloc/favorite_course/favorite_course_bloc.dart';
import 'package:online_course/src/theme/app_color.dart';
import 'package:online_course/src/widgets/custom_image.dart';
import 'package:online_course/src/widgets/favorite_box_v2.dart';

class CourseItem extends StatelessWidget {
  const CourseItem(
      {Key? key,
      required this.course,
      this.onTap,
      this.width = 200,
      this.height = 290})
      : super(key: key);
  final Course course;
  final double width;
  final double height;
  final GestureTapCallback? onTap;

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: onTap,
      child: Container(
        width: width,
        height: height,
        padding: const EdgeInsets.all(10),
        margin: const EdgeInsets.only(bottom: 5, top: 5),
        decoration: BoxDecoration(
          color: Colors.white,
          borderRadius: BorderRadius.circular(20),
          boxShadow: [
            BoxShadow(
              color: AppColor.shadowColor.withOpacity(0.1),
              spreadRadius: 1,
              blurRadius: 1,
              offset: const Offset(1, 1), // changes position of shadow
            ),
          ],
        ),
        child: Stack(
          children: [
            _buildCourseImage(),

            Positioned(
              top: 210,
              child: _buildCourseInfo(),
            )
          ],
        ),
      ),
    );
  }


  Widget _buildCourseInfo() {
    return Container(
      width: width - 50,
      padding: const EdgeInsets.fromLTRB(5, 0, 5, 0),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            course.name,
            maxLines: 1,
            overflow: TextOverflow.ellipsis,
            style: const TextStyle(
              fontSize: 17,
              color: AppColor.textColor,
              fontWeight: FontWeight.w600,
            ),
          ),
          const SizedBox(height: 6),
          // Chỉ còn 1 dòng giá (đã dùng promotionPrice trong mapper)
          Text(
            _formatPrice(course.price),
            style: const TextStyle(
              fontSize: 16,
              color: AppColor.primary,
              fontWeight: FontWeight.w700,
            ),
          ),
        ],
      ),
    );
  }

  // Bạn có thể dùng AppUtil riêng của bạn nếu có formatCurrency.
  String _formatPrice(String v) {
    // Nếu bạn có AppUtil.formatCurrency, dùng như sau:
    // return AppUtil.formatCurrency(int.tryParse(v) ?? 0);
    final n = int.tryParse(v) ?? 0;
    // format đơn giản: 900000 -> "900.000đ"
    final s = n.toString().replaceAllMapped(
      RegExp(r'(\d)(?=(\d{3})+(?!\d))'),
          (m) => '${m[1]}.',
    );
    return '$sđ';
  }


  Widget _buildCourseImage() {
    return Hero(
      tag: '${course.id}${course.image}',
      child: ClipRRect(
        borderRadius: BorderRadius.circular(15),
        child: Image.asset(
          "${course.image}",
          width: width,
          height: 190,
          fit: BoxFit.cover,
        ),
      ),
    );
  }



  Widget _buildAttribute(IconData icon, Color color, String info) {
    return Row(
      children: [
        Icon(
          icon,
          size: 18,
          color: color,
        ),
        const SizedBox(
          width: 3,
        ),
        Text(
          info,
          maxLines: 1,
          overflow: TextOverflow.ellipsis,
          style: const TextStyle(color: AppColor.labelColor, fontSize: 13),
        ),
      ],
    );
  }
}
