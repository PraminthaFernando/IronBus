import axios from "axios";
import type { ApiError } from "../types/api-error";

export function getApiError(error: unknown): ApiError | null {
  if (!axios.isAxiosError<ApiError>(error)) return null;
  return error.response?.data ?? null;
}
