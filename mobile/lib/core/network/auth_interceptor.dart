import 'package:dio/dio.dart';
import '../services/token_storage.dart';

class AuthInterceptor extends Interceptor {
  @override
  void onRequest(
      RequestOptions options, RequestInterceptorHandler handler) async {

    final token = await TokenStorage.getToken();

    if (token != null && token.isNotEmpty) {
      options.headers['Authorization'] = 'Bearer $token';
    }

    return handler.next(options);
  }

  @override
  void onError(DioException err, ErrorInterceptorHandler handler) {
    // Nếu Backend trả 401 = token hết hạn → sau này có thể Refresh Token
    if (err.response?.statusCode == 401) {
      // TODO: xử lý refresh token sau này
    }
    return handler.next(err);
  }
}
