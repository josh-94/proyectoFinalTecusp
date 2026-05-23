import 'package:dio/dio.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../../core/constants/api_constants.dart';
import '../../../core/network/dio_client.dart';
import '../domain/lote_model.dart';

final inventarioServiceProvider =
    Provider((ref) => InventarioService(ref.read(dioClientProvider)));

class InventarioService {
  final Dio _dio;
  InventarioService(this._dio);

  Future<List<LoteModel>> getLotes() async {
    final resp = await _dio.get('${ApiConstants.lotes}');
    final list = resp.data as List<dynamic>;
    return list.map((e) => LoteModel.fromJson(e as Map<String, dynamic>)).toList();
  }

  Future<LoteModel> getLote(String loteId) async {
    final resp = await _dio.get('${ApiConstants.lotes}/$loteId');
    return LoteModel.fromJson(resp.data as Map<String, dynamic>);
  }

  Future<void> registrarMovimiento({
    required String loteId,
    required String tipo,
    required int cantidad,
    String? referenciaExterna,
  }) async {
    await _dio.post(ApiConstants.movimientos, data: {
      'loteId': loteId,
      'tipo': tipo,
      'cantidad': cantidad,
      if (referenciaExterna != null) 'referenciaExterna': referenciaExterna,
    });
  }
}
