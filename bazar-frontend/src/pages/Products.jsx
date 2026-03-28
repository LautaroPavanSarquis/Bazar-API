import { useEffect, useState } from 'react';
import { getProducts, createProduct, updateProduct, deleteProduct, getLowStock } from '../services/productService';

export default function Products() {
  const [products, setProducts] = useState([]);
  const [lowStockCount, setLowStockCount] = useState(0);
  const [form, setForm] = useState({ name: '', brand: '', price: '', stock: '' });
  const [editingId, setEditingId] = useState(null);
  const [error, setError] = useState(null);
  const [showForm, setShowForm] = useState(false);

  useEffect(() => {
    fetchProducts();
    fetchLowStock();
  }, []);

  const fetchProducts = async () => {
    try {
      const res = await getProducts();
      setProducts(res.data);
    } catch (err) {
      setError('Error al cargar productos');
    }
  };

  const fetchLowStock = async () => {
    try {
      const res = await getLowStock();
      setLowStockCount(res.data.length);
    } catch (err) {
      setLowStockCount(0);
    }
  };

  const totalStock = products.reduce((acc, p) => acc + (p.price * p.stock), 0);

  const handleSubmit = async () => {
    try {
      if (editingId) {
        await updateProduct(editingId, form);
      } else {
        await createProduct(form);
      }
      setForm({ name: '', brand: '', price: '', stock: '' });
      setEditingId(null);
      setShowForm(false);
      fetchProducts();
      fetchLowStock();
    } catch (err) {
      setError('Error al guardar producto');
    }
  };

  const handleEdit = (product) => {
    setForm({ name: product.name, brand: product.brand, price: product.price, stock: product.stock });
    setEditingId(product.id);
    setShowForm(true);
  };

  const handleDelete = async (id) => {
    try {
      await deleteProduct(id);
      fetchProducts();
      fetchLowStock();
    } catch (err) {
      setError('Error al eliminar producto');
    }
  };

  const handleCancel = () => {
    setEditingId(null);
    setForm({ name: '', brand: '', price: '', stock: '' });
    setShowForm(false);
  };

  const formatCurrency = (value) => {
    if (value >= 1000) return `$${Math.round(value / 1000)}k`;
    return `$${value.toLocaleString()}`;
  };

  return (
    <div>
      {/* Header */}
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-xl font-medium text-gray-900">Productos</h1>
          <p className="text-sm text-gray-400 mt-0.5">Gestión de stock del bazar</p>
        </div>
        <button
          onClick={() => setShowForm(true)}
          className="bg-blue-500 hover:bg-blue-600 text-white text-sm font-medium px-4 py-2 rounded-lg transition-colors"
        >
          + Nuevo
        </button>
      </div>

      {/* Error */}
      {error && (
        <div className="mb-4 px-4 py-3 bg-red-50 border border-red-200 text-red-700 text-sm rounded-lg">
          {error}
        </div>
      )}

      {/* Stat cards */}
      <div className="grid grid-cols-3 gap-4 mb-6">
        <div className="bg-gray-50 rounded-xl p-4">
          <p className="text-xs text-gray-400 mb-1">Total productos</p>
          <p className="text-2xl font-medium text-gray-900">{products.length}</p>
        </div>
        <div className="bg-gray-50 rounded-xl p-4">
          <p className="text-xs text-gray-400 mb-1">Stock bajo</p>
          <p className="text-2xl font-medium text-red-500">{lowStockCount}</p>
        </div>
        <div className="bg-gray-50 rounded-xl p-4">
          <p className="text-xs text-gray-400 mb-1">Valor total stock</p>
          <p className="text-2xl font-medium text-gray-900">{formatCurrency(totalStock)}</p>
        </div>
      </div>

      {/* Form */}
      {showForm && (
        <div className="mb-6 bg-white border border-gray-200 rounded-xl p-5">
          <h2 className="text-sm font-medium text-gray-700 mb-4">
            {editingId ? 'Editar producto' : 'Nuevo producto'}
          </h2>
          <div className="grid grid-cols-2 gap-3 mb-4">
            <input
              className="border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              placeholder="Nombre"
              value={form.name}
              onChange={e => setForm({ ...form, name: e.target.value })}
            />
            <input
              className="border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              placeholder="Marca"
              value={form.brand}
              onChange={e => setForm({ ...form, brand: e.target.value })}
            />
            <input
              className="border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              placeholder="Precio"
              type="number"
              value={form.price}
              onChange={e => setForm({ ...form, price: e.target.value })}
            />
            <input
              className="border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              placeholder="Stock"
              type="number"
              value={form.stock}
              onChange={e => setForm({ ...form, stock: e.target.value })}
            />
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
              <th className="text-left px-4 py-3 text-xs text-gray-400 font-medium">Nombre</th>
              <th className="text-left px-4 py-3 text-xs text-gray-400 font-medium">Marca</th>
              <th className="text-left px-4 py-3 text-xs text-gray-400 font-medium">Precio</th>
              <th className="text-left px-4 py-3 text-xs text-gray-400 font-medium">Stock</th>
              <th className="text-left px-4 py-3 text-xs text-gray-400 font-medium">Acciones</th>
            </tr>
          </thead>
          <tbody>
            {products.map((p, i) => (
              <tr key={p.id} className={i % 2 === 0 ? 'bg-white' : 'bg-gray-50'}>
                <td className="px-4 py-3 text-gray-400">{p.id}</td>
                <td className="px-4 py-3 text-gray-900 font-medium">{p.name}</td>
                <td className="px-4 py-3 text-gray-600">{p.brand}</td>
                <td className="px-4 py-3 text-gray-600">${p.price?.toLocaleString()}</td>
                <td className="px-4 py-3">
                  <span className={`px-2 py-0.5 rounded-md text-xs font-medium ${
                    p.stock <= 5 ? 'bg-red-50 text-red-600' : 'bg-green-50 text-green-700'
                  }`}>
                    {p.stock}
                  </span>
                </td>
                <td className="px-4 py-3">
                  <div className="flex gap-3">
                    <button
                      onClick={() => handleEdit(p)}
                      className="text-xs text-gray-500 hover:text-gray-800 border border-gray-200 hover:bg-gray-50 px-2 py-1 rounded transition-colors"
                    >
                      Editar
                    </button>
                    <button
                      onClick={() => handleDelete(p.id)}
                      className="text-xs text-red-500 hover:text-red-700 font-medium"
                    >
                      Eliminar
                    </button>
                  </div>
                </td>
              </tr>
            ))}
            {products.length === 0 && (
              <tr>
                <td colSpan={6} className="px-4 py-8 text-center text-gray-400 text-sm">
                  No hay productos cargados
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
