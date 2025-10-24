import '../../domain/entities/course.dart';
import '../models/course_model.dart';

class CourseMapper {
  static Course toEntity(CourseModel m) {
    return Course(
      id: m.id,
      name: m.name,
      image: m.image,
      // Option A1: ưu tiên promotionPrice nếu có
      price: (m.promotionPrice ?? m.price).toString(),
      // Các field dưới đây không dùng ở Explore (đặt rỗng để không crash)
      session: '',
      duration: '',
      review: '',
      isFavorited: false,
      // Nếu Course entity của bạn BẮT BUỘC có description:
      description: m.description,
    );
  }
}
