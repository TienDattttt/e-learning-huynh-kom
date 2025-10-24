import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:online_course/core/services/injection_container.dart';
import 'package:online_course/src/features/auth/presentation/bloc/login/login_bloc.dart';
import 'package:online_course/src/features/auth/presentation/bloc/login/login_event.dart';
import 'package:online_course/src/features/auth/presentation/bloc/login/login_state.dart';
import 'package:online_course/src/features/auth/presentation/pages/register_page.dart';
import 'package:online_course/src/root_app.dart';

// TODO: Nếu muốn dùng widget tùy biến của bạn, import 2 file sau và thay ElevatedButton/TextField ở chỗ TODO:
// import 'package:online_course/widgets/custom_textfield.dart';
// import 'package:online_course/widgets/custom_button.dart';

class LoginPage extends StatefulWidget {
  const LoginPage({super.key});

  static Route route() => MaterialPageRoute(builder: (_) => const LoginPage());

  @override
  State<LoginPage> createState() => _LoginPageState();
}

class _LoginPageState extends State<LoginPage> {
  final _emailCtrl = TextEditingController();
  final _passCtrl = TextEditingController();
  final _formKey = GlobalKey<FormState>();
  bool _obscure = true;

  @override
  void dispose() {
    _emailCtrl.dispose();
    _passCtrl.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);

    return BlocProvider<LoginBloc>(
      create: (_) => locator<LoginBloc>(),
      child: Scaffold(
        backgroundColor: theme.colorScheme.background,
        body: SafeArea(
          child: BlocConsumer<LoginBloc, LoginState>(
            listener: (context, state) {
              if (state is LoginSuccess) {
                // Đăng nhập ok → vào RootApp (flow C)
                Navigator.of(context).pushAndRemoveUntil(
                  MaterialPageRoute(builder: (_) => const RootApp()),
                      (route) => false,
                );
              } else if (state is LoginFailure) {
                ScaffoldMessenger.of(context).showSnackBar(
                  SnackBar(content: Text(state.message)),
                );
              }
            },
            builder: (context, state) {
              final isLoading = state is LoginLoading;

              return Center(
                child: SingleChildScrollView(
                  padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 16),
                  child: ConstrainedBox(
                    constraints: const BoxConstraints(maxWidth: 500),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.stretch,
                      children: [
                        // ===== Logo chữ =====
                        Text(
                          "OnlineCourse",
                          textAlign: TextAlign.center,
                          style: theme.textTheme.headlineMedium?.copyWith(
                            fontWeight: FontWeight.w700,
                          ),
                        ),
                        const SizedBox(height: 8),
                        Text(
                          "Welcome back!",
                          textAlign: TextAlign.center,
                          style: theme.textTheme.titleMedium?.copyWith(
                            color: theme.textTheme.bodyMedium?.color?.withOpacity(0.8),
                          ),
                        ),
                        const SizedBox(height: 28),

                        // ===== Form =====
                        Form(
                          key: _formKey,
                          child: Column(
                            children: [
                              // TODO: thay bằng CustomTextField nếu muốn
                              TextFormField(
                                controller: _emailCtrl,
                                keyboardType: TextInputType.emailAddress,
                                decoration: const InputDecoration(
                                  labelText: "Email",
                                  hintText: "you@example.com",
                                  prefixIcon: Icon(Icons.email_outlined),
                                ),
                                validator: (v) {
                                  if (v == null || v.trim().isEmpty) {
                                    return "Please enter your email";
                                  }
                                  final ok = RegExp(r'^[^@]+@[^@]+\.[^@]+').hasMatch(v.trim());
                                  if (!ok) return "Invalid email";
                                  return null;
                                },
                              ),
                              const SizedBox(height: 16),

                              // TODO: thay bằng CustomTextField nếu muốn
                              TextFormField(
                                controller: _passCtrl,
                                obscureText: _obscure,
                                decoration: InputDecoration(
                                  labelText: "Password",
                                  hintText: "••••••",
                                  prefixIcon: const Icon(Icons.lock_outline),
                                  suffixIcon: IconButton(
                                    onPressed: () => setState(() => _obscure = !_obscure),
                                    icon: Icon(_obscure ? Icons.visibility_outlined : Icons.visibility_off_outlined),
                                  ),
                                ),
                                validator: (v) {
                                  if (v == null || v.isEmpty) {
                                    return "Please enter your password";
                                  }
                                  if (v.length < 6) return "Password must be at least 6 characters";
                                  return null;
                                },
                              ),
                            ],
                          ),
                        ),
                        const SizedBox(height: 24),

                        // ===== Login Button =====
                        // TODO: thay bằng CustomButton nếu muốn
                        SizedBox(
                          height: 48,
                          child: FilledButton(
                            onPressed: isLoading
                                ? null
                                : () {
                              if (_formKey.currentState?.validate() != true) return;
                              context.read<LoginBloc>().add(
                                LoginSubmitted(
                                  _emailCtrl.text.trim(),
                                  _passCtrl.text.trim(),
                                ),
                              );
                            },
                            child: isLoading
                                ? const SizedBox(
                              height: 22,
                              width: 22,
                              child: CircularProgressIndicator(strokeWidth: 2),
                            )
                                : const Text("Login"),
                          ),
                        ),

                        const SizedBox(height: 16),
                        Align(
                          alignment: Alignment.centerRight,
                          child: TextButton(
                            onPressed: () {
                              // TODO: sau này thêm màn Forgot Password nếu cần
                            },
                            child: const Text("Forgot password?"),
                          ),
                        ),

                        const SizedBox(height: 12),
                        Row(
                          mainAxisAlignment: MainAxisAlignment.center,
                          children: [
                            Text(
                              "Don't have an account?",
                              style: theme.textTheme.bodyMedium,
                            ),
                            TextButton(
                              onPressed: () {
                                Navigator.of(context).push(
                                  MaterialPageRoute(builder: (_) => const RegisterPage()),
                                );
                              },
                              child: const Text("Register"),
                            )
                          ],
                        ),
                      ],
                    ),
                  ),
                ),
              );
            },
          ),
        ),
      ),
    );
  }
}
