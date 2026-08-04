import { z } from "zod";

export const stationSchema = z.object({
    code: z
        .string()
        .trim()
        .min(2)
        .max(10)
        .regex(/^[A-Z0-9-]+$/),
    name: z.string().trim().min(2).max(150),
    active: z.boolean(),
});

export const routeSchema = z.object({
    code: z
        .string()
        .trim()
        .min(2)
        .max(30)
        .regex(/^[A-Z0-9-]+$/),
    name: z.string().trim().min(3).max(150),
    active: z.boolean(),
});

export const trainSchema = routeSchema;

export const coachSchema = z.object({
    coachNumber: z.string().trim().min(1).max(20),
    travelClass: z.enum(["FIRST_CLASS", "SECOND_CLASS", "THIRD_CLASS"]),
    reservationMode: z.enum(["RESERVED", "UNRESERVED"]),
    active: z.boolean(),
});

export const seatSchema = z.object({
    seatNumber: z.string().trim().min(1).max(20),
    seatType: z.enum(["OTHER", "WINDOW", "AISLE", "MIDDLE"]),
    rowNumber: z.number().int().positive(),
    columnNumber: z.number().int().positive(),
    active: z.boolean(),
});

export const bulkSeatSchema = z.object({
    rows: z.number().int().min(1).max(100),
    columnSuffixes: z.string().trim().min(1),
    seatType: z.enum(["OTHER", "WINDOW", "AISLE", "MIDDLE"]),
});

export const journeySchema = z.object({
  routeId: z.string().min(1, "Route ID is required"),
  trainId: z.string().min(1, "Train ID is required"),
  departureTime: z.string().min(1, "Departure time is required"),
  status: z.enum([
    "SUSPENDED",
    "SCHEDULED",
    "BOARDING",
    "DEPARTED",
    "COMPLETED",
    "CANCELLED",
  ]),
});

export type StationFormValues = z.infer<typeof stationSchema>;
export type RouteFormValues = z.infer<typeof routeSchema>;
export type TrainFormValues = z.infer<typeof trainSchema>;
export type CoachFormValues = z.infer<typeof coachSchema>;
export type SeatFormValues = z.infer<typeof seatSchema>;
export type BulkSeatFormValues = z.infer<typeof bulkSeatSchema>;
export type JourneyFormValues = z.infer<typeof journeySchema>;
