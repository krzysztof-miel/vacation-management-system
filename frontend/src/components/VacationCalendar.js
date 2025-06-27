import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import api from '../services/api';

const VacationCalendar = () => {
  const { isAdmin } = useAuth();
  const [currentDate, setCurrentDate] = useState(new Date());
  const [vacations, setVacations] = useState([]);
  const [loading, setLoading] = useState(false);
  const [selectedDate, setSelectedDate] = useState(null);
  const [dayVacations, setDayVacations] = useState([]);

  const currentMonth = currentDate.getMonth();
  const currentYear = currentDate.getFullYear();

  useEffect(() => {
    fetchMonthVacations();
  }, [currentMonth, currentYear]);

  const fetchMonthVacations = async () => {
    try {
      setLoading(true);
      
      // Pobierz pierwszy i ostatni dzień miesiąca
      const startDate = new Date(currentYear, currentMonth, 1);
      const endDate = new Date(currentYear, currentMonth + 1, 0);
      
      const startDateStr = startDate.toISOString().split('T')[0];
      const endDateStr = endDate.toISOString().split('T')[0];

      const response = await api.get(`/vacations/calendar?startDate=${startDateStr}&endDate=${endDateStr}`);
      setVacations(response.data);
    } catch (error) {
      console.error('Błąd pobierania kalendarza:', error);
    } finally {
      setLoading(false);
    }
  };

  const fetchDayVacations = async (date) => {
    try {
      const dateStr = date.toISOString().split('T')[0];
      const response = await api.get(`/vacations/calendar/${dateStr}`);
      setDayVacations(response.data);
      setSelectedDate(date);
    } catch (error) {
      console.error('Błąd pobierania urlopów na dzień:', error);
    }
  };

  const navigateMonth = (direction) => {
    const newDate = new Date(currentDate);
    newDate.setMonth(currentMonth + direction);
    setCurrentDate(newDate);
    setSelectedDate(null);
    setDayVacations([]);
  };

  const isDateInVacation = (date) => {
    const dateStr = date.toISOString().split('T')[0];
    return vacations.some(vacation => 
      dateStr >= vacation.startDate && dateStr <= vacation.endDate
    );
  };

  const getVacationsForDate = (date) => {
    const dateStr = date.toISOString().split('T')[0];
    return vacations.filter(vacation => 
      dateStr >= vacation.startDate && dateStr <= vacation.endDate
    );
  };

  const getDaysInMonth = () => {
    const firstDay = new Date(currentYear, currentMonth, 1);
    const lastDay = new Date(currentYear, currentMonth + 1, 0);
    const daysInMonth = lastDay.getDate();
    const startingDayOfWeek = firstDay.getDay();
    
    const days = [];
    
    // Dodaj puste komórki na początku
    for (let i = 0; i < startingDayOfWeek; i++) {
      days.push(null);
    }
    
    // Dodaj dni miesiąca
    for (let day = 1; day <= daysInMonth; day++) {
      days.push(new Date(currentYear, currentMonth, day));
    }
    
    return days;
  };

  const formatMonth = (date) => {
    return date.toLocaleDateString('pl-PL', { 
      month: 'long', 
      year: 'numeric' 
    });
  };

  const isToday = (date) => {
    if (!date) return false;
    const today = new Date();
    return date.toDateString() === today.toDateString();
  };

  const isPastDate = (date) => {
    if (!date) return false;
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    return date < today;
  };

  const days = getDaysInMonth();
  const weekDays = ['Nd', 'Pn', 'Wt', 'Śr', 'Cz', 'Pt', 'Sb'];

  return (
    <div className="bg-white shadow rounded-lg">
      <div className="px-4 py-5 sm:p-6">
        <div className="flex justify-between items-center mb-6">
          <h3 className="text-lg leading-6 font-medium text-gray-900">
            Kalendarz urlopów
          </h3>
          <div className="flex items-center space-x-4">
            <button
              onClick={() => navigateMonth(-1)}
              className="p-2 hover:bg-gray-100 rounded-md"
            >
              <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
              </svg>
            </button>
            <h4 className="text-xl font-semibold text-gray-900 min-w-[200px] text-center">
              {formatMonth(currentDate)}
            </h4>
            <button
              onClick={() => navigateMonth(1)}
              className="p-2 hover:bg-gray-100 rounded-md"
            >
              <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
              </svg>
            </button>
          </div>
        </div>

        {loading ? (
          <div className="flex justify-center py-8">
            <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-500"></div>
          </div>
        ) : (
          <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
            {/* Calendar Grid */}
            <div className="lg:col-span-2">
              <div className="grid grid-cols-7 gap-1 mb-2">
                {weekDays.map(day => (
                  <div key={day} className="p-2 text-center text-sm font-medium text-gray-500">
                    {day}
                  </div>
                ))}
              </div>
              
              <div className="grid grid-cols-7 gap-1">
                {days.map((date, index) => {
                  if (!date) {
                    return <div key={index} className="h-24"></div>;
                  }
                  
                  const hasVacation = isDateInVacation(date);
                  const dayVacations = getVacationsForDate(date);
                  const isSelected = selectedDate && date.toDateString() === selectedDate.toDateString();
                  
                  return (
                    <div
                      key={index}
                      onClick={() => fetchDayVacations(date)}
                      className={`
                        h-24 p-1 border border-gray-200 cursor-pointer transition-colors
                        ${isSelected ? 'bg-blue-100 border-blue-300' : 'hover:bg-gray-50'}
                        ${isToday(date) ? 'bg-blue-50 border-blue-200' : ''}
                        ${isPastDate(date) ? 'bg-gray-50 text-gray-400' : ''}
                      `}
                    >
                      <div className={`text-sm font-medium ${isToday(date) ? 'text-blue-600' : ''}`}>
                        {date.getDate()}
                      </div>
                      
                      {hasVacation && (
                        <div className="mt-1">
                          {dayVacations.slice(0, 2).map((vacation, idx) => (
                            <div
                              key={idx}
                              className="text-xs bg-green-100 text-green-800 px-1 py-0.5 rounded mb-0.5 truncate"
                              title={`${vacation.userFullName} - ${vacation.reason || 'Urlop'}`}
                            >
                              {isAdmin ? vacation.userFullName.split(' ')[0] : 'Urlop'}
                            </div>
                          ))}
                          {dayVacations.length > 2 && (
                            <div className="text-xs text-gray-500">
                              +{dayVacations.length - 2} więcej
                            </div>
                          )}
                        </div>
                      )}
                    </div>
                  );
                })}
              </div>
            </div>

            {/* Selected Day Details */}
            <div className="lg:col-span-1">
              <div className="bg-gray-50 rounded-lg p-4">
                <h5 className="text-sm font-medium text-gray-900 mb-3">
                  {selectedDate 
                    ? `Urlopy na ${selectedDate.toLocaleDateString('pl-PL')}` 
                    : 'Wybierz dzień aby zobaczyć szczegóły'
                  }
                </h5>
                
                {selectedDate && dayVacations.length > 0 ? (
                  <div className="space-y-2">
                    {dayVacations.map((vacation) => (
                      <div key={vacation.id} className="bg-white p-3 rounded border">
                        <div className="font-medium text-sm text-gray-900">
                          {vacation.userFullName}
                        </div>
                        <div className="text-xs text-gray-500">
                          {vacation.userEmail}
                        </div>
                        <div className="text-xs text-gray-600 mt-1">
                          {vacation.startDate} - {vacation.endDate}
                        </div>
                        {vacation.reason && (
                          <div className="text-xs text-gray-600 mt-1">
                            <strong>Powód:</strong> {vacation.reason}
                          </div>
                        )}
                        <div className="text-xs mt-1">
                          <span className="inline-flex px-2 py-1 text-xs font-semibold rounded-full bg-green-100 text-green-800">
                            {vacation.daysCount} dni
                          </span>
                        </div>
                      </div>
                    ))}
                  </div>
                ) : selectedDate && dayVacations.length === 0 ? (
                  <p className="text-sm text-gray-500">
                    Brak urlopów w tym dniu
                  </p>
                ) : null}
              </div>

              {/* Legend */}
              <div className="bg-gray-50 rounded-lg p-4 mt-4">
                <h5 className="text-sm font-medium text-gray-900 mb-3">
                  Legenda
                </h5>
                <div className="space-y-2 text-xs">
                  <div className="flex items-center">
                    <div className="w-4 h-4 bg-blue-50 border border-blue-200 rounded mr-2"></div>
                    <span>Dzisiaj</span>
                  </div>
                  <div className="flex items-center">
                    <div className="w-4 h-4 bg-green-100 rounded mr-2"></div>
                    <span>Dni z urlopami</span>
                  </div>
                  <div className="flex items-center">
                    <div className="w-4 h-4 bg-blue-100 border border-blue-300 rounded mr-2"></div>
                    <span>Wybrany dzień</span>
                  </div>
                  <div className="flex items-center">
                    <div className="w-4 h-4 bg-gray-50 rounded mr-2"></div>
                    <span>Przeszłość</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default VacationCalendar;