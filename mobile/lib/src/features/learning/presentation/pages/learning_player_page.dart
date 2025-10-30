import 'package:flutter/material.dart';
import 'package:chewie/chewie.dart';
import 'package:video_player/video_player.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:online_course/src/features/learning/presentation/bloc/learning_bloc.dart';
import 'package:online_course/src/features/learning/presentation/bloc/learning_event.dart';

class LearningPlayerPage extends StatefulWidget {
  final String videoUrl;
  final int courseId;
  final int lessonId;

  const LearningPlayerPage({
    Key? key,
    required this.videoUrl,
    required this.courseId,
    required this.lessonId,
  }) : super(key: key);

  @override
  State<LearningPlayerPage> createState() => _LearningPlayerPageState();
}

class _LearningPlayerPageState extends State<LearningPlayerPage> {
  late VideoPlayerController _videoController;
  ChewieController? _chewieController;

  @override
  void initState() {
    super.initState();
    _videoController = VideoPlayerController.networkUrl(Uri.parse(widget.videoUrl))
      ..initialize().then((_) {
        setState(() {});
        _videoController.play();
      });

    _chewieController = ChewieController(
      videoPlayerController: _videoController,
      autoPlay: true,
      looping: false,
      allowFullScreen: true,
      allowMuting: true,
    );
  }

  /// Khi người dùng thoát khỏi trang học
  @override
  void dispose() {
    try {
      if (_videoController.value.isInitialized) {
        final duration = _videoController.value.duration.inSeconds;
        final position = _videoController.value.position.inSeconds;
        int percent = 0;
        if (duration > 0) {
          percent = ((position / duration) * 100).round();
        }

        // Nếu xem > 95% thì đánh dấu completed
        final completed = percent >= 95;

        // Gọi Bloc lưu tiến độ
        context.read<LearningBloc>().add(
          UpdateLessonProgress(
            courseId: widget.courseId,
            lessonId: widget.lessonId,
            progressPercent: percent,
            completed: completed,
          ),
        );

        debugPrint("📊 Saved lesson progress: $percent% (completed=$completed)");
      }

      _videoController.dispose();
      _chewieController?.dispose();
    } catch (e) {
      debugPrint("⚠️ Dispose error: $e");
    }
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.black,
      appBar: AppBar(
        title: const Text('Learning Player'),
        backgroundColor: Colors.black,
      ),
      body: Center(
        child: _videoController.value.isInitialized
            ? Chewie(controller: _chewieController!)
            : const CircularProgressIndicator(color: Colors.white),
      ),
    );
  }
}
