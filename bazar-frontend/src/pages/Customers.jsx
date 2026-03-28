import { useEffect, useState } from 'react';
import { getCustomers, createCustomer, updateCustomer, deleteCustomer } from '../services/customerService';

export default function Customers() {
  const [customers, setCustomers] = useState([]);
  const [form, setForm] = useState({ firstName: '', lastName: '', dni: '', email: '' });
  const [editingId, setEditingId] = useState(null);
  const [error, setError] = useState(null);
  const [showForm, setShowForm] = useState(false);

  useEffect(() => {
    fetchCustomers();
  }, []);

  const fetchCustomers = async () => {
    try {
      const res = await getCustomers();
      setCustomers(res.data);
    } catch (err) {
      setError('Error al cargar clientes');
    }
  };

  const handleSubmit = async () => {
    try {
      if (editingId) {
        await updateCustomer(editingId, form);
      } else {
        await createCustomer(form);
      }
      setForm({ firstName: '', lastName: '', dni: '', email: '' });
      setEditingId(null);
      setShowForm(false);
      fetchCustomers();
    } catch (err) {
      setError('Error al guardar cliente');
    }
  };

  const handleEdit = (customer) => {
    setForm({
      firstName: customer.firstName,
      lastName: customer.lastName,
      dni: customer.dni,
      email: customer.email,
    });
    setEditingId(customer.id);
    setShowForm(true);
  };

  const handleDelete = async (id) => {
    try {
      await deleteCustomer(id);
      fetchCustomers();
    } catch (err) {
      setError('Error al eliminar cliente');
    }
  };

  const handleCancel = () => {
    setEditingId(null);
    setForm({ firstName: '', lastName: '', dni: '', email: '' });
    setShowForm(false);
  };

  return (
    <div>
      {/* Header */}
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-xl font-medium text-gray-900">Clientes</h1>
          <p className="text-sm text-gray-400 mt-0.5">Gestión de clientes del bazar</p>
        </div>
        <button
          onClick={() => setShowForm(true)}
          className="bg-blue-500 hover:bg-blue-600 text-white text-sm font-medium px-4 py-2 rounded-lg transition-colors"
        >
          + Nuevo cliente
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
            {editingId ? 'Editar cliente' : 'Nuevo cliente'}
          </h2>
          <div className="grid grid-cols-2 gap-3 mb-4">
            <input
              className="border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              placeholder="Nombre"
              value={form.firstName}
              onChange={e => setForm({ ...form, firstName: e.target.value })}
            />
            <input
              className="border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              placeholder="Apellido"
              value={form.lastName}
              onChange={e => setForm({ ...form, lastName: e.target.value })}
            />
            <input
              className="border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              placeholder="DNI"
              value={form.dni}
              onChange={e => setForm({ ...form, dni: e.target.value })}
            />
            <input
              className="border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              placeholder="Email"
              type="email"
              value={form.email}
              onChange={e => setForm({ ...form, email: e.target.value })}
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
              <th className="text-left px-4 py-3 text-xs text-gray-400 font-medium">Apellido</th>
              <th className="text-left px-4 py-3 text-xs text-gray-400 font-medium">DNI</th>
              <th className="text-left px-4 py-3 text-xs text-gray-400 font-medium">Email</th>
              <th className="text-left px-4 py-3 text-xs text-gray-400 font-medium">Acciones</th>
            </tr>
          </thead>
          <tbody>
            {customers.map((c, i) => (
              <tr key={c.id} className={i % 2 === 0 ? 'bg-white' : 'bg-gray-50'}>
                <td className="px-4 py-3 text-gray-400">{c.id}</td>
                <td className="px-4 py-3 text-gray-900 font-medium">{c.firstName}</td>
                <td className="px-4 py-3 text-gray-600">{c.lastName}</td>
                <td className="px-4 py-3 text-gray-600">{c.dni}</td>
                <td className="px-4 py-3 text-gray-600">{c.email}</td>
                <td className="px-4 py-3">
                  <div className="flex gap-2">
                    <button
                      onClick={() => handleEdit(c)}
                      className="text-xs text-blue-600 hover:text-blue-800 font-medium"
                    >
                      Editar
                    </button>
                    <button
                      onClick={() => handleDelete(c.id)}
                      className="text-xs text-red-500 hover:text-red-700 font-medium"
                    >
                      Eliminar
                    </button>
                  </div>
                </td>
              </tr>
            ))}
            {customers.length === 0 && (
              <tr>
                <td colSpan={6} className="px-4 py-8 text-center text-gray-400 text-sm">
                  No hay clientes cargados
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
