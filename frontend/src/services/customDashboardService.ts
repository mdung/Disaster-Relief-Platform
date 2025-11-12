import { apiService } from './api';

export interface Dashboard {
  id: string;
  name: string;
  description: string;
  userId: string;
  userRole: string;
  isPublic: boolean;
  layout: Record<string, any>;
  createdAt: string;
  updatedAt: string;
  isActive: boolean;
}

export interface DashboardWidget {
  id: string;
  dashboardId: string;
  widgetType: string;
  title: string;
  configuration: Record<string, any>;
  position: Record<string, any>;
  createdAt: string;
  updatedAt: string;
  isVisible: boolean;
}

export interface WidgetData {
  widgetId: string;
  data: any[];
  metadata: Record<string, any>;
  generatedAt: string;
}

export interface DashboardTemplate {
  id: string;
  name: string;
  description: string;
  userRole: string;
  template: Record<string, any>;
  createdBy: string;
  createdAt: string;
  isPublic: boolean;
}

export interface DashboardAnalytics {
  dashboardId: string;
  viewCount: number;
  uniqueViewers: number;
  lastViewed: string;
  popularWidgets: string[];
  userEngagement: Record<string, any>;
}

class CustomDashboardService {
  private baseUrl = '/api/analytics/dashboards';

  async createDashboard(name: string, description: string, userId: string, userRole: string, isPublic: boolean = false, layout: Record<string, any> = {}): Promise<Dashboard> {
    return apiService.createCustomDashboard(name, description, userId, userRole, isPublic, layout);
  }

  async updateDashboard(dashboardId: string, name: string, description: string, layout: Record<string, any>, isPublic: boolean = false): Promise<Dashboard> {
    return apiService.updateCustomDashboard(dashboardId, name, description, layout, isPublic);
  }

  async getDashboard(dashboardId: string): Promise<Dashboard> {
    return apiService.getCustomDashboard(dashboardId);
  }

  async getUserDashboards(userId: string, userRole: string): Promise<Dashboard[]> {
    return apiService.getCustomDashboards(userId, userRole);
  }

  async getPublicDashboards(): Promise<Dashboard[]> {
    return apiService.getPublicDashboards();
  }

  async addWidget(dashboardId: string, widgetType: string, title: string, configuration: Record<string, any>, position: Record<string, any>): Promise<DashboardWidget> {
    return apiService.addDashboardWidget(dashboardId, widgetType, title, configuration, position);
  }

  async updateWidget(widgetId: string, title: string, configuration: Record<string, any>, position: Record<string, any>, isVisible: boolean = true): Promise<DashboardWidget> {
    return apiService.updateDashboardWidget(widgetId, title, configuration, position, isVisible);
  }

  async getDashboardWidgets(dashboardId: string): Promise<DashboardWidget[]> {
    return apiService.getDashboardWidgets(dashboardId);
  }

  async getWidget(widgetId: string): Promise<DashboardWidget> {
    return apiService.getWidget(widgetId);
  }

  async getWidgetData(widgetId: string, filters: Record<string, any> = {}): Promise<WidgetData> {
    return apiService.getWidgetData(widgetId, filters);
  }

  async removeWidget(widgetId: string): Promise<void> {
    return apiService.removeWidget(widgetId);
  }

  async createTemplate(name: string, description: string, userRole: string, template: Record<string, any>, createdBy: string): Promise<DashboardTemplate> {
    return apiService.createDashboardTemplate(name, description, userRole, template, createdBy);
  }

  async getTemplates(userRole: string): Promise<DashboardTemplate[]> {
    return apiService.getDashboardTemplates(userRole);
  }

  async cloneDashboard(dashboardId: string, newName: string, userId: string): Promise<Dashboard> {
    return apiService.cloneDashboard(dashboardId, newName, userId);
  }

  async shareDashboard(dashboardId: string, userId: string, permission: string): Promise<void> {
    return apiService.shareDashboard(dashboardId, userId, permission);
  }

  async getDashboardAnalytics(dashboardId: string): Promise<DashboardAnalytics> {
    return apiService.getDashboardAnalytics(dashboardId);
  }

  async deleteDashboard(dashboardId: string): Promise<void> {
    return apiService.delete(`${this.baseUrl}/${dashboardId}`);
  }
}

export const customDashboardService = new CustomDashboardService();
