import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'core/router/app_router.dart';
import 'core/theme/app_theme.dart';

void main() {
  runApp(const ProviderScope(child: MedInventoryApp()));
}

class MedInventoryApp extends ConsumerStatefulWidget {
  const MedInventoryApp({super.key});

  @override
  ConsumerState<MedInventoryApp> createState() => _MedInventoryAppState();
}

class _MedInventoryAppState extends ConsumerState<MedInventoryApp> {
  late final GoRouter _router;

  @override
  void initState() {
    super.initState();
    _router = ref.read(appRouterProvider);
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp.router(
      title: 'MedInventory',
      theme: AppTheme.light(),
      debugShowCheckedModeBanner: false,
      routerConfig: _router,
    );
  }
}
