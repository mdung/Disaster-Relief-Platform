import { apiService } from './api';

export interface MiningJob {
  id: string;
  name: string;
  description: string;
  algorithm: string;
  dataSources: string[];
  parameters: Record<string, any>;
  userId: string;
  createdAt: string;
  startedAt?: string;
  completedAt?: string;
  status: string;
  isActive: boolean;
}

export interface MiningResult {
  jobId: string;
  patterns: DataPattern[];
  insights: DataInsight[];
  recommendations: string[];
  confidenceScore: number;
  generatedAt: string;
}

export interface DataPattern {
  id: string;
  patternType: string;
  description: string;
  confidence: number;
  frequency: number;
  dataPoints: any[];
  insights: string[];
  discoveredAt: string;
}

export interface DataInsight {
  id: string;
  insightType: string;
  title: string;
  description: string;
  confidence: number;
  impact: string;
  dataSource: string;
  parameters: Record<string, any>;
  generatedAt: string;
  recommendations: string[];
}

export interface PredictiveModel {
  id: string;
  name: string;
  modelType: string;
  targetVariable: string;
  features: string[];
  parameters: Record<string, any>;
  userId: string;
  createdAt: string;
  status: string;
  accuracy: number;
}

export interface ModelPrediction {
  id: string;
  modelId: string;
  inputData: Record<string, any>;
  prediction: Record<string, any>;
  confidence: number;
  generatedAt: string;
}

export interface AnomalyDetection {
  id: string;
  dataSource: string;
  detectionType: string;
  parameters: Record<string, any>;
  anomalies: Anomaly[];
  threshold: number;
  detectedAt: string;
}

export interface Anomaly {
  id: string;
  type: string;
  description: string;
  severity: number;
  data: Record<string, any>;
  detectedAt: string;
}

export interface TrendAnalysis {
  id: string;
  dataSource: string;
  trendType: string;
  startDate: string;
  endDate: string;
  trends: Trend[];
  forecast: Record<string, any>;
  generatedAt: string;
}

export interface Trend {
  id: string;
  name: string;
  direction: string;
  strength: number;
  startDate: string;
  endDate: string;
  data: Record<string, any>;
}

export interface CorrelationAnalysis {
  id: string;
  dataSource: string;
  variables: string[];
  correlations: Correlation[];
  significance: number;
  generatedAt: string;
}

export interface Correlation {
  variable1: string;
  variable2: string;
  coefficient: number;
  pValue: number;
  strength: string;
}

export interface ClusteringResult {
  id: string;
  dataSource: string;
  algorithm: string;
  parameters: Record<string, any>;
  clusters: Cluster[];
  silhouetteScore: number;
  generatedAt: string;
}

export interface Cluster {
  id: string;
  name: string;
  size: number;
  centroid: Record<string, any>;
  characteristics: string[];
}

export interface MiningAnalytics {
  dataSource: string;
  totalJobs: number;
  successfulJobs: number;
  averageExecutionTime: number;
  discoveredPatterns: number;
  generatedInsights: number;
  modelAccuracy: number;
  lastAnalyzed: string;
}

class DataMiningService {
  private baseUrl = '/api/analytics/data-mining';

  async createMiningJob(name: string, description: string, algorithm: string, dataSources: string[], parameters: Record<string, any>, userId: string): Promise<MiningJob> {
    return apiService.createMiningJob(name, description, algorithm, dataSources, parameters, userId);
  }

  async executeJob(jobId: string): Promise<MiningJob> {
    return apiService.executeMiningJob(jobId);
  }

  async getJob(jobId: string): Promise<MiningJob> {
    return apiService.getMiningJob(jobId);
  }

  async getUserJobs(userId: string): Promise<MiningJob[]> {
    return apiService.getMiningJobs(userId);
  }

  async getMiningResult(jobId: string): Promise<MiningResult> {
    return apiService.getMiningResult(jobId);
  }

  async discoverPatterns(dataSource: string, patternType: string, filters: Record<string, any>): Promise<DataPattern[]> {
    return apiService.discoverPatterns(dataSource, patternType, filters);
  }

  async generateInsights(dataSource: string, insightType: string, parameters: Record<string, any>): Promise<DataInsight[]> {
    return apiService.generateInsights(dataSource, insightType, parameters);
  }

  async createPredictiveModel(name: string, modelType: string, targetVariable: string, features: string[], parameters: Record<string, any>, userId: string): Promise<PredictiveModel> {
    return apiService.createPredictiveModel(name, modelType, targetVariable, features, parameters, userId);
  }

  async makePrediction(modelId: string, inputData: Record<string, any>): Promise<ModelPrediction> {
    return apiService.makePrediction(modelId, inputData);
  }

  async detectAnomalies(dataSource: string, detectionType: string, parameters: Record<string, any>): Promise<AnomalyDetection> {
    return apiService.detectAnomalies(dataSource, detectionType, parameters);
  }

  async analyzeTrends(dataSource: string, trendType: string, startDate: string, endDate: string): Promise<TrendAnalysis> {
    return apiService.analyzeTrends(dataSource, trendType, startDate, endDate);
  }

  async findCorrelations(dataSource: string, variables: string[]): Promise<CorrelationAnalysis> {
    return apiService.findCorrelations(dataSource, variables);
  }

  async performClustering(dataSource: string, algorithm: string, parameters: Record<string, any>): Promise<ClusteringResult> {
    return apiService.performClustering(dataSource, algorithm, parameters);
  }

  async getMiningAnalytics(dataSource: string): Promise<MiningAnalytics> {
    return apiService.getMiningAnalytics(dataSource);
  }

  async deleteJob(jobId: string): Promise<void> {
    return apiService.deleteMiningJob(jobId);
  }
}

export const dataMiningService = new DataMiningService();
