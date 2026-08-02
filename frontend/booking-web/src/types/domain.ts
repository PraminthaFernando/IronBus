export interface Station {
  id: string;
  code: string;
  name: string;
}

export interface RouteSummary {
  id: string;
  code: string;
  name: string;
  active: boolean;
  createdAt: Date;
  version: number;
}

export interface RouteStationSummary {
  id: string;
  stationId: string;
  stationCode: string;
  stationName: string;
  sequenceNumber: number;
  distanceFromOriginKm: number;
  scheduledOffsetMinutes: number;
}

export interface RouteWithStations {
    routeId: string;
    code: string;
    name: string;
    stations: RouteStationSummary[];
}

export type JourneyStatus =
  | "SCHEDULED"
  | "BOARDING"
  | "DEPARTED"
  | "COMPLETED"
  | "CANCELLED";

export interface JourneySummary {
  id: string;
  trainId: string;
  trainCode: string;
  trainName: string;
  routeId: string;
  routeCode: string;
  routeName: string;
  departureTime: string;
  status: JourneyStatus;
}

export type TravelClass = "FIRST_CLASS" | "SECOND_CLASS" | "THIRD_CLASS";

export type SeatType = "WINDOW" | "AISLE" | "MIDDLE" | "OTHER";

export interface AvailableSeat {
  seatId: string;
  coachId: string;
  coachNumber: string;
  travelClass: TravelClass;
  seatNumber: string;
  seatType: SeatType;
  rowNumber: number | null;
  columnNumber: number | null;
  fareAmount: number;
  currency: string;
}

export interface AvailabilityResponse {
  journeyId: string;
  originStationId: string;
  destinationStationId: string;
  originSequence: number;
  destinationSequence: number;
  distanceKm: number;
  segmentSequences: number[];
  seats: AvailableSeat[];
}

export type BookingStatus = "PENDING" | "CONFIRMED" | "CANCELLED" | "EXPIRED";

export interface BookingResponse {
  id: string;
  reference: string;
  status: BookingStatus;
  journeyId: string;
  departureTime: string;
  seatId: string;
  coachNumber: string;
  seatNumber: string;
  travelClass: TravelClass;
  originCode: string;
  originName: string;
  destinationCode: string;
  destinationName: string;
  fareAmount: number;
  currency: string;
  createdAt: string;
}

export interface BookingSearchItem {
  id: string;
  reference: string;
  status: BookingStatus;
  journeyId: string;
  departureTime: string;
  originCode: string;
  originName: string;
  destinationCode: string;
  destinationName: string;
  coachNumber: string;
  seatNumber: string;
  travelClass: TravelClass;
  fareAmount: number;
  currency: string;
  createdAt: string;
}

export interface BookingSearchResponse {
  items: BookingSearchItem[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}
