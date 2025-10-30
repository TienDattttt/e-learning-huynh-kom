import 'package:flutter/material.dart';
import 'package:online_course/src/theme/app_color.dart';

class AccountBlock3 extends StatelessWidget {
  const AccountBlock3({super.key, this.onLogout});

  final VoidCallback? onLogout;

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        ListTile(
          leading: const Icon(Icons.logout_rounded, color: AppColor.red),
          title: const Text(
            "Log out",
            style: TextStyle(color: AppColor.red, fontWeight: FontWeight.w600),
          ),
          onTap: onLogout ?? () {},
        ),
      ],
    );
  }
}
