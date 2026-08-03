import axios, { type AxiosError } from "axios";
import type { ApiErrorResponse } from "../types/api-error";

export class ApiError extends Error {
  readonly status: number;
  readonly code: string;
  readonly traceId?: string;
  readonly fieldErrors: Record<string, string[]>;

  constructor({
    status,
    code,
    message,
    traceId,
    fieldErrors,
  }: {
    status: number;
    code: string;
    message: string;
    traceId?: string;
    fieldErrors?: Record<string, string[]>;
  }) {
    super(message);

    this.name = "ApiError";
    this.status = status;
    this.code = code;
    this.traceId = traceId;
    this.fieldErrors = fieldErrors ?? {};
  }
}

export function normalizeApiError(error: unknown): ApiError {
  if (error instanceof ApiError) {
    return error;
  }

  if (axios.isAxiosError<ApiErrorResponse>(error)) {
    return normalizeAxiosError(error);
  }

  if (error instanceof Error) {
    return new ApiError({
      status: 0,
      code: "CLIENT_ERROR",
      message: error.message,
    });
  }

  return new ApiError({
    status: 0,
    code: "UNKNOWN_ERROR",
    message: "An unexpected error occurred.",
  });
}

function normalizeAxiosError(
  error: AxiosError<ApiErrorResponse>,
): ApiError {
  const response = error.response;
  const body = response?.data;

  if (body && typeof body === "object") {
    return new ApiError({
      status: response?.status ?? body.status ?? 0,
      code: body.code || "API_ERROR",
      message: body.message || "The request could not be completed.",
      traceId:
        body.traceId ??
        getHeaderValue(response?.headers, "x-trace-id"),
      fieldErrors: body.fieldErrors,
    });
  }

  if (error.code === "ECONNABORTED") {
    return new ApiError({
      status: 0,
      code: "REQUEST_TIMEOUT",
      message:
        "The request took too long. Please check your connection and try again.",
    });
  }

  if (!response) {
    return new ApiError({
      status: 0,
      code: "NETWORK_ERROR",
      message:
        "The service could not be reached. Please check your connection.",
    });
  }

  return new ApiError({
    status: response.status,
    code: "HTTP_ERROR",
    message: defaultMessageForStatus(response.status),
    traceId: getHeaderValue(response.headers, "x-trace-id"),
  });
}

function defaultMessageForStatus(status: number): string {
  switch (status) {
    case 400:
      return "Some of the submitted information is invalid.";
    case 404:
      return "The requested resource could not be found.";
    case 409:
      return "The operation conflicts with the current resource state.";
    case 429:
      return "Too many requests were sent. Please try again shortly.";
    case 500:
    case 502:
    case 503:
    case 504:
      return "The booking service is temporarily unavailable.";
    default:
      return "The request could not be completed.";
  }
}

function getHeaderValue(
  headers: unknown,
  name: string,
): string | undefined {
  if (
    headers &&
    typeof headers === "object" &&
    name in headers
  ) {
    const value = (headers as Record<string, unknown>)[name];

    return typeof value === "string" ? value : undefined;
  }

  return undefined;
}