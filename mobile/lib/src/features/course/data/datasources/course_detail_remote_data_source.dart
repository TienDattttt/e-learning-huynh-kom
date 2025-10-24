import 'package:dio/dio.dart';
import 'package:online_course/core/network/dio_client.dart';
import '../models/course_detail_model.dart';

abstract class CourseDetailRemoteDataSource {
  Future<CourseDetailModel> getPublicDetail(int courseId);
  Future<CourseDetailModel> getPrivateDetail(int userId, int courseId);
}

class CourseDetailRemoteDataSourceImpl implements CourseDetailRemoteDataSource {
  final Dio _dio = DioClient.createDio();

  @override
  Future<CourseDetailModel> getPublicDetail(int courseId) async {
    final resp = await _dio.get("/courses/public/detail", queryParameters: {"courseId": courseId});
    return CourseDetailModel.fromPublicJson(resp.data);
    // image backend đã trả kiểu "assets/xxx.jpg" -> dùng Image.asset
  }

  @override
  Future<CourseDetailModel> getPrivateDetail(int userId, int courseId) async {
    final resp = await _dio.get("/learning/my-course/detail",
        queryParameters: {"userId": userId, "courseId": courseId});
    return CourseDetailModel.fromPrivateJson(resp.data);
  }
}
