import 'package:online_course/src/features/course/data/order_api.dart';

class BuyCourseUseCase {
  final OrderApi orderApi;
  BuyCourseUseCase(this.orderApi);

  Future<(bool success, String message)> call(int courseId) async {
    try {
      final result = await orderApi.buyCourse(courseId);
      final status = result['status']?.toString() ?? 'UNKNOWN';

      if (status == 'SUCCESS' || status == 'ALREADY_OWNED') {
        return (true, status);
      }

      return (false, status);
    } catch (e) {
      return (false, e.toString());
    }
  }
}
