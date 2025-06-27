import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import api from '../services/api';

const VacationForm = ({ onClose, onSuccess }) => {
  const { user } = useAuth();
  const [formData, setFormData] = useState({
    startDate: '',
    endDate: '',
    reason: ''
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const calculateDays = () => {
    if (formData.startDate && formData.endDate) {
      const start = new Date(formData.startDate);
      const end = new Date(formData.endDate);
      const diffTime = Math.abs(end - start);
      const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24)) + 1;
      return diffDays;
    }
    return 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    // Walidacja dat
    if (new Date(formData.startDate) > new Date(formData.endDate)) {
      setError('Data rozpoczęcia nie może być późniejsza niż data zakończenia');
      setLoading(false);
      return;
    }

    if (new Date(formData.startDate) < new Date()) {
      setError('Nie można składać wniosków na przeszłe daty');
      setLoading(false);
      return;
    }

    const requestedDays = calculateDays();
    if (requestedDays > user.availableVacationDays) {
      setError(`Nie masz wystarczająco dni urlopowych. Dostępne: ${user.availableVacationDays}, potrzebne: ${requestedDays}`);
      setLoading(false);
      return;
    }

    try {
      await api.post('/vacations', formData);
      onSuccess && onSuccess();
      onClose && onClose();
    } catch (error) {
      console.error('Błąd składania wniosku:', error);
      setError(error.response?.data?.message || 'Błąd składania wniosku urlopowego');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full z-50">
      <div className="relative top-20 mx-auto p-5 border w-11/12 max-w-md shadow-lg rounded-md bg-white">
        <div className="mt-3">
          <h3 className="text-lg font-medium text-gray-900 mb-4">
            Nowy wniosek urlopowy
          </h3>
          
          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Data rozpoczęcia
              </label>
              <input
                type="date"
                name="startDate"
                value={formData.startDate}
                onChange={handleInputChange}
                required
                min={new Date().toISOString().split('T')[0]}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-blue-500 focus:border-blue-500"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Data zakończenia
              </label>
              <input
                type="date"
                name="endDate"
                value={formData.endDate}
                onChange={handleInputChange}
                required
                min={formData.startDate || new Date().toISOString().split('T')[0]}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-blue-500 focus:border-blue-500"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Powód urlopu (opcjonalnie)
              </label>
              <textarea
                name="reason"
                value={formData.reason}
                onChange={handleInputChange}
                rows="3"
                placeholder="Np. urlop wypoczynkowy, sprawy rodzinne..."
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-blue-500 focus:border-blue-500"
              />
            </div>

            {formData.startDate && formData.endDate && (
              <div className="bg-blue-50 p-3 rounded-md">
                <p className="text-sm text-blue-700">
                  <strong>Liczba dni:</strong> {calculateDays()} dni
                </p>
                <p className="text-sm text-blue-700">
                  <strong>Dostępne dni:</strong> {user.availableVacationDays} dni
                </p>
                <p className="text-sm text-blue-700">
                  <strong>Pozostanie:</strong> {user.availableVacationDays - calculateDays()} dni
                </p>
              </div>
            )}

            {error && (
              <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded">
                {error}
              </div>
            )}

            <div className="flex space-x-3 pt-4">
              <button
                type="submit"
                disabled={loading}
                className="flex-1 bg-blue-600 hover:bg-blue-700 text-white py-2 px-4 rounded-md font-medium disabled:opacity-50"
              >
                {loading ? 'Składanie...' : 'Złóż wniosek'}
              </button>
              <button
                type="button"
                onClick={onClose}
                className="flex-1 bg-gray-300 hover:bg-gray-400 text-gray-700 py-2 px-4 rounded-md font-medium"
              >
                Anuluj
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default VacationForm;