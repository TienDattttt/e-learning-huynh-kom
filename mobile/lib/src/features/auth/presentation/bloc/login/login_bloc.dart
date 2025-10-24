import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:online_course/core/services/token_storage.dart';
import '../../../domain/usecases/login_usecase.dart';
import 'login_event.dart';
import 'login_state.dart';

class LoginBloc extends Bloc<LoginEvent, LoginState> {
  final LoginUseCase loginUseCase;

  LoginBloc(this.loginUseCase) : super(LoginInitial()) {
    on<LoginSubmitted>((event, emit) async {
      emit(LoginLoading());
      try {
        final auth = await loginUseCase(event.email, event.password);
        await TokenStorage.saveToken(auth.token, auth.role);
        emit(LoginSuccess());
      } catch (e) {
        final message = e.toString().replaceFirst('Exception: ', '');
        emit(LoginFailure("Login failed: $message"));
      }
    });
  }
}
