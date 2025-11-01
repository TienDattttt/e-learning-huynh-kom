import 'package:flutter/material.dart';
import 'package:online_course/core/services/injection_container.dart';
import 'package:online_course/src/features/auth/data/datasources/auth_remote_data_source.dart';

// TODO: Nếu muốn dùng widget tùy biến của bạn, import 2 file sau và thay ElevatedButton/TextField ở chỗ TODO:
// import 'package:online_course/widgets/custom_textfield.dart';
// import 'package:online_course/widgets/custom_button.dart';

class RegisterPage extends StatefulWidget {
  const RegisterPage({super.key});

  @override
  State<RegisterPage> createState() => _RegisterPageState();
}

class _RegisterPageState extends State<RegisterPage> {
  final _fullCtrl = TextEditingController();
  final _emailCtrl = TextEditingController();
  final _passCtrl = TextEditingController();
  final _formKey = GlobalKey<FormState>();
  bool _obscure = true;
  bool _loading = false;

  @override
  void dispose() {
    _fullCtrl.dispose();
    _emailCtrl.dispose();
    _passCtrl.dispose();
    super.dispose();
  }

  Future<void> _onRegister() async {
    if (_formKey.currentState?.validate() != true) return;

    setState(() => _loading = true);
    try {
      // Mặc định role = HocVien (theo yêu cầu hiện tại)
      final ds = locator<AuthRemoteDataSource>();
      final result = await ds.register(
        _fullCtrl.text.trim(),
        _emailCtrl.text.trim(),
        _passCtrl.text.trim(),
        "HocVien",
      );

      if (!mounted) return;

      // ✅ Nếu backend trả success=false → show message lỗi
      if (!result.success) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text(result.message)),
        );
        return;
      }

      // ✅ Thành công
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text("Registration successful! Please log in.")),
      );
      Navigator.of(context).pop();

    } catch (e) {
      if (!mounted) return;
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text("Registration failed: $e")),
      );
    } finally {
      if (mounted) setState(() => _loading = false);
    }
  }


  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);

    return Scaffold(
      backgroundColor: theme.colorScheme.background,
      appBar: AppBar(
        title: const Text("Register"),
        centerTitle: true,
      ),
      body: SafeArea(
        child: Center(
          child: SingleChildScrollView(
            padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 16),
            child: ConstrainedBox(
              constraints: const BoxConstraints(maxWidth: 500),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.stretch,
                children: [
                  // ===== Logo chữ =====
                  Text(
                    "Khóa học trực tuyến",
                    textAlign: TextAlign.center,
                    style: theme.textTheme.headlineSmall?.copyWith(
                      fontWeight: FontWeight.w700,
                    ),
                  ),
                  const SizedBox(height: 8),
                  Text(
                    "Tạo tài khoản",
                    textAlign: TextAlign.center,
                    style: theme.textTheme.titleMedium?.copyWith(
                      color: theme.textTheme.bodyMedium?.color?.withOpacity(0.8),
                    ),
                  ),
                  const SizedBox(height: 28),

                  Form(
                    key: _formKey,
                    child: Column(
                      children: [
                        // TODO: thay bằng CustomTextField nếu muốn
                        TextFormField(
                          controller: _fullCtrl,
                          textInputAction: TextInputAction.next,
                          decoration: const InputDecoration(
                            labelText: "Họ và tên",
                            prefixIcon: Icon(Icons.person_outline),
                          ),
                          validator: (v) {
                            if (v == null || v.trim().isEmpty) {
                              return "Vui lòng nhập họ và tên của bạn";
                            }
                            return null;
                          },
                        ),
                        const SizedBox(height: 16),

                        // TODO: thay bằng CustomTextField nếu muốn
                        TextFormField(
                          controller: _emailCtrl,
                          keyboardType: TextInputType.emailAddress,
                          textInputAction: TextInputAction.next,
                          decoration: const InputDecoration(
                            labelText: "Email",
                            hintText: "you@example.com",
                            prefixIcon: Icon(Icons.email_outlined),
                          ),
                          validator: (v) {
                            if (v == null || v.trim().isEmpty) {
                              return "Vui lòng nhập email của bạn";
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
                            labelText: "Mật khẩu",
                            hintText: "••••••",
                            prefixIcon: const Icon(Icons.lock_outline),
                            suffixIcon: IconButton(
                              onPressed: () => setState(() => _obscure = !_obscure),
                              icon: Icon(_obscure ? Icons.visibility_outlined : Icons.visibility_off_outlined),
                            ),
                          ),
                          validator: (v) {
                            if (v == null || v.isEmpty) {
                              return "Vui lòng nhập mật khẩu của bạn";
                            }
                            if (v.length < 8) {
                              return "Mật khẩu phải có ít nhất 8 ký tự";
                            }
                            if (!RegExp(r'[a-z]').hasMatch(v)) {
                              return "Mật khẩu phải chứa ít nhất 1 chữ cái thường";
                            }
                            if (!RegExp(r'[A-Z]').hasMatch(v)) {
                              return "Mật khẩu phải chứa ít nhất 1 chữ cái in hoa";
                            }
                            if (!RegExp(r'\d').hasMatch(v)) {
                              return "Mật khẩu phải chứa ít nhất 1 chữ số";
                            }
                            if (!RegExp(r'[\W_]').hasMatch(v)) {
                              return "Mật khẩu phải chứa ít nhất 1 ký tự đặc biệt";
                            }
                            return null;
                          },

                        ),
                      ],
                    ),
                  ),
                  const SizedBox(height: 24),

                  // ===== Register Button =====
                  // TODO: thay bằng CustomButton nếu muốn
                  SizedBox(
                    height: 48,
                    child: FilledButton(
                      onPressed: _loading ? null : _onRegister,
                      child: _loading
                          ? const SizedBox(
                        height: 22,
                        width: 22,
                        child: CircularProgressIndicator(strokeWidth: 2),
                      )
                          : const Text("Tạo tài khoản"),
                    ),
                  ),

                  const SizedBox(height: 12),
                  Row(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      Text(
                        "Đã có tài khoản?",
                        style: theme.textTheme.bodyMedium,
                      ),
                      TextButton(
                        onPressed: () => Navigator.of(context).pop(),
                        child: const Text("Đăng nhập"),
                      )
                    ],
                  ),
                ],
              ),
            ),
          ),
        ),
      ),
    );
  }
}
