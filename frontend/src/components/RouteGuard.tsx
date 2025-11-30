import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuthStore } from '../store/authStore';

interface RouteGuardProps {
  children: React.ReactNode;
  requiredRole?: string;
  requiredPermissions?: string[];
  fallback?: React.ReactNode;
}

const RouteGuard: React.FC<RouteGuardProps> = ({ 
  children, 
  requiredRole, 
  requiredPermissions = [], 
  fallback 
}) => {
  const { user, isAuthenticated } = useAuthStore();

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  // Check role requirement
  if (requiredRole && user?.role !== requiredRole) {
    return (fallback || (
      <div className="min-h-screen flex items-center justify-center bg-gray-50">
        <div className="text-center">
          <h1 className="text-2xl font-bold text-gray-900 mb-4">Access Denied</h1>
          <p className="text-gray-600 mb-4">
            You don't have permission to access this page.
          </p>
          <p className="text-sm text-gray-500">
            Required role: {requiredRole}
          </p>
        </div>
      </div>
    )) as React.ReactElement;
  }

  // Check permission requirements
  if (requiredPermissions.length > 0) {
    const hasPermission = requiredPermissions.some(permission => {
      // This would be implemented based on your permission system
      // For now, we'll do basic role-based checks
      switch (permission) {
        case 'user:read':
        case 'user:write':
          return ['ADMIN', 'DISPATCHER'].includes(user?.role || '');
        case 'needs:read':
        case 'needs:write':
          return ['ADMIN', 'DISPATCHER', 'HELPER', 'RESIDENT'].includes(user?.role || '');
        case 'task:read':
        case 'task:write':
          return ['ADMIN', 'DISPATCHER', 'HELPER'].includes(user?.role || '');
        case 'inventory:read':
        case 'inventory:write':
          return ['ADMIN', 'DISPATCHER', 'HELPER'].includes(user?.role || '');
        case 'system:monitor':
        case 'system:config':
          return user?.role === 'ADMIN';
        default:
          return false;
      }
    });

    if (!hasPermission) {
      return (fallback || (
        <div className="min-h-screen flex items-center justify-center bg-gray-50">
          <div className="text-center">
            <h1 className="text-2xl font-bold text-gray-900 mb-4">Access Denied</h1>
            <p className="text-gray-600 mb-4">
              You don't have permission to access this page.
            </p>
            <p className="text-sm text-gray-500">
              Required permissions: {requiredPermissions.join(', ')}
            </p>
          </div>
        </div>
      )) as React.ReactElement;
    }
  }

  return <>{children}</> as React.ReactElement;
};

export default RouteGuard;



