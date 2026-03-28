import { useEffect, useState } from 'react';
import { getSales, createSale, updateSale, deleteSale, getProductsBySaleId } from '../services/saleService';
import { getCustomers } from '../services/customerService';
import { getProducts } from '../services/productService';

export default function Sales() {
  const [sales, setSales] = useState([]);
  const [customers, setCustomers] = useState([]);
  const [products, setProducts] = useState([]);
  const [error, setError] = useState(null);
  const [showForm, setShowForm] = useState(false);
  const [editingId, setEditingId] = useState(null);
  const [expandedSaleId, setExpandedSaleId] = useState(null);
  const [saleProducts, setSaleProducts] = useState({});

  const emptyForm = {
    saleDate: new Date().toISOString().split('T')[0],
    customerId: '',
    items: [{ productId: '', quantity: 1 }],
  };
  const [form, setForm] = useState(emptyForm);

  useEffect(() => {
    fetchAll();
  }, []);

  const fetchAll = async () => {
    try {
      const [salesRes, customersRes, productsRes] = await Promise.all([
        getSales(),
        getCustomers(),
        getProducts(),
      ]);
      setSales(salesRes.data);
      setCustomers(customersRes.data);
      setProducts(productsRes.data);
    } catch (err) {
      setError('Error al cargar datos');
    }
  };

  const handleToggleProducts = async (saleId) => {
    if (expandedSaleId === saleId) {
      setExpandedSaleId(null);
      return;
    }
    setExpandedSaleId(saleId);
    if (!saleProducts[saleId]) {
      try {
        const res = await getProductsBySaleId(saleId);
        setSaleProducts(prev => ({ ...prev, [saleId]: res.data }));
      } catch (err) {
        setSaleProducts(prev => ({ ...prev, [saleId]: [] }));
      }
    }
  };

  const handleAddItem = () => {
    setForm({ ...form, items: [...form.items, { productId: '', quantity: 1 }] });
  };

  const handleRemoveItem = (index) => {
    const updated = form.items.filter((_, i) => i !== index);
    setForm({ ...form, items: updated });
  };

  const handleItemChange = (index, field, value) => {
    const updated = form.items.map((item, i) =>
      i === index ? { ...item, [field]: value } : item
    );
    setForm({ ...form, items: updated });
  };

  const handleSubmit = async () => {
    try {
      const payload = {
        saleDate: form.saleDate,
        customerId: Number(form.customerId),
        items: form.items.map(item => ({
          productId: Number(item.productId),
          quantity: Number(item.quantity),
        })),
      };
      if (editingId) {
        await updateSale(editingId, payload);
      } else {
        await createSale(payload);
      }
      setForm(emptyForm);
      setEditingId(null);
      setShowForm(false);
      fetchAll();
    } catch (err) {
      setError('Error al guardar venta');
    }
  };

  const handleEdit = (sale) => {
    setForm({
      saleDate: new Date().toISOString().split('T')[0],
      customerId: '',
      items: [{ productId: '', quantity: 1 }],
    });
    setEditingId(sale.id);
    setShowForm(true);
  };

  const handleDelete = async (id) => {
    try {
      await deleteSale(id);
      if (expandedSaleId === id) setExpandedSaleId(null);
      fetchAll();
    } catch (err) {
      setError('Error al eliminar venta');
    }
  };

  const handleCancel = () => {
    setForm(emptyForm);
    setEditingId(null);
    setShowForm(false);
  };

  return (
    <div>
      {/* Header */}
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-xl font-medium text-gray-900">Ventas</h1>
          <p className="text-sm text-gray-400 mt-0.5">Registro de ventas del bazar</p>
        </div>
        <button
          onClick={() => setShowForm(true)}
          className="bg-blue-500 hover:bg-blue-600 text-white text-sm font-medium px-4 py-2 rounded-lg transition-colors"
        >
          + Nueva venta
        </button>
      </div>

      {/* Error */}
      {error && (
        <div className="mb-4 px-4 py-3 bg-red-50 border border-red-200 text-red-700 text-sm rounded-lg">
          {error}
        </div>
      )}

      {/* Form */}
      {showForm && (
        <div className="mb-6 bg-white border border-gray-200 rounded-xl p-5">
          <h2 className="text-sm font-medium text-gray-700 mb-4">
            {editingId ? 'Editar venta' : 'Nueva venta'}
          </h2>
          <div className="grid grid-cols-2 gap-3 mb-4">
            <div>
              <label className="block text-xs text-gray-400 mb-1">Fecha</label>
              <input
                type="date"
                className="w-full border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                value={form.saleDate}
                onChange={e => setForm({ ...form, saleDate: e.target.value })}
              />
            </div>
            <div>
              <label className="block text-xs text-gray-400 mb-1">Cliente</label>
              <select
                className="w-full border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 bg-white"
                value={form.customerId}
                onChange={e => setForm({ ...form, customerId: e.target.value })}
              >
                <option value="">Seleccionar cliente</option>
                {customers.map(c => (
                  <option key={c.id} value={c.id}>
                    {c.firstName} {c.lastName}
                  </option>
                ))}
              </select>
            </div>
          </div>

          <div className="mb-4">
            <div className="flex items-center justify-between mb-2">
              <label className="text-xs text-gray-400">Productos</label>
              <button
                onClick={handleAddItem}
                className="text-xs text-blue-600 hover:text-blue-800 font-medium"
              >
                + Agregar producto
              </button>
            </div>
            <div className="space-y-2">
              {form.items.map((item, index) => (
                <div key={index} className="flex gap-2 items-center">
                  <select
                    className="flex-1 border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 bg-white"
                    value={item.productId}
                    onChange={e => handleItemChange(index, 'productId', e.target.value)}
                  >
                    <option value="">Seleccionar producto</option>
                    {products.map(p => (
                      <option key={p.id} value={p.id}>
                        {p.name} — {p.brand} (stock: {p.stock})
                      </option>
                    ))}
                  </select>
                  <input
                    type="number"
                    min="1"
                    className="w-24 border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                    placeholder="Cant."
                    value={item.quantity}
                    onChange={e => handleItemChange(index, 'quantity', e.target.value)}
                  />
                  {form.items.length > 1 && (
                    <button
                      onClick={() => handleRemoveItem(index)}
                      className="text-xs text-red-500 hover:text-red-700 font-medium px-2"
                    >
                      ✕
                    </button>
                  )}
                </div>
              ))}
            </div>
          </div>

          <div className="flex gap-2">
            <button
              onClick={handleSubmit}
              className="bg-blue-500 hover:bg-blue-600 text-white text-sm font-medium px-4 py-2 rounded-lg transition-colors"
            >
              {editingId ? 'Actualizar' : 'Crear'}
            </button>
            <button
              onClick={handleCancel}
              className="border border-gray-200 hover:bg-gray-50 text-gray-600 text-sm px-4 py-2 rounded-lg transition-colors"
            >
              Cancelar
            </button>
          </div>
        </div>
      )}

      {/* Tabla */}
      <div className="bg-white border border-gray-200 rounded-xl overflow-hidden">
        <table className="w-full text-sm">
          <thead>
            <tr className="bg-gray-50 border-b border-gray-200">
              <th className="text-left px-4 py-3 text-xs text-gray-400 font-medium">ID</th>
              <th className="text-left px-4 py-3 text-xs text-gray-400 font-medium">Cliente</th>
              <th className="text-left px-4 py-3 text-xs text-gray-400 font-medium">Total</th>
              <th className="text-left px-4 py-3 text-xs text-gray-400 font-medium">Productos</th>
              <th className="text-left px-4 py-3 text-xs text-gray-400 font-medium">Acciones</th>
            </tr>
          </thead>
          <tbody>
            {sales.map((s, i) => (
              <>
                <tr key={s.id} className={i % 2 === 0 ? 'bg-white' : 'bg-gray-50'}>
                  <td className="px-4 py-3 text-gray-400">{s.id}</td>
                  <td className="px-4 py-3 text-gray-900 font-medium">
                    {s.nombreCliente} {s.apellidoCliente}
                  </td>
                  <td className="px-4 py-3 text-gray-600">
                    ${s.total?.toLocaleString()}
                  </td>
                  <td className="px-4 py-3">
                    <button
                      onClick={() => handleToggleProducts(s.id)}
                      className={`px-2 py-0.5 rounded-md text-xs font-medium transition-colors cursor-pointer ${
                        expandedSaleId === s.id
                          ? 'bg-blue-100 text-blue-800'
                          : 'bg-blue-50 text-blue-700 hover:bg-blue-100'
                      }`}
                    >
                      {s.cantidadProductos} productos {expandedSaleId === s.id ? '▲' : '▼'}
                    </button>
                  </td>
                  <td className="px-4 py-3">
                    <div className="flex gap-3">
                      <button
                        onClick={() => handleEdit(s)}
                        className="text-xs text-gray-500 hover:text-gray-800 border border-gray-200 hover:bg-gray-50 px-2 py-1 rounded transition-colors"
                      >
                        Editar
                      </button>
                      <button
                        onClick={() => handleDelete(s.id)}
                        className="text-xs text-red-500 hover:text-red-700 font-medium"
                      >
                        Eliminar
                      </button>
                    </div>
                  </td>
                </tr>

                {/* Accordion */}
                {expandedSaleId === s.id && (
                  <tr key={`accordion-${s.id}`} className="bg-blue-50">
                    <td colSpan={5} className="px-6 py-3">
                      {!saleProducts[s.id] ? (
                        <p className="text-xs text-gray-400">Cargando...</p>
                      ) : saleProducts[s.id].length === 0 ? (
                        <p className="text-xs text-gray-400">Sin productos</p>
                      ) : (
                        <table className="w-full text-xs">
                          <thead>
                            <tr className="text-gray-400">
                              <th className="text-left pb-2 font-medium">Producto</th>
                              <th className="text-left pb-2 font-medium">Marca</th>
                              <th className="text-left pb-2 font-medium">Cantidad</th>
                              <th className="text-left pb-2 font-medium">Precio unitario</th>
                              <th className="text-left pb-2 font-medium">Subtotal</th>
                            </tr>
                          </thead>
                          <tbody>
                            {saleProducts[s.id].map((p) => (
                              <tr key={p.productId} className="border-t border-blue-100">
                                <td className="py-1.5 text-gray-800 font-medium">{p.name}</td>
                                <td className="py-1.5 text-gray-600">{p.brand}</td>
                                <td className="py-1.5 text-gray-600">{p.quantity}</td>
                                <td className="py-1.5 text-gray-600">${p.unitPrice?.toLocaleString()}</td>
                                <td className="py-1.5 text-gray-800 font-medium">${(p.unitPrice * p.quantity)?.toLocaleString()}</td>
                              </tr>
                            ))}
                          </tbody>
                        </table>
                      )}
                    </td>
                  </tr>
                )}
              </>
            ))}
            {sales.length === 0 && (
              <tr>
                <td colSpan={5} className="px-4 py-8 text-center text-gray-400 text-sm">
                  No hay ventas registradas
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
