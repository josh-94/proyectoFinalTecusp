class LoteModel {
  final String id;
  final String numeroLote;
  final String productoId;
  final String nombreProducto;
  final int cantidadDisponible;
  final int stockMinimo;
  final String fechaVencimiento;
  final bool vencido;

  const LoteModel({
    required this.id,
    required this.numeroLote,
    required this.productoId,
    required this.nombreProducto,
    required this.cantidadDisponible,
    required this.stockMinimo,
    required this.fechaVencimiento,
    required this.vencido,
  });

  bool get stockBajo => cantidadDisponible <= stockMinimo;

  factory LoteModel.fromJson(Map<String, dynamic> j) => LoteModel(
        id: j['id'] as String,
        numeroLote: j['numeroLote'] as String? ?? '',
        productoId: j['productoId'] as String? ?? '',
        nombreProducto: j['nombreProducto'] as String? ?? j['productoId'] as String? ?? '',
        cantidadDisponible: j['cantidadDisponible'] as int? ?? 0,
        stockMinimo: j['stockMinimo'] as int? ?? 0,
        fechaVencimiento: j['fechaVencimiento'] as String? ?? '',
        vencido: j['vencido'] as bool? ?? false,
      );
}
