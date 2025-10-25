import 'package:flutter/material.dart';
import 'package:online_course/src/features/course/domain/entities/course_detail.dart';
import 'package:cloudinary_flutter/image/cld_image.dart';
import 'package:online_course/core/utils/cloudinary_helper.dart';

class CourseDetailImage extends StatelessWidget {
  const CourseDetailImage({
    super.key,
    required this.detail,
    this.isHero = false,
  });

  final CourseDetail detail;
  final bool isHero;

  @override
  Widget build(BuildContext context) {
    String publicId = extractPublicId(detail.image);
    Widget imageWidget = CldImageWidget(
      publicId: publicId,  // Public ID từ URL
      transformation: getOptimizedTransformation(width: 400, height: 220),  // Optimized transformation
      width: double.infinity,
      height: 220,
      fit: BoxFit.cover,
      placeholder: (context, url) => const CircularProgressIndicator(),
      errorBuilder: (context, url, error) => const Icon(Icons.error),
    );

    return isHero
        ? Hero(
      tag: '${detail.id}${detail.image}',
      child: imageWidget,
    )
        : imageWidget;
  }
}

//   @override
//   Widget build(BuildContext context) {
//     return isHero
//         ? Hero(
//       tag: '${detail.id}${detail.image}',
//       child: Image.network(
//         detail.image,
//         width: double.infinity,
//         height: 220,
//         fit: BoxFit.cover,
//         errorBuilder: (context, error, stackTrace) => const Icon(Icons.error),
//       ),
//     )
//         : Image.network(
//       detail.image,
//       width: double.infinity,
//       height: 220,
//       fit: BoxFit.cover,
//       errorBuilder: (context, error, stackTrace) => const Icon(Icons.error),
//     );
//   }
// }
