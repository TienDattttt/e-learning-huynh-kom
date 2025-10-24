import 'package:dio/dio.dart';
import 'package:online_course/core/network/dio_client.dart';
import '../models/my_course_model.dart';

abstract class MyCourseRemoteDataSource {
  Future<List<MyCourseModel>> getMyCourses({required int userId});
  Future<double> getProgress({required int userId, required int courseId});
}

class MyCourseRemoteDataSourceImpl implements MyCourseRemoteDataSource {
  final Dio _dio = DioClient.createDio();

  @override
  Future<List<MyCourseModel>> getMyCourses({required int userId}) async {
    final resp = await _dio.get(
      "/orders/my-courses",
      queryParameters: {"userId": userId},
    );
    final list = (resp.data['data'] as List?) ?? [];
    return list.map((e) => MyCourseModel.fromJson(e)).toList();
  }

  @override
  Future<double> getProgress({required int userId, required int courseId}) async {
    final resp = await _dio.get(
      "/learning/progress/get",
      queryParameters: {"userId": userId, "courseId": courseId},
    );
    // backend trả số thực 0.0 → 100.0
    final val = resp.data['data'];
    if (val is num) return val.toDouble();
    return 0.0;
  }
}
