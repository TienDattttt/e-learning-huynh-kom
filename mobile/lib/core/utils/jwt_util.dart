import 'dart:convert';

class JwtUtil {
  /// Trả về uid (int) từ access token, hoặc null nếu không đọc được
  static int? extractUserId(String token) {
    try {
      final parts = token.split('.');
      if (parts.length != 3) return null;
      final payload = _normalize(parts[1]);
      final decoded = json.decode(utf8.decode(base64Url.decode(payload)));
      // Backend của bạn có field uid trong token (đã thấy ở ví dụ token)
      final uid = decoded['uid'];
      if (uid is int) return uid;
      if (uid is String) return int.tryParse(uid);
      return null;
    } catch (_) {
      return null;
    }
  }

  static String _normalize(String input) {
    // base64Url padding
    final pad = input.length % 4;
    if (pad == 2) return '$input==';
    if (pad == 3) return '$input=';
    if (pad == 1) return '${input}===';
    return input;
    // (pad==0) return input;
  }
}
