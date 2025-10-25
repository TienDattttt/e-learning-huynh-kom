import 'package:cloudinary_flutter/cloudinary_object.dart';
import 'package:cloudinary_url_gen/transformation/transformation.dart';
import 'package:cloudinary_url_gen/transformation/resize/resize.dart';
import 'package:cloudinary_url_gen/transformation/delivery/delivery.dart';
import 'package:cloudinary_url_gen/transformation/delivery/delivery_actions.dart';  // Cho Delivery actions

// Singleton giữ nguyên
final CloudinaryObject cld = CloudinaryObject.fromCloudName(
  cloudName: 'dhg7jec6p',
);

// Hàm extractPublicId (bạn nói giữ nguyên, giả sử đã có code đúng, ví dụ như trong hướng dẫn trước)
String extractPublicId(String url) {
  if (url.isEmpty) return 'placeholder';
  Uri uri = Uri.parse(url);
  String path = uri.path;
  int uploadIndex = path.indexOf('/upload/');
  if (uploadIndex == -1) return url;
  String afterUpload = path.substring(uploadIndex + '/upload/'.length);
  if (afterUpload.startsWith('v')) {
    int slashIndex = afterUpload.indexOf('/');
    if (slashIndex != -1) {
      afterUpload = afterUpload.substring(slashIndex + 1);
    }
  }
  return afterUpload.replaceAll(RegExp(r'\.\w+$'), '');
}

// Hàm để build optimized Transformation (trả về Transformation để pass trực tiếp vào widget)
Transformation getOptimizedTransformation({int? width, int? height}) {
  return Transformation()
    ..resize(Resize.fill()..width(width)..height(height))  // Fill mode để crop và fit (tương đương BoxFit.cover)
    ..delivery(Delivery.quality(Quality.auto()));  // Quality auto// Format auto - xóa ; và thêm dòng này
}