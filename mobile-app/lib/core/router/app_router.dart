import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import '../../features/auth/presentation/auth_provider.dart';
import '../../features/auth/presentation/login_screen.dart';
import '../../features/shell/app_shell.dart';
import '../../features/dashboard/presentation/dashboard_screen.dart';
import '../../features/inventario/presentation/inventario_screen.dart';
import '../../features/pedidos/presentation/pedidos_screen.dart';
import '../../features/devoluciones/presentation/devoluciones_screen.dart';

final appRouterProvider = Provider<GoRouter>((ref) {
  return GoRouter(
    initialLocation: '/dashboard',
    redirect: (context, state) {
      final authState = ref.read(authProvider);
      if (authState.isLoading) return null;

      final isLoggedIn = authState.valueOrNull != null;
      final isOnLogin = state.matchedLocation == '/login';

      if (!isLoggedIn && !isOnLogin) return '/login';
      if (isLoggedIn && isOnLogin) return '/dashboard';
      return null;
    },
    routes: [
      GoRoute(path: '/login', builder: (_, __) => const LoginScreen()),
      // No ShellRoute — AppShell is inlined in each route to avoid the nested
      // navigator that ShellRoute creates, which causes navigator.dart assertions.
      GoRoute(
        path: '/dashboard',
        builder: (_, __) => const AppShell(child: DashboardScreen()),
      ),
      GoRoute(
        path: '/inventario',
        builder: (_, __) => const AppShell(child: InventarioScreen()),
      ),
      GoRoute(
        path: '/pedidos',
        builder: (_, __) => const AppShell(child: PedidosScreen()),
      ),
      GoRoute(
        path: '/devoluciones',
        builder: (_, __) => const AppShell(child: DevolucionesScreen()),
      ),
    ],
  );
});
