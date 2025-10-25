import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:online_course/core/services/injection_container.dart';
import 'package:online_course/src/features/auth/presentation/pages/login_page.dart';
import 'package:online_course/src/features/auth/presentation/pages/register_page.dart';
import 'package:online_course/src/features/course/pesentation/bloc/explore/course_bloc.dart';
import 'package:online_course/src/features/course/pesentation/bloc/my_course/my_course_bloc.dart';
import 'package:online_course/src/features/course/pesentation/bloc/detail/course_detail_bloc.dart';
import 'package:online_course/src/features/course/pesentation/bloc/favorite_course/favorite_course_bloc.dart';
import 'package:online_course/src/root_app.dart';
import 'src/theme/app_color.dart';
import 'package:cloudinary_flutter/cloudinary_context.dart';
import 'package:online_course/core/utils/cloudinary_helper.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await initLocator();
  CloudinaryContext.cloudinary = cld;
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MultiBlocProvider(
      providers: [
        BlocProvider(create: (_) => locator.get<CourseBloc>()),
        BlocProvider(create: (_) => locator.get<FavoriteCourseBloc>()),
        BlocProvider(create: (_) => locator.get<MyCourseBloc>()),
        BlocProvider(create: (_) => locator.get<CourseDetailBloc>()),
      ],
      child: MaterialApp(
        debugShowCheckedModeBanner: false,
        title: 'E-Learning App',
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
