class AuthResponse {
  final bool success;
  final String message;
  final AuthData? data;

  AuthResponse({
    required this.success,
    required this.message,
    required this.data,
  });

  factory AuthResponse.fromJson(Map<String, dynamic> json) {
    return AuthResponse(
      success: json['success'] ?? false,
      message: json['message'] ?? "",
      data: json['data'] != null
          ? AuthData.fromJson(json['data'])
          : null, // ✅ tránh crash
    );
  }
}

class AuthData {
  final String accessToken;
  final int expiresIn;
  final String scope;

  AuthData({
    required this.accessToken,
    required this.expiresIn,
    required this.scope,
  });

  factory AuthData.fromJson(Map<String, dynamic> json) {
    return AuthData(
      accessToken: json['accessToken'] ?? "",
      expiresIn: json['expiresIn'] ?? 0,
      scope: json['scope'] ?? "",
    );
  }
}