import axios from "axios";
import { environment } from "../config/environment";
import { normalizeApiError } from "./api-error";

export const apiClient = axios.create({
  baseURL: environment.apiBaseUrl,
  timeout: 15_000,
  headers: {
    Accept: "application/json",
    "Content-Type": "application/json",
  },
});

apiClient.interceptors.request.use((config) => {
  const traceId = crypto.randomUUID();

  config.headers.set("X-Trace-Id", traceId);

  return config;
});

apiClient.interceptors.response.use(
  (response) => response,
  (error: unknown) => Promise.reject(normalizeApiError(error)),
);