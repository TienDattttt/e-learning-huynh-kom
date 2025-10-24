import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:online_course/core/services/injection_container.dart';
import 'package:online_course/src/features/auth/presentation/pages/login_page.dart';
import 'package:online_course/src/features/auth/presentation/pages/register_page.dart';
import 'package:online_course/src/features/course/pesentation/bloc/explore/course_bloc.dart';
import 'package:online_course/src/features/course/pesentation/bloc/favorite_course/favorite_course_bloc.dart';
import 'package:online_course/src/features/course/pesentation/bloc/feature/feature_course_bloc.dart';
import 'package:online_course/src/features/course/pesentation/bloc/recommend/recommend_course_bloc.dart';
import 'package:online_course/src/root_app.dart';
import 'src/theme/app_color.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await initLocator();
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MultiBlocProvider(
      providers: [
        BlocProvider(create: (_) => locator.get<CourseBloc>()),
        BlocProvider(create: (_) => locator.get<FeatureCourseBloc>()),
        BlocProvider(create: (_) => locator.get<RecommendCourseBloc>()),
        BlocProvider(create: (_) => locator.get<FavoriteCourseBloc>()),
      ],
      child: MaterialApp(
        debugShowCheckedModeBanner: false,
        title: 'Online Course App',
        theme: ThemeData(
          primaryColor: AppColor.primary,
        ),
        initialRoute: '/login',
        routes: {
          '/login': (_) => const LoginPage(),
          '/register': (_) => const RegisterPage(),
          '/root': (_) => const RootApp(),
        },
      ),
    );
  }
}
