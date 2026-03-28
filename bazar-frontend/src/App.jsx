import { Routes, Route, NavLink } from 'react-router-dom'
import Products from './pages/Products'
import Customers from './pages/Customers'
import Sales from './pages/Sales'

export default function App() {
  return (
    <div className="flex h-screen bg-gray-50">

      {/* Sidebar */}
      <aside className="w-48 bg-white border-r border-gray-200 flex flex-col">

        {/* Logo */}
        <div className="px-4 py-5 border-b border-gray-200">
          <div className="flex items-center gap-3">
            <div className="w-8 h-8 bg-blue-500 rounded-lg flex items-center justify-center text-white font-semibold text-sm">B</div>
            <div>
              <p className="text-sm font-medium text-gray-900">Bazar API</p>
              <p className="text-xs text-gray-400">Admin Panel</p>
            </div>
          </div>
        </div>

        {/* Nav */}
        <nav className="flex-1 px-2 py-4 space-y-1">
          <p className="text-xs text-gray-400 px-2 mb-2">MENÚ</p>
          <NavLink
            to="/products"
            className={({ isActive }) =>
              `flex items-center gap-2 px-3 py-2 rounded-lg text-sm transition-colors ${
                isActive
                  ? 'bg-blue-50 text-blue-700 font-medium'
                  : 'text-gray-600 hover:bg-gray-100'
              }`
            }
          >
            <span className="w-2 h-2 rounded-sm bg-current" />
            Productos
          </NavLink>
          <NavLink
            to="/customers"
            className={({ isActive }) =>
              `flex items-center gap-2 px-3 py-2 rounded-lg text-sm transition-colors ${
                isActive
                  ? 'bg-blue-50 text-blue-700 font-medium'
                  : 'text-gray-600 hover:bg-gray-100'
              }`
            }
          >
            <span className="w-2 h-2 rounded-sm bg-current" />
            Clientes
          </NavLink>
          <NavLink
            to="/sales"
            className={({ isActive }) =>
              `flex items-center gap-2 px-3 py-2 rounded-lg text-sm transition-colors ${
                isActive
                  ? 'bg-blue-50 text-blue-700 font-medium'
                  : 'text-gray-600 hover:bg-gray-100'
              }`
            }
          >
            <span className="w-2 h-2 rounded-sm bg-current" />
            Ventas
          </NavLink>
        </nav>
      </aside>

      {/* Main content */}
      <main className="flex-1 overflow-auto p-8">
        <Routes>
          <Route path="/products" element={<Products />} />
          <Route path="/" element={<Products />} />
          <Route path="/customers" element={<Customers />} />
          <Route path="/sales" element={<Sales />} />
        </Routes>
      </main>

    </div>
  )
}