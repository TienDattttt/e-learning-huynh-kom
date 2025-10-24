import 'package:flutter_secure_storage/flutter_secure_storage.dart';

class TokenStorage {
  static const _accessTokenKey = 'access_token';
  static const _roleKey = 'user_role';

  static const _storage = FlutterSecureStorage();

  static Future<void> saveToken(String token, String role) async {
    await _storage.write(key: _accessTokenKey, value: token);
    await _storage.write(key: _roleKey, value: role);
  }

  static Future<String?> getToken() async {
    return await _storage.read(key: _accessTokenKey);
  }

  static Future<String?> getRole() async {
    return await _storage.read(key: _roleKey);
  }

  static Future<void> clear() async {
    await _storage.deleteAll();
  }
}
