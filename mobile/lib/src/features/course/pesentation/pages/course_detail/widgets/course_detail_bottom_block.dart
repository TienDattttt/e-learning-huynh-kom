import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:online_course/src/features/course/domain/entities/course_detail.dart';
import 'package:online_course/src/features/learning/presentation/pages/learning_player_page.dart';
import 'package:online_course/src/features/learning/presentation/bloc/learning_bloc.dart';
import 'package:online_course/src/theme/app_color.dart';
import 'package:online_course/src/widgets/custom_button.dart';

class CourseDetailBottomBlock extends StatelessWidget {
  const CourseDetailBottomBlock({
    super.key,
    required this.detail,
    required this.purchased,
    this.onBuy,
  });

  final CourseDetail detail;
  final bool purchased;
  final VoidCallback? onBuy;

  @override
  Widget build(BuildContext context) {
    final priceText = (detail.promotionPrice ?? detail.price).toString();

    return Container(
      padding: const EdgeInsets.fromLTRB(15, 0, 15, 20),
      height: 80,
      width: MediaQuery.of(context).size.width,
      decoration: BoxDecoration(
        color: Colors.white,
        boxShadow: [
          BoxShadow(
            color: AppColor.shadowColor.withOpacity(0.05),
            spreadRadius: 1,
            blurRadius: 1,
            offset: const Offset(0, 0),
          ),
        ],
      ),
      child: Row(
        children: [
          if (!purchased)
            Column(
              mainAxisAlignment: MainAxisAlignment.center,
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                const Text(
                  "Price",
                  style: TextStyle(
                    fontSize: 14,
                    color: AppColor.labelColor,
                    fontWeight: FontWeight.w500,
                  ),
                ),
                const SizedBox(height: 3),
                Row(
                  children: [
                    Text(
                      priceText,
                      style: const TextStyle(
                        fontSize: 18,
                        color: AppColor.textColor,
                        fontWeight: FontWeight.w700,
                      ),
                    ),
                    const SizedBox(width: 8),
                    if (detail.promotionPrice != null)
                      Text(
                        detail.price.toString(),
                        style: const TextStyle(
                          fontSize: 14,
                          color: AppColor.labelColor,
                          decoration: TextDecoration.lineThrough,
                        ),
                      ),
                  ],
                ),
              ],
            ),

          if (!purchased) const SizedBox(width: 20),

          // Button
    Expanded(
    child: purchased
    ? const SizedBox.shrink()
        : CustomButton(
    radius: 10,
    title: "Mua ngay",
    onTap: onBuy ?? () {},
    ),
    ),
        ],
      ),
    );
  }
}
