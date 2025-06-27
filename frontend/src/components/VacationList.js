import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import api from '../services/api';

const VacationList = ({ refreshTrigger }) => {
  const { isAdmin } = useAuth();
  const [vacations, setVacations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [statusFilter, setStatusFilter] = useState('ALL');

  useEffect(() => {
    fetchVacations();
  }, [refreshTrigger]);

  const fetchVacations = async () => {
    try {
      setLoading(true);
      const response = await api.get('/vacations');
      setVacations(response.data);
    } catch (error) {
      console.error('Błąd pobierania wniosków:', error);
      setError('Błąd pobierania wniosków urlopowych');
    } finally {
      setLoading(false);
    }
  };

  const handleStatusUpdate = async (vacationId, status, comment = '') => {
    try {
      await api.put(`/vacations/${vacationId}/status`, {
        status,
        adminComment: comment
      });
      
      // Odśwież listę
      fetchVacations();
    } catch (error) {
      console.error('Błąd aktualizacji statusu:', error);
      alert('Błąd aktualizacji statusu wniosku');
    }
  };

  const handleCancel = async (vacationId) => {
    if (window.confirm('Czy na pewno chcesz anulować ten wniosek?')) {
      try {
        await api.delete(`/vacations/${vacationId}`);
        fetchVacations();
      } catch (error) {
        console.error('Błąd anulowania wniosku:', error);
        alert('Błąd anulowania wniosku');
      }
    }
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'PENDING': return 'bg-yellow-100 text-yellow-800';
      case 'APPROVED': return 'bg-green-100 text-green-800';
      case 'REJECTED': return 'bg-red-100 text-red-800';
      case 'CANCELLED': return 'bg-gray-100 text-gray-800';
      default: return 'bg-gray-100 text-gray-800';
    }
  };

  const getStatusText = (status) => {
    switch (status) {
      case 'PENDING': return 'Oczekuje';
      case 'APPROVED': return 'Zatwierdzony';
      case 'REJECTED': return 'Odrzucony';
      case 'CANCELLED': return 'Anulowany';
      default: return status;
    }
  };

  const filteredVacations = vacations.filter(vacation => {
    if (statusFilter === 'ALL') return true;
    return vacation.status === statusFilter;
  });

  if (loading) {
    return (
      <div className="flex justify-center py-8">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-500"></div>
      </div>
    );
  }

  return (
    <div className="bg-white shadow rounded-lg">
      <div className="px-4 py-5 sm:p-6">
        <div className="flex justify-between items-center mb-4">
          <h3 className="text-lg leading-6 font-medium text-gray-900">
            {isAdmin ? 'Wszystkie wnioski urlopowe' : 'Moje wnioski urlopowe'}
          </h3>
          
          <select
            value={statusFilter}
            onChange={(e) => setStatusFilter(e.target.value)}
            className="px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-blue-500 focus:border-blue-500"
          >
            <option value="ALL">Wszystkie</option>
            <option value="PENDING">Oczekujące</option>
            <option value="APPROVED">Zatwierdzone</option>
            <option value="REJECTED">Odrzucone</option>
            <option value="CANCELLED">Anulowane</option>
          </select>
        </div>

        {error && (
          <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
            {error}
          </div>
        )}

        {filteredVacations.length === 0 ? (
          <p className="text-gray-500 text-center py-8">
            {statusFilter === 'ALL' 
              ? 'Brak wniosków urlopowych' 
              : `Brak wniosków o statusie: ${getStatusText(statusFilter)}`
            }
          </p>
        ) : (
          <div className="overflow-x-auto">
            <table className="min-w-full divide-y divide-gray-200">
              <thead className="bg-gray-50">
                <tr>
                  {isAdmin && (
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Pracownik
                    </th>
                  )}
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Okres
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Dni
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Status
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Powód
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Akcje
                  </th>
                </tr>
              </thead>
              <tbody className="bg-white divide-y divide-gray-200">
                {filteredVacations.map((vacation) => (
                  <tr key={vacation.id}>
                    {isAdmin && (
                      <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                        {vacation.userFullName}
                        <div className="text-xs text-gray-500">{vacation.userEmail}</div>
                      </td>
                    )}
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                      {vacation.startDate} - {vacation.endDate}
                      <div className="text-xs text-gray-500">
                        Złożony: {new Date(vacation.createdAt).toLocaleDateString()}
                      </div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                      {vacation.daysCount}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <span className={`inline-flex px-2 py-1 text-xs font-semibold rounded-full ${getStatusColor(vacation.status)}`}>
                        {getStatusText(vacation.status)}
                      </span>
                    </td>
                    <td className="px-6 py-4 text-sm text-gray-900 max-w-xs truncate">
                      {vacation.reason || '-'}
                      {vacation.adminComment && (
                        <div className="text-xs text-red-600 mt-1">
                          Admin: {vacation.adminComment}
                        </div>
                      )}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm font-medium space-x-2">
                      {isAdmin && vacation.status === 'PENDING' && (
                        <>
                          <button
                            onClick={() => handleStatusUpdate(vacation.id, 'APPROVED')}
                            className="text-green-600 hover:text-green-900"
                          >
                            Zatwierdź
                          </button>
                          <button
                            onClick={() => {
                              const comment = prompt('Komentarz (opcjonalnie):');
                              if (comment !== null) {
                                handleStatusUpdate(vacation.id, 'REJECTED', comment);
                              }
                            }}
                            className="text-red-600 hover:text-red-900"
                          >
                            Odrzuć
                          </button>
                        </>
                      )}
                      {!isAdmin && vacation.status === 'PENDING' && (
                        <button
                          onClick={() => handleCancel(vacation.id)}
                          className="text-red-600 hover:text-red-900"
                        >
                          Anuluj
                        </button>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
};

export default VacationList;