import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import '../auth/presentation/auth_provider.dart';

class AppShell extends ConsumerWidget {
  final Widget child;
  const AppShell({super.key, required this.child});

  static const _navItems = [
    _NavItem('/dashboard',    'Dashboard',    Icons.dashboard_outlined,         Icons.dashboard),
    _NavItem('/inventario',   'Inventario',   Icons.inventory_2_outlined,       Icons.inventory_2),
    _NavItem('/pedidos',      'Pedidos',      Icons.list_alt_outlined,          Icons.list_alt),
    _NavItem('/devoluciones', 'Devoluciones', Icons.assignment_return_outlined,  Icons.assignment_return),
  ];

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    // Navigate to login whenever auth is lost (logout / token expired)
    ref.listen(authProvider, (_, next) {
      if (!next.isLoading && next.valueOrNull == null) {
        context.go('/login');
      }
    });

    final location = GoRouterState.of(context).matchedLocation;
    final user = ref.watch(authProvider).valueOrNull;
    final cs = Theme.of(context).colorScheme;

    int selectedIndex = _navItems.indexWhere((n) => location.startsWith(n.path));
    if (selectedIndex < 0) selectedIndex = 0;

    return Scaffold(
      body: Row(
        children: [
          // ── Sidebar ─────────────────────────────────────────────
          Container(
            width: 220,
            color: cs.surfaceContainer,
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                // Header
                Container(
                  color: cs.primary,
                  padding: const EdgeInsets.fromLTRB(20, 32, 20, 20),
                  width: double.infinity,
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Icon(Icons.local_hospital_rounded,
                          color: cs.onPrimary, size: 32),
                      const SizedBox(height: 8),
                      Text('MedInventory',
                          style: TextStyle(
                              color: cs.onPrimary,
                              fontSize: 18,
                              fontWeight: FontWeight.bold)),
                    ],
                  ),
                ),
                const SizedBox(height: 8),
                // Nav items — each tile handles its own context.go()
                Expanded(
                  child: ListView(
                    padding: const EdgeInsets.symmetric(vertical: 8),
                    children: _navItems.map((item) {
                      final selected =
                          _navItems.indexOf(item) == selectedIndex;
                      return _SidebarTile(item: item, selected: selected);
                    }).toList(),
                  ),
                ),
                // User info + logout
                const Divider(height: 1),
                Padding(
                  padding: const EdgeInsets.all(12),
                  child: Row(
                    children: [
                      CircleAvatar(
                        radius: 16,
                        backgroundColor: cs.primaryContainer,
                        child:
                            Icon(Icons.person, size: 18, color: cs.primary),
                      ),
                      const SizedBox(width: 10),
                      Expanded(
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Text(user?.username ?? '',
                                style: const TextStyle(
                                    fontSize: 13,
                                    fontWeight: FontWeight.w600),
                                overflow: TextOverflow.ellipsis),
                            Text(
                              user?.roles.join(', ') ?? '',
                              style: TextStyle(
                                  fontSize: 11,
                                  color: cs.onSurfaceVariant),
                              overflow: TextOverflow.ellipsis,
                            ),
                          ],
                        ),
                      ),
                      IconButton(
                        icon: const Icon(Icons.logout, size: 18),
                        tooltip: 'Cerrar sesión',
                        onPressed: () =>
                            ref.read(authProvider.notifier).logout(),
                      ),
                    ],
                  ),
                ),
              ],
            ),
          ),
          // ── Content ─────────────────────────────────────────────
          Expanded(child: child),
        ],
      ),
    );
  }
}

class _NavItem {
  final String path;
  final String label;
  final IconData icon;
  final IconData selectedIcon;
  const _NavItem(this.path, this.label, this.icon, this.selectedIcon);
}

// Each tile uses its own BuildContext for context.go() — avoids stale-context
// captures from AppShell's build closure.
class _SidebarTile extends StatelessWidget {
  final _NavItem item;
  final bool selected;
  const _SidebarTile({required this.item, required this.selected});

  @override
  Widget build(BuildContext context) {
    final cs = Theme.of(context).colorScheme;
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 2),
      child: ListTile(
        dense: true,
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
        selected: selected,
        selectedTileColor: cs.primaryContainer,
        leading: Icon(
          selected ? item.selectedIcon : item.icon,
          color: selected ? cs.primary : cs.onSurfaceVariant,
          size: 22,
        ),
        title: Text(
          item.label,
          style: TextStyle(
            fontSize: 14,
            fontWeight: selected ? FontWeight.w600 : FontWeight.normal,
            color: selected ? cs.primary : cs.onSurface,
          ),
        ),
        onTap: () => context.go(item.path),
      ),
    );
  }
}
