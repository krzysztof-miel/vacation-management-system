import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import VacationForm from '../components/VacationForm';
import VacationList from '../components/VacationList';
import UserManagement from '../components/UserManagement';
import VacationCalendar from '../components/VacationCalendar';

const Dashboard = () => {
  const { user, logout, isAdmin } = useAuth();
  const [showVacationForm, setShowVacationForm] = useState(false);
  const [refreshTrigger, setRefreshTrigger] = useState(0);
  const [activeSection, setActiveSection] = useState('overview');

  const handleVacationSuccess = () => {
    setRefreshTrigger(prev => prev + 1);
    // Po złożeniu wniosku, automatycznie przełączamy na listę wniosków.
    setActiveSection('vacations'); 
  };

  /**
   * Renderuje główną zawartość pulpitu w oparciu o aktywną sekcję.
   * Użycie instrukcji switch-case jest bardziej czytelne i skalowalne
   * niż powtarzające się warunkowe renderowanie JSX.
   */
  const renderContent = () => {
    switch (activeSection) {
      case 'overview':
        return (
          <>
            {/* Stats Cards */}
            <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
              <div className="bg-white overflow-hidden shadow rounded-lg">
                <div className="p-5">
                  <div className="flex items-center">
                    <div className="flex-shrink-0">
                      <div className="w-8 h-8 bg-blue-500 rounded-full flex items-center justify-center">
                        <span className="text-white text-sm font-medium">D</span>
                      </div>
                    </div>
                    <div className="ml-5 w-0 flex-1">
                      <dl>
                        <dt className="text-sm font-medium text-gray-500 truncate">
                          Dostępne dni urlopowe
                        </dt>
                        <dd className="text-lg font-medium text-gray-900">
                          {user?.availableVacationDays} dni
                        </dd>
                      </dl>
                    </div>
                  </div>
                </div>
              </div>
              <div className="bg-white overflow-hidden shadow rounded-lg">
                <div className="p-5">
                  <div className="flex items-center">
                    <div className="flex-shrink-0">
                      <div className="w-8 h-8 bg-green-500 rounded-full flex items-center justify-center">
                        <span className="text-white text-sm font-medium">W</span>
                      </div>
                    </div>
                    <div className="ml-5 w-0 flex-1">
                      <dl>
                        <dt className="text-sm font-medium text-gray-500 truncate">
                          Wykorzystane dni
                        </dt>
                        <dd className="text-lg font-medium text-gray-900">
                          {user?.usedVacationDays} dni
                        </dd>
                      </dl>
                    </div>
                  </div>
                </div>
              </div>
              <div className="bg-white overflow-hidden shadow rounded-lg">
                <div className="p-5">
                  <div className="flex items-center">
                    <div className="flex-shrink-0">
                      <div className="w-8 h-8 bg-purple-500 rounded-full flex items-center justify-center">
                        <span className="text-white text-sm font-medium">L</span>
                      </div>
                    </div>
                    <div className="ml-5 w-0 flex-1">
                      <dl>
                        <dt className="text-sm font-medium text-gray-500 truncate">
                          Łączna pula
                        </dt>
                        <dd className="text-lg font-medium text-gray-900">
                          {user?.totalVacationDays} dni
                        </dd>
                      </dl>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            {/* Quick Actions */}
            <div className="bg-white shadow rounded-lg mb-8">
              <div className="px-4 py-5 sm:p-6">
                <h3 className="text-lg leading-6 font-medium text-gray-900 mb-4">
                  Szybkie akcje
                </h3>
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
                  <button
                    onClick={() => setShowVacationForm(true)}
                    className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-3 rounded-md text-sm font-medium"
                  >
                    Złóż wniosek urlopowy
                  </button>
                  <button
                    onClick={() => setActiveSection('vacations')}
                    className="bg-green-600 hover:bg-green-700 text-white px-4 py-3 rounded-md text-sm font-medium"
                  >
                    Moje wnioski
                  </button>
                  <button
                    onClick={() => setActiveSection('calendar')}
                    className="bg-purple-600 hover:bg-purple-700 text-white px-4 py-3 rounded-md text-sm font-medium"
                  >
                    Kalendarz urlopów
                  </button>
                  {isAdmin && (
                    <button
                      onClick={() => setActiveSection('users')}
                      className="bg-orange-600 hover:bg-orange-700 text-white px-4 py-3 rounded-md text-sm font-medium"
                    >
                      Zarządzaj użytkownikami
                    </button>
                  )}
                </div>
              </div>
            </div>

            {/* Admin Section - widoczna tylko na pulpicie */}
            {isAdmin && (
              <div className="bg-white shadow rounded-lg">
                <div className="px-4 py-5 sm:p-6">
                  <h3 className="text-lg leading-6 font-medium text-gray-900 mb-4">
                    Panel Administratora
                  </h3>
                  <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                    <button
                      onClick={() => setActiveSection('vacations')}
                      className="bg-indigo-600 hover:bg-indigo-700 text-white px-4 py-3 rounded-md text-sm font-medium"
                    >
                      Wnioski do zatwierdzenia
                    </button>
                    <button
                      onClick={() => setActiveSection('users')}
                      className="bg-gray-600 hover:bg-gray-700 text-white px-4 py-3 rounded-md text-sm font-medium"
                    >
                      Lista wszystkich użytkowników
                    </button>
                    <button
                      onClick={() => setActiveSection('calendar')}
                      className="bg-yellow-600 hover:bg-yellow-700 text-white px-4 py-3 rounded-md text-sm font-medium"
                    >
                      Kalendarz urlopowy
                    </button>
                  </div>
                </div>
              </div>
            )}
          </>
        );
      case 'vacations':
        // Przekazujemy isAdmin, aby VacationList wiedział, co ma wyświetlić.
        return <VacationList refreshTrigger={refreshTrigger} isAdmin={isAdmin} />;
      case 'calendar':
        // Renderujemy komponent kalendarza.
        return <VacationCalendar />;
      case 'users':
        // Komponent do zarządzania użytkownikami jest renderowany tylko dla admina.
        return isAdmin ? <UserManagement /> : null;
      default:
        // W przypadku nieznanej sekcji, nic nie renderujemy.
        return null;
    }
  };

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <header className="bg-white shadow">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center py-6">
            <div>
              <h1 className="text-3xl font-bold text-gray-900">
                System Zarządzania Urlopami
              </h1>
              <p className="text-sm text-gray-600">
                Witaj, {user?.firstName} {user?.lastName} ({isAdmin ? 'Administrator' : 'Pracownik'})
              </p>
            </div>
            <button
              onClick={logout}
              className="bg-red-600 hover:bg-red-700 text-white px-4 py-2 rounded-md text-sm font-medium"
            >
              Wyloguj się
            </button>
          </div>
        </div>
      </header>

      {/* Main Content */}
      <main className="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
        <div className="px-4 py-6 sm:px-0">
          
          {/* Navigation */}
          <div className="bg-white shadow rounded-lg mb-6">
            <div className="px-4 py-3">
              <nav className="flex space-x-8">
                <button
                  onClick={() => setActiveSection('overview')}
                  className={`py-2 px-1 border-b-2 font-medium text-sm ${
                    activeSection === 'overview'
                      ? 'border-blue-500 text-blue-600'
                      : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                  }`}
                >
                  Przegląd
                </button>
                <button
                  onClick={() => setActiveSection('vacations')}
                  className={`py-2 px-1 border-b-2 font-medium text-sm ${
                    activeSection === 'vacations'
                      ? 'border-blue-500 text-blue-600'
                      : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                  }`}
                >
                  {isAdmin ? 'Wszystkie wnioski' : 'Moje wnioski'}
                </button>
                <button
                  onClick={() => setActiveSection('calendar')}
                  className={`py-2 px-1 border-b-2 font-medium text-sm ${
                    activeSection === 'calendar'
                      ? 'border-blue-500 text-blue-600'
                      : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                  }`}
                >
                  Kalendarz
                </button>
                {isAdmin && (
                  <button
                    onClick={() => setActiveSection('users')}
                    className={`py-2 px-1 border-b-2 font-medium text-sm ${
                      activeSection === 'users'
                        ? 'border-blue-500 text-blue-600'
                        : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                    }`}
                  >
                    Użytkownicy
                  </button>
                )}
              </nav>
            </div>
          </div>

          {/* Content based on active section */}
          {renderContent()}

        </div>
      </main>

      {/* Vacation Form Modal */}
      {showVacationForm && (
        <VacationForm
          onClose={() => setShowVacationForm(false)}
          onSuccess={handleVacationSuccess}
        />
      )}
    </div>
  );
};

export default Dashboard;