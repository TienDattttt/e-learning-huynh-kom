import 'package:flutter/material.dart';
import 'package:online_course/src/theme/app_color.dart';
import 'package:online_course/src/widgets/custom_image.dart';

class AccountProfileBlock extends StatelessWidget {
  const AccountProfileBlock({required this.profile, super.key});
  final Map profile;

  bool _isNetworkImage(String path) {
    return path.startsWith('http') || path.startsWith('https');
  }

  @override
  Widget build(BuildContext context) {
    final imagePath = profile["image"] ?? "";
    final name = profile["name"] ?? "User";

    return Column(
      children: [
        // Avatar Container có viền gradient
        Container(
          padding: const EdgeInsets.all(3),
          decoration: BoxDecoration(
            shape: BoxShape.circle,
            gradient: const LinearGradient(
              colors: [AppColor.primary, AppColor.secondary],
              begin: Alignment.topLeft,
              end: Alignment.bottomRight,
            ),
            boxShadow: [
              BoxShadow(
                color: Colors.black.withOpacity(0.15),
                blurRadius: 6,
                offset: const Offset(0, 3),
              )
            ],
          ),
          child: ClipOval(
            child: _isNetworkImage(imagePath)
                ? CustomImage(
              imagePath,
              width: 80,
              height: 80,
              radius: 40,
              fit: BoxFit.cover,
            )
                : Image.asset(
              imagePath,
              width: 80,
              height: 80,
              fit: BoxFit.cover,
            ),
          ),
        ),
        const SizedBox(height: 10),
        Text(
          name,
          style: const TextStyle(
            fontSize: 18,
            fontWeight: FontWeight.w600,
            color: AppColor.textColor,
          ),
          textAlign: TextAlign.center,
        ),
      ],
    );
  }
}
