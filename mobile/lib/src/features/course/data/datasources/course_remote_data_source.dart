import 'package:dio/dio.dart';
import 'package:online_course/core/network/dio_client.dart';
import '../models/course_model.dart';

abstract class CourseRemoteDataSource {
  Future<List<CourseModel>> getCourses({int page, int size});
}

class CourseRemoteDataSourceImpl implements CourseRemoteDataSource {
  final Dio _dio = DioClient.createDio();

  @override
  Future<List<CourseModel>> getCourses({int page = 0, int size = 20}) async {
    final resp = await _dio.get(
      "/courses/public/list",
      queryParameters: {"page": page, "size": size},
    );
    final list = (resp.data['data']?['content'] as List?) ?? [];
    return list.map((e) => CourseModel.fromJson(e)).toList();
  }
}
