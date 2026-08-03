import axios from "axios";
import type { ApiErrorResponse } from "../types/api-error";

export function getApiError(error: unknown): ApiErrorResponse | null {
  if (!axios.isAxiosError<ApiErrorResponse>(error)) return null;
  return error.response?.data ?? null;
}
