import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuthStore } from '../store/authStore';
import { apiService } from '../services/api';
import { realtimeService, RealtimeEventType } from '../services/realtimeService';
import NeedsForm from '../components/NeedsForm';
import { 
  MapPin, 
  Users, 
  AlertTriangle, 
  CheckCircle, 
  Clock,
  TrendingUp,
  Activity,
  Plus
} from 'lucide-react';
import StockAlerts from '../components/StockAlerts';

const DashboardPage: React.FC = () => {
  const navigate = useNavigate();
  const { user } = useAuthStore();
  const [stats, setStats] = useState({
    activeRequests: 0,
    completedToday: 0,
    activeHelpers: 0,
    responseTime: '0h'
  });
  const [recentRequests, setRecentRequests] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [showNeedsForm, setShowNeedsForm] = useState(false);

  useEffect(() => {
    fetchDashboardData();
    
    // Set up real-time updates
    const unsubscribe = realtimeService.subscribe('needs.created' as RealtimeEventType, (event) => {
      console.log('New need created:', event.data);
      fetchDashboardData(); // Refresh dashboard data
    });
    
    const unsubscribeUpdated = realtimeService.subscribe('needs.updated' as RealtimeEventType, (event) => {
      console.log('Need updated:', event.data);
      fetchDashboardData(); // Refresh dashboard data
    });
    
    return () => {
      unsubscribe();
      unsubscribeUpdated();
    };
  }, []);

  const fetchDashboardData = async () => {
    try {
      setLoading(true);
      
      // Fetch recent requests
      const requestsData = await apiService.getRequests({ page: 0, size: 5 });
      setRecentRequests(requestsData.content || []);
      
      // Mock stats - in real app, these would come from dedicated dashboard API
      setStats({
        activeRequests: requestsData.totalElements || 0,
        completedToday: Math.floor(Math.random() * 20), // Mock data
        activeHelpers: Math.floor(Math.random() * 15), // Mock data
        responseTime: '2.3h' // Mock data
      });
    } catch (error) {
      console.error('Failed to fetch dashboard data:', error);
    } finally {
      setLoading(false);
    }
  };

  const getSeverityColor = (severity: number) => {
    switch (severity) {
      case 5: return 'bg-red-100 text-red-800';
      case 4: return 'bg-orange-100 text-orange-800';
      case 3: return 'bg-yellow-100 text-yellow-800';
      case 2: return 'bg-blue-100 text-blue-800';
      default: return 'bg-gray-100 text-gray-800';
    }
  };

  const handleFindShelter = async () => {
    try {
      // Try to get user's current location
      if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(
          async (position) => {
            const { latitude, longitude } = position.coords;
            // Navigate to map with shelter filter
            navigate(`/map?shelter=true&lat=${latitude}&lng=${longitude}`);
          },
          () => {
            // If location access denied, just navigate to map
            navigate('/map?shelter=true');
          }
        );
      } else {
        // Geolocation not supported, navigate to map
        navigate('/map?shelter=true');
      }
    } catch (error) {
      console.error('Error finding shelter:', error);
      navigate('/map?shelter=true');
    }
  };

  const statsData = [
    { name: 'Active Requests', value: stats.activeRequests.toString(), icon: AlertTriangle, color: 'text-red-600' },
    { name: 'Completed Today', value: stats.completedToday.toString(), icon: CheckCircle, color: 'text-green-600' },
    { name: 'Active Helpers', value: stats.activeHelpers.toString(), icon: Users, color: 'text-blue-600' },
    { name: 'Response Time', value: stats.responseTime, icon: Clock, color: 'text-yellow-600' },
  ];

  return (
    <div className="space-y-6">
      {/* Welcome Header */}
      <div className="bg-white shadow rounded-lg p-6">
        <h1 className="text-2xl font-bold text-gray-900">
          Welcome back, {user?.fullName}!
        </h1>
        <p className="text-gray-600 mt-1">
          Here's what's happening in your area today.
        </p>
      </div>

      {/* Stats Grid */}
      <div className="grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-4">
        {statsData.map((stat) => {
          const Icon = stat.icon;
          return (
            <div key={stat.name} className="bg-white overflow-hidden shadow rounded-lg">
              <div className="p-5">
                <div className="flex items-center">
                  <div className="flex-shrink-0">
                    <Icon className={`h-6 w-6 ${stat.color}`} />
                  </div>
                  <div className="ml-5 w-0 flex-1">
                    <dl>
                      <dt className="text-sm font-medium text-gray-500 truncate">
                        {stat.name}
                      </dt>
                      <dd className="text-lg font-medium text-gray-900">
                        {stat.value}
                      </dd>
                    </dl>
                  </div>
                </div>
              </div>
            </div>
          );
        })}
      </div>

      <div className="grid grid-cols-1 gap-6 lg:grid-cols-2">
        {/* Recent Requests */}
        <div className="bg-white shadow rounded-lg">
          <div className="px-4 py-5 sm:p-6">
            <h3 className="text-lg leading-6 font-medium text-gray-900">
              Recent Requests
            </h3>
            <div className="mt-5">
              {loading ? (
                <div className="flex items-center justify-center h-32">
                  <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
                </div>
              ) : (
                <div className="flow-root">
                  <ul className="-my-5 divide-y divide-gray-200">
                    {recentRequests.map((request) => (
                      <li key={request.id} className="py-4">
                        <div className="flex items-center space-x-4">
                          <div className="flex-shrink-0">
                            <MapPin className="h-5 w-5 text-gray-400" />
                          </div>
                          <div className="flex-1 min-w-0">
                            <p className="text-sm font-medium text-gray-900 truncate">
                              {request.category}
                            </p>
                            <p className="text-sm text-gray-500 truncate">
                              {request.address || 'Location not specified'}
                            </p>
                          </div>
                          <div className="flex items-center space-x-2">
                            <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${getSeverityColor(request.severity || 3)}`}>
                              Severity {request.severity || 3}
                            </span>
                            <span className="text-sm text-gray-500">
                              {new Date(request.createdAt).toLocaleTimeString()}
                            </span>
                          </div>
                        </div>
                      </li>
                    ))}
                    {recentRequests.length === 0 && (
                      <li className="py-4 text-center text-gray-500">
                        No recent requests found
                      </li>
                    )}
                  </ul>
                </div>
              )}
            </div>
          </div>
        </div>

        {/* Quick Actions */}
        <div className="bg-white shadow rounded-lg">
          <div className="px-4 py-5 sm:p-6">
            <h3 className="text-lg leading-6 font-medium text-gray-900">
              Quick Actions
            </h3>
            <div className="mt-5 grid grid-cols-2 gap-4">
              <button 
                onClick={() => setShowNeedsForm(true)}
                className="bg-blue-600 text-white p-4 rounded-lg hover:bg-blue-700 transition-colors"
              >
                <Plus className="h-6 w-6 mx-auto mb-2" />
                <span className="text-sm font-medium">Create Need</span>
              </button>
              <button 
                onClick={() => navigate('/tasks')}
                className="bg-green-600 text-white p-4 rounded-lg hover:bg-green-700 transition-colors"
                title="View available tasks to help"
              >
                <Users className="h-6 w-6 mx-auto mb-2" />
                <span className="text-sm font-medium">Help Someone</span>
              </button>
              <button 
                onClick={handleFindShelter}
                className="bg-yellow-600 text-white p-4 rounded-lg hover:bg-yellow-700 transition-colors"
                title="Find nearby emergency shelters"
              >
                <MapPin className="h-6 w-6 mx-auto mb-2" />
                <span className="text-sm font-medium">Find Shelter</span>
              </button>
              <button 
                onClick={() => navigate('/map')}
                className="bg-purple-600 text-white p-4 rounded-lg hover:bg-purple-700 transition-colors"
                title="View interactive map"
              >
                <Activity className="h-6 w-6 mx-auto mb-2" />
                <span className="text-sm font-medium">View Map</span>
              </button>
            </div>
          </div>
        </div>
      </div>

      {/* Stock Alerts */}
      <StockAlerts />

      {/* Activity Chart Placeholder */}
      <div className="bg-white shadow rounded-lg">
        <div className="px-4 py-5 sm:p-6">
          <h3 className="text-lg leading-6 font-medium text-gray-900">
            Activity Overview
          </h3>
          <div className="mt-5 h-64 flex items-center justify-center bg-gray-50 rounded-lg">
            <div className="text-center">
              <TrendingUp className="h-12 w-12 text-gray-400 mx-auto mb-4" />
              <p className="text-gray-500">Activity chart will be displayed here</p>
            </div>
          </div>
        </div>
      </div>

      {/* Needs Form Modal */}
      {showNeedsForm && (
        <NeedsForm
          onSuccess={() => {
            setShowNeedsForm(false);
            fetchDashboardData(); // Refresh data
          }}
          onCancel={() => setShowNeedsForm(false)}
        />
      )}
    </div>
  );
};

export default DashboardPage;
