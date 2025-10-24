import 'package:dio/dio.dart';
import 'package:online_course/core/network/dio_client.dart';
import '../models/auth_response.dart';

class AuthRemoteDataSource {
  final _dio = DioClient.createDio();

  Future<AuthResponse> login(String email, String password) async {
    try {
      final response = await _dio.post("/auth/login", data: {
        "email": email,
        "password": password
      });
      return AuthResponse.fromJson(response.data);
    } on DioException catch (e) {
      if (e.response != null) {
        return AuthResponse.fromJson(e.response!.data);
      }
      rethrow;
    }
  }

  Future<AuthResponse> register(
      String fullName, String email, String password, String role) async {
    try {
      final response = await _dio.post("/auth/register", data: {
        "fullName": fullName,
        "email": email,
        "password": password,
        "role": role
      });
      return AuthResponse.fromJson(response.data);
    } on DioException catch (e) {
      if (e.response != null && e.response?.data != null) {
        // ✅ Trả về message lỗi từ backend (vd: "Email already exists")
        return AuthResponse.fromJson(e.response!.data);
      }
      rethrow;
    }
  }
}
