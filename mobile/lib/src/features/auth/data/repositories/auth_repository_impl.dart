import '../../domain/entities/auth_entity.dart';
import '../../domain/repositories/auth_repository.dart';
import '../datasources/auth_remote_data_source.dart';

class AuthRepositoryImpl implements AuthRepository {
  final AuthRemoteDataSource remoteDataSource;

  AuthRepositoryImpl(this.remoteDataSource);

  @override
  Future<AuthEntity> login(String email, String password) async {
    final response = await remoteDataSource.login(email, password);

    // Nếu thất bại → quăng lỗi để UI hiển thị
    if (!response.success || response.data == null) {
      throw Exception(response.message);
    }

    // Thành công → trả về entity
    return AuthEntity(
      token: response.data!.accessToken,
      role: response.data!.scope,
    );
  }
}
