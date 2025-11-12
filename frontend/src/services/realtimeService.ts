import { useAuthStore } from '../store/authStore';

export type RealtimeEventType = 
  | 'needs.created'
  | 'needs.updated'
  | 'task.created'
  | 'task.assigned'
  | 'task.updated'
  | 'delivery.created'
  | 'inventory.updated'
  | 'heartbeat';

export interface RealtimeEvent {
  id: string;
  type: RealtimeEventType;
  ts: string;
  data: any;
}

export class RealtimeService {
  private eventSource: EventSource | null = null;
  private listeners: Map<RealtimeEventType, Set<(event: RealtimeEvent) => void>> = new Map();
  private reconnectAttempts = 0;
  private maxReconnectAttempts = 5;
  private reconnectDelay = 1000;

  connect(): void {
    if (this.eventSource) {
      this.disconnect();
    }

    const { token } = useAuthStore.getState();
    const url = `${process.env.REACT_APP_API_URL}/requests/stream${token ? `?token=${token}` : ''}`;
    
    this.eventSource = new EventSource(url);
    
    this.eventSource.onopen = () => {
      console.log('SSE connection opened');
      this.reconnectAttempts = 0;
    };

    this.eventSource.onmessage = (event) => {
      try {
        const data = JSON.parse(event.data);
        this.handleEvent(data);
      } catch (error) {
        console.error('Failed to parse SSE event:', error);
      }
    };

    this.eventSource.onerror = (error) => {
      console.error('SSE connection error:', error);
      this.handleReconnect();
    };

    // Listen for specific event types including smart automation and task management events
    Object.values([
      'needs.created', 'needs.updated', 'task.created', 'task.assigned', 'task.updated', 
      'delivery.created', 'inventory.updated', 'heartbeat',
      // Smart automation events
      'ai.categorization.completed', 'workflow.started', 'workflow.completed', 'workflow.failed',
      'notification.sent', 'escalation.triggered', 'dedupe.group.created', 'dedupe.group.merged',
      // Advanced task management events
      'task.dynamic.created', 'task.skill.matched', 'task.auto.assigned', 'task.dependency.ready',
      'task.workflow.updated', 'task.performance.updated', 'task.analytics.updated'
    ] as RealtimeEventType[]).forEach(eventType => {
      this.eventSource?.addEventListener(eventType, (event: any) => {
        try {
          const data = JSON.parse(event.data);
          this.handleEvent(data);
        } catch (error) {
          console.error(`Failed to parse ${eventType} event:`, error);
        }
      });
    });
  }

  disconnect(): void {
    if (this.eventSource) {
      this.eventSource.close();
      this.eventSource = null;
    }
  }

  subscribe(eventType: RealtimeEventType, callback: (event: RealtimeEvent) => void): () => void {
    if (!this.listeners.has(eventType)) {
      this.listeners.set(eventType, new Set());
    }
    
    this.listeners.get(eventType)!.add(callback);
    
    // Return unsubscribe function
    return () => {
      this.listeners.get(eventType)?.delete(callback);
    };
  }

  private handleEvent(event: RealtimeEvent): void {
    const callbacks = this.listeners.get(event.type);
    if (callbacks) {
      callbacks.forEach(callback => {
        try {
          callback(event);
        } catch (error) {
          console.error('Error in event callback:', error);
        }
      });
    }
  }

  private handleReconnect(): void {
    if (this.reconnectAttempts >= this.maxReconnectAttempts) {
      console.error('Max reconnection attempts reached');
      return;
    }

    this.reconnectAttempts++;
    const delay = this.reconnectDelay * Math.pow(2, this.reconnectAttempts - 1);
    
    console.log(`Reconnecting in ${delay}ms (attempt ${this.reconnectAttempts})`);
    
    setTimeout(() => {
      this.connect();
    }, delay);
  }

  isConnected(): boolean {
    return this.eventSource?.readyState === EventSource.OPEN;
  }

  getConnectionState(): number {
    return this.eventSource?.readyState ?? EventSource.CLOSED;
  }
}

export const realtimeService = new RealtimeService();

