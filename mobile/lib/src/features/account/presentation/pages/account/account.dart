import 'dart:convert';
import 'dart:math';
import 'package:flutter/material.dart';
import 'package:online_course/core/services/token_storage.dart';
import 'package:online_course/core/utils/jwt_util.dart';
import 'package:online_course/src/features/account/presentation/pages/account/widgets/account_appbar.dart';
import 'package:online_course/src/features/account/presentation/pages/account/widgets/account_profile_block.dart';
import 'package:online_course/src/features/account/presentation/pages/account/widgets/account_record_block.dart';
import 'package:online_course/src/features/account/presentation/pages/account/widgets/account_section1.dart';
import 'package:online_course/src/features/account/presentation/pages/account/widgets/account_section2.dart';
import 'package:online_course/src/features/account/presentation/pages/account/widgets/account_section3.dart';
import 'package:online_course/src/features/course/data/order_api.dart';
import 'package:online_course/src/theme/app_color.dart';

class AccountPage extends StatefulWidget {
  const AccountPage({Key? key}) : super(key: key);

  @override
  State<AccountPage> createState() => _AccountPageState();
}

class _AccountPageState extends State<AccountPage> {
  String displayName = "Loading...";
  String avatarUrl = "";
  int courseCount = 0;
  int totalHours = 0;

  /// ✅ Danh sách ảnh avatar local
  final List<String> localAvatars = [
    "assets/avatar/avatar1.jpg",
    "assets/avatar/avatar2.jpg",
    "assets/avatar/avatar3.jpg",
    "assets/avatar/avatar4.jpg",
    "assets/avatar/avatar5.jpg",
    "assets/avatar/avatar6.jpg",
    "assets/avatar/avatar7.jpg",
  ];

  @override
  void initState() {
    super.initState();
    _loadAccountData();
  }

  Future<void> _loadAccountData() async {
    final token = await TokenStorage.getToken();
    if (token == null) return;

    // ✅ Giải mã token (vì JwtUtil hiện chỉ có extractUserId)
    final payload = _decodeJwtPayload(token);
    print("🔍 JWT PAYLOAD: $payload");

    final fullName = payload['fullName'];
    final sub = payload['sub'];
    final display = (fullName != null && fullName.toString().trim().isNotEmpty)
        ? fullName.toString()
        : sub?.toString() ?? "User";

    // ✅ Random 1 ảnh avatar từ thư mục local
    final randomAvatar =
    localAvatars[Random().nextInt(localAvatars.length)];

    // ✅ Lấy số khóa học đã mua
    final api = OrderApi();
    final myCourses = await api.getMyCourses();

    // Giả định mỗi khóa học = 5 giờ
    final estimatedHours = myCourses.length * 5;

    setState(() {
      displayName = display;
      avatarUrl = randomAvatar;
      courseCount = myCourses.length;
      totalHours = estimatedHours;
    });
  }

  /// ✅ Helper decode payload từ JWT (base64url)
  Map<String, dynamic> _decodeJwtPayload(String token) {
    try {
      final parts = token.split('.');
      if (parts.length != 3) return {};
      final normalized = base64Url.normalize(parts[1]);
      final decoded = utf8.decode(base64Url.decode(normalized));
      return json.decode(decoded);
    } catch (e) {
      print("JWT decode error: $e");
      return {};
    }
  }

  Future<void> _logout(BuildContext context) async {
    await TokenStorage.clear(); // ✅ dùng clear() đúng với code gốc
    if (context.mounted) {
      Navigator.pushNamedAndRemoveUntil(context, '/login', (route) => false);
    }
  }

  @override
  Widget build(BuildContext context) {
    return CustomScrollView(
      slivers: <Widget>[
        const SliverAppBar(
          backgroundColor: AppColor.appBgColor,
          pinned: true,
          snap: true,
          floating: true,
          title: AccountAppBar(),
        ),
        SliverToBoxAdapter(child: _buildBody(context))
      ],
    );
  }

  Widget _buildBody(BuildContext context) {
    return SingleChildScrollView(
      padding: const EdgeInsets.symmetric(horizontal: 15),
      child: Column(
        children: [
          AccountProfileBlock(
            profile: {
              "image": avatarUrl,
              "name": displayName,
            },
          ),
          const SizedBox(height: 20),
          AccountRecordBlock(
            courseCount: courseCount,
            totalHours: totalHours,
          ),
          const SizedBox(height: 20),
          const AccountBlock1(),
          const SizedBox(height: 20),
          const AccountBlock2(),
          const SizedBox(height: 20),
          AccountBlock3(
            onLogout: () => _logout(context),
          ),
        ],
      ),
    );
  }
}
