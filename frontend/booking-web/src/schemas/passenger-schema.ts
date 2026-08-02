import { z } from "zod";

export const passengerSchema = z.object(
    {
        name: z.string().trim().min(2).max(150),
        email: z.string().trim().email().max(254),
        phone: z.string().trim().min(7).max(30),
    }
);

export type PassengerFormValues = z.infer<typeof passengerSchema>;
