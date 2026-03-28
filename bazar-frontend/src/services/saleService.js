import api from './api';

export const getSales = () => api.get('/api/sales');
export const getSaleById = (id) => api.get(`/api/sales/${id}`);
export const createSale = (data) => api.post('/api/sales', data);
export const updateSale = (id, data) => api.put(`/api/sales/${id}`, data);
export const deleteSale = (id) => api.delete(`/api/sales/${id}`);
export const getTopSale = () => api.get('/api/sales/top');
export const getSalesByDate = (date) => api.get(`/api/sales/by-date/${date}`);
export const getProductsBySaleId = (id) => api.get(`/api/sales/${id}/products`);