import { apiService } from './api';

export interface Report {
  id: string;
  name: string;
  description: string;
  reportType: string;
  userId: string;
  dataSources: ReportDataSource[];
  configuration: Record<string, any>;
  createdAt: string;
  updatedAt: string;
  status: string;
  isPublic: boolean;
}

export interface ReportDataSource {
  id: string;
  name: string;
  sourceType: string;
  connectionString: string;
  configuration: Record<string, any>;
  query: string;
  parameters: Record<string, any>;
}

export interface ReportExecution {
  id: string;
  reportId: string;
  parameters: Record<string, any>;
  userId: string;
  startedAt: string;
  completedAt?: string;
  status: string;
  errorMessage?: string;
}

export interface ReportResult {
  executionId: string;
  data: any[];
  visualizations: ReportVisualization[];
  summary: Record<string, any>;
  generatedAt: string;
  status: string;
}

export interface ReportVisualization {
  id: string;
  type: string;
  title: string;
  configuration: Record<string, any>;
  dataSource: string;
  position: Record<string, any>;
}

export interface ReportSchedule {
  id: string;
  reportId: string;
  scheduleType: string;
  cronExpression: string;
  userId: string;
  parameters: Record<string, any>;
  createdAt: string;
  isActive: boolean;
  nextRun: string;
}

export interface ReportTemplate {
  id: string;
  name: string;
  description: string;
  reportType: string;
  userRole: string;
  template: Record<string, any>;
  createdBy: string;
  createdAt: string;
  isPublic: boolean;
}

export interface ReportAnalytics {
  reportId: string;
  executionCount: number;
  averageExecutionTime: number;
  lastExecuted: string;
  popularParameters: Record<string, any>;
  userUsage: Record<string, any>;
}

export interface ReportExport {
  id: string;
  executionId: string;
  format: string;
  fileUrl: string;
  createdAt: string;
  expiresAt: string;
}

class AdvancedReportingService {
  private baseUrl = '/api/analytics/reports';

  async createReport(name: string, description: string, reportType: string, userId: string, dataSources: ReportDataSource[], configuration: Record<string, any>): Promise<Report> {
    return apiService.createAdvancedReport(name, description, reportType, userId, dataSources, configuration);
  }

  async updateReport(reportId: string, name: string, description: string, dataSources: ReportDataSource[], configuration: Record<string, any>): Promise<Report> {
    return apiService.updateAdvancedReport(reportId, name, description, dataSources, configuration);
  }

  async getReport(reportId: string): Promise<Report> {
    return apiService.getAdvancedReport(reportId);
  }

  async getUserReports(userId: string, reportType?: string): Promise<Report[]> {
    return apiService.getAdvancedReports(userId, reportType);
  }

  async getPublicReports(reportType?: string): Promise<Report[]> {
    return apiService.getPublicReports(reportType);
  }

  async executeReport(reportId: string, parameters: Record<string, any>, userId: string): Promise<ReportExecution> {
    return apiService.executeReport(reportId, parameters, userId);
  }

  async getExecution(executionId: string): Promise<ReportExecution> {
    return apiService.getExecution(executionId);
  }

  async getReportResult(executionId: string): Promise<ReportResult> {
    return apiService.getReportResult(executionId);
  }

  async getReportExecutions(reportId: string): Promise<ReportExecution[]> {
    return apiService.getReportExecutions(reportId);
  }

  async scheduleReport(reportId: string, scheduleType: string, cronExpression: string, userId: string, parameters: Record<string, any>): Promise<ReportSchedule> {
    return apiService.scheduleReport(reportId, scheduleType, cronExpression, userId, parameters);
  }

  async cancelSchedule(scheduleId: string): Promise<void> {
    return apiService.cancelSchedule(scheduleId);
  }

  async createTemplate(name: string, description: string, reportType: string, userRole: string, template: Record<string, any>, createdBy: string): Promise<ReportTemplate> {
    return apiService.createReportTemplate(name, description, reportType, userRole, template, createdBy);
  }

  async getTemplates(reportType?: string, userRole?: string): Promise<ReportTemplate[]> {
    return apiService.getReportTemplates(reportType, userRole);
  }

  async getReportAnalytics(reportId: string): Promise<ReportAnalytics> {
    return apiService.getReportAnalytics(reportId);
  }

  async shareReport(reportId: string, userId: string, permission: string): Promise<void> {
    return apiService.shareReport(reportId, userId, permission);
  }

  async exportReport(executionId: string, format: string): Promise<ReportExport> {
    return apiService.exportReport(executionId, format);
  }

  async deleteReport(reportId: string): Promise<void> {
    return apiService.deleteReport(reportId);
  }
}

export const advancedReportingService = new AdvancedReportingService();
