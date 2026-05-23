class LineaDevolucionModel {
  final String loteId;
  final int cantidadDevuelta;
  final String motivoDevolucion;

  const LineaDevolucionModel({
    required this.loteId,
    required this.cantidadDevuelta,
    required this.motivoDevolucion,
  });

  factory LineaDevolucionModel.fromJson(Map<String, dynamic> j) =>
      LineaDevolucionModel(
        loteId: j['loteId'] as String,
        cantidadDevuelta: j['cantidadDevuelta'] as int,
        motivoDevolucion: j['motivoDevolucion'] as String? ?? '',
      );

  Map<String, dynamic> toJson() => {
        'loteId': loteId,
        'cantidadDevuelta': cantidadDevuelta,
        'motivoDevolucion': motivoDevolucion,
      };
}

class DevolucionModel {
  final String id;
  final String numeroDevolucion;
  final String pedidoId;
  final String solicitadoPor;
  final String estado;
  final String? observaciones;
  final String? motivoRechazo;
  final List<LineaDevolucionModel> lineas;
  final String creadoEn;

  const DevolucionModel({
    required this.id,
    required this.numeroDevolucion,
    required this.pedidoId,
    required this.solicitadoPor,
    required this.estado,
    this.observaciones,
    this.motivoRechazo,
    required this.lineas,
    required this.creadoEn,
  });

  factory DevolucionModel.fromJson(Map<String, dynamic> j) => DevolucionModel(
        id: j['id'] as String,
        numeroDevolucion: j['numeroDevolucion'] as String,
        pedidoId: j['pedidoId'] as String,
        solicitadoPor: j['solicitadoPor'] as String,
        estado: j['estado'] as String,
        observaciones: j['observaciones'] as String?,
        motivoRechazo: j['motivoRechazo'] as String?,
        lineas: (j['lineas'] as List<dynamic>? ?? [])
            .map((e) =>
                LineaDevolucionModel.fromJson(e as Map<String, dynamic>))
            .toList(),
        creadoEn: j['creadoEn'] as String? ?? '',
      );
}
