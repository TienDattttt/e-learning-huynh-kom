import 'package:dio/dio.dart';
import '../services/token_storage.dart';

class AuthInterceptor extends Interceptor {
  @override
  void onRequest(
      RequestOptions options, RequestInterceptorHandler handler) async {
    // Skip thêm token cho endpoint public (login/register)
    if (options.path.contains('/auth/login') ||
        options.path.contains('/auth/register')) {
      return handler.next(options);  // Không thêm header, tiếp tục request
    }

    final token = await TokenStorage.getToken();

    if (token != null && token.isNotEmpty) {
      options.headers['Authorization'] = 'Bearer $token';
    }

    return handler.next(options);
  }

  @override
  void onError(DioException err, ErrorInterceptorHandler handler) {
    if (err.response?.statusCode == 401) {
      // Xóa token expired để tránh lặp lỗi
      TokenStorage.clear();  // Giả sử TokenStorage có method clearToken(), nếu không thì thêm (xem dưới)
      // TODO: Xử lý refresh token sau này nếu backend hỗ trợ
    }
    return handler.next(err);
  }
}