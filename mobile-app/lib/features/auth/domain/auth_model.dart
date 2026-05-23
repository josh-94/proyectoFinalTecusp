class TokenPair {
  final String accessToken;
  final String refreshToken;

  const TokenPair({required this.accessToken, required this.refreshToken});

  factory TokenPair.fromJson(Map<String, dynamic> json) => TokenPair(
        accessToken: json['access_token'] as String,
        refreshToken: json['refresh_token'] as String,
      );
}

class AuthUser {
  final String userId;
  final String username;
  final List<String> roles;

  const AuthUser({
    required this.userId,
    required this.username,
    required this.roles,
  });

  bool get isAdmin           => roles.contains('ADMIN');
  bool get isWarehouseOp     => roles.contains('WAREHOUSE_OPERATOR');
  bool get isHospitalStaff   => roles.contains('HOSPITAL_STAFF');
  bool get isInspector       => roles.contains('INSPECTOR');
  bool get isAuditor         => roles.contains('AUDITOR');

  bool canManageStock()      => isAdmin || isWarehouseOp;
  bool canCreateOrders()     => isAdmin || isHospitalStaff;
  bool canDispatchOrders()   => isAdmin || isWarehouseOp;
  bool canCreateReturns()    => isAdmin || isHospitalStaff;
  bool canInspectReturns()   => isAdmin || isInspector;
}
