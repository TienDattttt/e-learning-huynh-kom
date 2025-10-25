import 'package:flutter/material.dart';
import 'package:online_course/src/features/course/domain/entities/my_course.dart';
import 'package:online_course/src/theme/app_color.dart';
import 'package:online_course/src/widgets/custom_image.dart';
import 'package:cloudinary_flutter/image/cld_image.dart';
import 'package:online_course/core/utils/cloudinary_helper.dart';

class MyCourseItem extends StatelessWidget {
  const MyCourseItem({
    required this.data,
    Key? key,
    this.progressColor = AppColor.blue,
    this.onTap,
  }) : super(key: key);

  final MyCourse data;
  final Color progressColor;
  final GestureTapCallback? onTap;

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: onTap,
      child: Container(
        padding: const EdgeInsets.all(10),
        margin: const EdgeInsets.symmetric(vertical: 6, horizontal: 12),
        decoration: BoxDecoration(
          borderRadius: BorderRadius.circular(20),
          color: Colors.white,
          boxShadow: [
            BoxShadow(
              color: AppColor.shadowColor.withOpacity(0.1),
              spreadRadius: 1,
              blurRadius: 1,
            ),
          ],
        ),
        child: Row(
          children: [
            ClipRRect(
              borderRadius: BorderRadius.circular(10),
              child: CldImageWidget(
                publicId: extractPublicId(data.image),
                transformation: getOptimizedTransformation(width: 70, height: 70),
                width: 70,
                height: 70,
                fit: BoxFit.cover,
                placeholder: (context, url) => const CircularProgressIndicator(),
                errorBuilder: (context, url, error) => const Icon(Icons.error),
              ),
            ),
            const SizedBox(width: 10),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(data.name, maxLines: 1, overflow: TextOverflow.ellipsis, style: const TextStyle(
                      fontSize: 15,
                      fontWeight: FontWeight.w500)),
                  const SizedBox(height: 6),
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      Text(data.completedLabel, style: TextStyle(color: progressColor)),
                      Visibility(
                        visible: data.progress < 100,
                        child: Text(data.progressLabel, style: const TextStyle(fontSize: 12, color: AppColor.labelColor)),
                      ),
                    ],
                  ),
                  const SizedBox(height: 7),
                  LinearProgressIndicator(
                    value: data.progressPercent,
                    backgroundColor: progressColor.withOpacity(.2),
                    valueColor: AlwaysStoppedAnimation<Color>(progressColor),
                  ),
                ],
              ),
            )
          ],
        ),
      ),
    );
  }
}

