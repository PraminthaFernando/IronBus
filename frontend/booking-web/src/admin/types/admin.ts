export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
}
export interface ListQuery {
  search?: string;
  page?: number;
  size?: number;
  sort?: string;
}
export type JourneyStatus =
  | "SUSPENDED"
  | "SCHEDULED"
  | "BOARDING"
  | "DEPARTED"
  | "COMPLETED"
  | "CANCELLED";
export interface StationAdminSummary {
  id: string;
  code: string;
  name: string;
  active: boolean;
  version: number;
}
export interface RouteAdminSummary {
  id: string;
  code: string;
  name: string;
  active: boolean;
  stationCount: number;
  totalDistanceKm: number;
  version: number;
}
export interface RouteStationAdminItem {
  id?: string;
  stationId: string;
  stationCode?: string;
  stationName?: string;
  sequenceNumber: number;
  distanceFromOriginKm: number;
  scheduledOffsetMinutes: number;
}
export interface TrainAdminSummary {
  id: string;
  code: string;
  name: string;
  active: boolean;
  coachCount: number;
  reservedCoachCount: number;
  seatCount: number;
  version: number;
}
export interface CoachAdminSummary {
  id: string;
  trainId: string;
  coachNumber: string;
  travelClass: "FIRST_CLASS" | "SECOND_CLASS" | "THIRD_CLASS";
  reservationMode: "RESERVED" | "UNRESERVED";
  active: boolean;
  seatCount: number;
  version: number;
}
export interface SeatAdminSummary {
  id: string;
  coachId: string;
  seatNumber: string;
  seatType: "OTHER" | "WINDOW" | "AISLE" | "MIDDLE";
  rowNumber: number | null;
  columnNumber: number | null;
  active: boolean;
  version: number;
}
export interface JourneyAdminSummary {
  id: string;
  trainId: string;
  trainCode: string;
  routeId: string;
  routeCode: string;
  departureTime: string;
  status: JourneyStatus;
  bookingCount: number;
  occupancyPercentage: number;
  version: number;
}
export interface JourneyListQuery extends ListQuery {
  routeId?: string;
  trainId?: string;
  status?: JourneyStatus;
  from?: string;
  to?: string;
}
export interface AdminConfigurationWarning {
  code: string;
  resourceType: "ROUTE" | "TRAIN" | "COACH" | "JOURNEY";
  resourceId: string;
  message: string;
}
export interface AdminDashboardSummary {
  stations: { total: number; active: number };
  routes: { total: number; active: number };
  trains: { total: number; active: number };
  coaches: { total: number; reserved: number; unreserved: number };
  seats: { total: number; active: number };
  journeys: {
    total: number;
    upcoming: number;
    today: number;
    cancelled: number;
  };
  warnings: AdminConfigurationWarning[];
  upcomingJourneys: JourneyAdminSummary[];
}
