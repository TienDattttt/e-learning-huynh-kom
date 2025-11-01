import 'package:flutter/material.dart';
import 'package:online_course/src/theme/app_color.dart';
import '../widgets/setting_item.dart';


class AccountBlock3 extends StatelessWidget {
  const AccountBlock3({super.key, this.onLogout});

  final VoidCallback? onLogout;

  @override
  Widget build(BuildContext context) {
    return SettingItem(
      title: "Đăng xuất",
      leadingIcon: "assets/icons/logout.svg",
      bgIconColor: AppColor.darker,
      onTap: onLogout ?? () {},
    );
  }


}

