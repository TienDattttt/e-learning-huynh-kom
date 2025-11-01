import 'package:flutter/material.dart';
import 'package:online_course/src/features/account/presentation/pages/account/widgets/setting_box.dart';

class AccountRecordBlock extends StatelessWidget {
  const AccountRecordBlock({
    super.key,
    required this.courseCount,
    required this.totalHours,
  });

  final int courseCount;
  final int totalHours;

  @override
  Widget build(BuildContext context) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        Expanded(
          child: SettingBox(
            title: "$courseCount khóa học",
            icon: "assets/icons/work.svg",
          ),
        ),
        const SizedBox(width: 10),
        Expanded(
          child: SettingBox(
            title: "$totalHours giờ",
            icon: "assets/icons/time.svg",
          ),
        ),
        const SizedBox(width: 10),
        const Expanded(
          child: SettingBox(
            title: "4.8",
            icon: "assets/icons/star.svg",
          ),
        ),
      ],
    );
  }
}
