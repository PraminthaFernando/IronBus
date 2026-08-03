export interface ApiErrorResponse {
  timestamp: string;
  status: number;
  code: string;
  message: string;
  path: string;
  traceId?: string;
  fieldErrors?: Record<string, string[]>;
}
