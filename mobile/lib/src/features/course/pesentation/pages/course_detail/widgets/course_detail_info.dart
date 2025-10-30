import 'package:flutter/material.dart';
import 'package:online_course/src/features/course/domain/entities/course_detail.dart';
import 'package:online_course/src/theme/app_color.dart';
import 'package:readmore/readmore.dart';

class CourseDetailInfo extends StatelessWidget {
  const CourseDetailInfo({
    super.key,
    required this.detail,
    required this.purchased,
  });

  final CourseDetail detail;
  final bool purchased;

  @override
  Widget build(BuildContext context) {
    final priceText = (detail.promotionPrice ?? detail.price).toString();

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        // Title
        Text(
          detail.name,
          maxLines: 2,
          overflow: TextOverflow.ellipsis,
          style: const TextStyle(fontSize: 20, fontWeight: FontWeight.w600),
        ),
        const SizedBox(height: 12),

        // Price row (nếu chưa mua)
        if (!purchased)
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
        if (!purchased) const SizedBox(height: 12),

        const Text(
          "Về khóa học",
          style: TextStyle(fontSize: 16, fontWeight: FontWeight.w500),
        ),
        const SizedBox(height: 6),

        ReadMoreText(
          detail.description.isNotEmpty ? detail.description : detail.content,
          style: const TextStyle(color: AppColor.labelColor, height: 1.5),
          trimLines: 3,
          trimMode: TrimMode.Line,
          trimCollapsedText: 'Show more',
          trimExpandedText: 'Show less',
          moreStyle: const TextStyle(
            fontSize: 14,
            fontWeight: FontWeight.bold,
            color: AppColor.red,
          ),
        ),

        if (purchased) ...[
          const SizedBox(height: 14),
          Row(
            children: [
              const Icon(Icons.check_circle, size: 18, color: AppColor.primary),
              const SizedBox(width: 6),
              Text(
                "Progress: ${(detail.overallProgress * 100).toStringAsFixed(0)}%",
                style: const TextStyle(
                  fontSize: 14,
                  color: AppColor.labelColor,
                  fontWeight: FontWeight.w500,
                ),
              ),
            ],
          ),
        ],
      ],
    );
  }
}
