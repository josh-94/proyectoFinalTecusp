import 'dart:convert';
import 'package:dio/dio.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:shared_preferences/shared_preferences.dart';
import '../../../core/constants/api_constants.dart';
import '../../../core/network/dio_client.dart';
import '../domain/auth_model.dart';

final authServiceProvider = Provider((ref) => AuthService(ref.read(dioClientProvider)));

class AuthService {
  final Dio _dio;
  AuthService(this._dio);

  Future<AuthUser> login(String username, String password) async {
    final resp = await _dio.post(ApiConstants.login,
        data: {'username': username, 'password': password});
    final pair = TokenPair.fromJson(resp.data as Map<String, dynamic>);

    final prefs = await SharedPreferences.getInstance();
    await prefs.setString('access_token', pair.accessToken);
    await prefs.setString('refresh_token', pair.refreshToken);

    return _decodeUser(pair.accessToken);
  }

  Future<void> logout() async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.clear();
  }

  Future<AuthUser?> tryRestoreSession() async {
    final prefs = await SharedPreferences.getInstance();
    final token = prefs.getString('access_token');
    if (token == null) return null;
    try {
      return _decodeUser(token);
    } catch (_) {
      await prefs.clear();
      return null;
    }
  }

  AuthUser _decodeUser(String jwt) {
    final parts = jwt.split('.');
    if (parts.length != 3) throw const FormatException('JWT inválido');
    final payload = utf8.decode(base64Url.decode(base64Url.normalize(parts[1])));
    final data = jsonDecode(payload) as Map<String, dynamic>;
    final roles = (data['roles'] as List<dynamic>? ?? []).cast<String>();
    return AuthUser(
      userId: data['sub'] as String,
      username: data['username'] as String? ?? data['sub'] as String,
      roles: roles,
    );
  }
}
