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
                    "OnlineCourse",
                    textAlign: TextAlign.center,
                    style: theme.textTheme.headlineSmall?.copyWith(
                      fontWeight: FontWeight.w700,
                    ),
                  ),
                  const SizedBox(height: 8),
                  Text(
                    "Create your account",
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
                            labelText: "Full name",
                            prefixIcon: Icon(Icons.person_outline),
                          ),
                          validator: (v) {
                            if (v == null || v.trim().isEmpty) {
                              return "Please enter your full name";
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
                              return "Please enter your email.";
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
                            if (v.length < 8) {
                              return "Password must be at least 8 characters";
                            }
                            if (!RegExp(r'[a-z]').hasMatch(v)) {
                              return "Password must contain at least 1 lowercase letter";
                            }
                            if (!RegExp(r'[A-Z]').hasMatch(v)) {
                              return "Password must contain at least 1 uppercase letter";
                            }
                            if (!RegExp(r'\d').hasMatch(v)) {
                              return "Password must contain at least 1 number";
                            }
                            if (!RegExp(r'[\W_]').hasMatch(v)) {
                              return "Password must contain at least 1 special character";
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
                          : const Text("Create account"),
                    ),
                  ),

                  const SizedBox(height: 12),
                  Row(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      Text(
                        "Already have an account?",
                        style: theme.textTheme.bodyMedium,
                      ),
                      TextButton(
                        onPressed: () => Navigator.of(context).pop(),
                        child: const Text("Login"),
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
