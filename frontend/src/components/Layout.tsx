import React from 'react';
import { Link, useLocation } from 'react-router-dom';
import { useAuthStore } from '../store/authStore';
import { 
  MapIcon, 
  HomeIcon, 
  UserIcon, 
  CogIcon,
  LogOutIcon,
  BellIcon,
  PackageIcon,
  ClipboardListIcon,
  MountainIcon,
  SatelliteIcon,
  FlameIcon,
  ShieldIcon,
  BuildingIcon,
  DownloadIcon,
  ActivityIcon,
  MessageCircleIcon,
  DollarSignIcon,
  GraduationCapIcon,
  GlobeIcon,
  BarChart3Icon,
  BrainIcon,
  ZapIcon
} from 'lucide-react';

interface LayoutProps {
  children: React.ReactNode;
}

const Layout: React.FC<LayoutProps> = ({ children }) => {
  const location = useLocation();
  const { user, logout } = useAuthStore();

  const navigation = [
    { name: 'Dashboard', href: '/dashboard', icon: HomeIcon },
    { name: 'Map', href: '/map', icon: MapIcon },
    { name: 'Terrain', href: '/terrain', icon: MountainIcon },
    { name: 'Satellite', href: '/satellite', icon: SatelliteIcon },
    { name: 'Heatmap', href: '/heatmap', icon: FlameIcon },
    { name: 'Geofencing', href: '/geofencing', icon: ShieldIcon },
    { name: 'Indoor', href: '/indoor', icon: BuildingIcon },
    { name: 'Offline Maps', href: '/offline-maps', icon: DownloadIcon },
    { name: 'Location Analytics', href: '/location-analytics', icon: ActivityIcon },
    { name: 'Communication', href: '/communication', icon: MessageCircleIcon },
    { name: 'Financial', href: '/financial', icon: DollarSignIcon },
    { name: 'Training', href: '/training', icon: GraduationCapIcon },
    { name: 'Integration', href: '/integration', icon: GlobeIcon },
    { name: 'Analytics', href: '/analytics', icon: BarChart3Icon },
    { name: 'Real-time Intelligence', href: '/realtime', icon: ActivityIcon },
    { name: 'AI & ML', href: '/ai', icon: BrainIcon },
    { name: 'Security', href: '/security', icon: ShieldIcon },
    { name: 'Optimization', href: '/optimization', icon: ZapIcon },
    { name: 'Tasks', href: '/tasks', icon: ClipboardListIcon },
    { name: 'Inventory', href: '/inventory', icon: PackageIcon },
    { name: 'Profile', href: '/profile', icon: UserIcon },
  ];

  if (user?.role === 'ADMIN') {
    navigation.push({ name: 'Admin', href: '/admin', icon: CogIcon });
  }

  const isActive = (path: string) => location.pathname === path;

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Top Navigation */}
      <nav className="bg-white shadow-sm border-b">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between h-16">
            <div className="flex">
              <div className="flex-shrink-0 flex items-center">
                <h1 className="text-xl font-bold text-gray-900">
                  Relief Platform
                </h1>
              </div>
              <div className="hidden sm:ml-6 sm:flex sm:space-x-8">
                {navigation.map((item) => {
                  const Icon = item.icon;
                  return (
                    <Link
                      key={item.name}
                      to={item.href}
                      className={`inline-flex items-center px-1 pt-1 border-b-2 text-sm font-medium ${
                        isActive(item.href)
                          ? 'border-blue-500 text-gray-900'
                          : 'border-transparent text-gray-500 hover:border-gray-300 hover:text-gray-700'
                      }`}
                    >
                      <Icon className="w-4 h-4 mr-2" />
                      {item.name}
                    </Link>
                  );
                })}
              </div>
            </div>
            <div className="flex items-center space-x-4">
              <button className="p-2 text-gray-400 hover:text-gray-500">
                <BellIcon className="w-5 h-5" />
              </button>
              <div className="flex items-center space-x-2">
                <span className="text-sm text-gray-700">{user?.fullName}</span>
                <span className="text-xs text-gray-500 bg-gray-100 px-2 py-1 rounded">
                  {user?.role}
                </span>
                <button
                  onClick={logout}
                  className="p-2 text-gray-400 hover:text-gray-500"
                  title="Logout"
                >
                  <LogOutIcon className="w-5 h-5" />
                </button>
              </div>
            </div>
          </div>
        </div>
      </nav>

      {/* Main Content */}
      <main className="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
        {children}
      </main>
    </div>
  );
};

export default Layout;
