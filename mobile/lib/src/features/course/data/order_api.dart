import 'dart:convert';
import 'package:dio/dio.dart';
import 'package:online_course/core/services/token_storage.dart';
import 'package:online_course/core/utils/network_url.dart';

class OrderApi {
  final Dio _dio = Dio();

  /// Mua khóa học
  Future<Map<String, dynamic>> buyCourse(
      int courseId, {
        String? payMethod,
        String? voucherCode,
      }) async {
    final token = await TokenStorage.getToken();
    if (token == null) {
      throw Exception('User not logged in');
    }

    final headers = {
      'Authorization': 'Bearer $token',
      'Content-Type': 'application/json',
    };

    final body = {
      'courseId': courseId,
      'payMethod': payMethod ?? 'AUTO',
      'voucherCode': voucherCode,
    };

    final response = await _dio.post(
      '${NetworkUrl.baseUrl}/orders/buy',
      data: jsonEncode(body),
      options: Options(headers: headers),
    );

    if (response.statusCode == 200 && response.data != null) {
      return response.data['data'];
    } else {
      throw Exception('Buy course failed: ${response.statusMessage}');
    }
  }

  /// Lấy danh sách khóa học đã mua
  Future<List<dynamic>> getMyCourses() async {
    final token = await TokenStorage.getToken();
    if (token == null) {
      throw Exception('User not logged in');
    }

    final headers = {
      'Authorization': 'Bearer $token',
    };

    final response = await _dio.get(
      '${NetworkUrl.baseUrl}/orders/my-courses',
      options: Options(headers: headers),
    );

    if (response.statusCode == 200 && response.data != null) {
      return response.data['data'];
    } else {
      throw Exception('Fetch my courses failed: ${response.statusMessage}');
    }
  }
}
