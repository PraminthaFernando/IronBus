import { httpClient } from './httpClient';

export interface SystemStatus {
  service: string;
  status: string;
}

export async function getSystemStatus(): Promise<SystemStatus> {
  const response =
    await httpClient.get<SystemStatus>('/system/status');

  return response.data;
}