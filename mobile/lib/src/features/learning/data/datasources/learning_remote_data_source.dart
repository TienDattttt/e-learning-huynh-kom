import 'package:dio/dio.dart';
import 'package:online_course/core/network/dio_client.dart';
import 'package:online_course/core/services/token_storage.dart';

class LearningRemoteDataSource {
  final Dio _dio = DioClient.createDio();

  /// Lưu tiến độ bài học
  Future<void> saveProgress({
    required int courseId,
    required int lessonId,
    required int progressPercent,
    bool completed = false,
  }) async {
    final token = await TokenStorage.getToken();
    await _dio.post(
      "/learning/progress/save",
      data: {
        "courseId": courseId,
        "lessonId": lessonId,
        "progressPercent": progressPercent,
        "completed": completed,
      },
      options: Options(headers: {"Authorization": "Bearer $token"}),
    );
  }

  /// Lấy tổng tiến độ khóa học
  Future<double> getProgress(int courseId) async {
    final token = await TokenStorage.getToken();
    final resp = await _dio.get(
      "/learning/progress/get",
      queryParameters: {"courseId": courseId},
      options: Options(headers: {"Authorization": "Bearer $token"}),
    );
    return (resp.data['data'] as num?)?.toDouble() ?? 0;
  }
}
