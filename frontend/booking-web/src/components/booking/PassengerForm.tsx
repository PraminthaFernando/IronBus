import { useEffect } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";

import {
  passengerSchema,
  type PassengerFormValues,
} from "../../schemas/passenger-schema";

interface PassengerFormProps {
  submitting: boolean;
  defaultValues?: Partial<PassengerFormValues>;
  onSubmit: (values: PassengerFormValues) => void;
}

export function PassengerForm({
  submitting,
  defaultValues,
  onSubmit,
}: PassengerFormProps) {
  const {
    register,
    handleSubmit,
    reset,
    formState: {
      errors,
      isValid,
    },
  } = useForm<PassengerFormValues>({
    resolver: zodResolver(passengerSchema),
    mode: "onChange",
    defaultValues: {
      name: defaultValues?.name ?? "",
      email: defaultValues?.email ?? "",
      phone: defaultValues?.phone ?? "",
    },
  });

  useEffect(() => {
    if (!defaultValues) {
      return;
    }

    reset({
      name: defaultValues.name ?? "",
      email: defaultValues.email ?? "",
      phone: defaultValues.phone ?? "",
    });
  }, [defaultValues, reset]);

  return (
    <form
      className="passenger-form"
      onSubmit={handleSubmit(onSubmit)}
      noValidate
    >
      <div className="form-field">
        <label htmlFor="passenger-name">
          Full name
        </label>

        <input
          id="passenger-name"
          type="text"
          autoComplete="name"
          placeholder="Enter passenger name"
          aria-invalid={Boolean(errors.name)}
          {...register("name")}
        />

        {errors.name && (
          <span
            className="form-field__error"
            role="alert"
          >
            {errors.name.message}
          </span>
        )}
      </div>

      <div className="passenger-form__row">
        <div className="form-field">
          <label htmlFor="passenger-email">
            Email address
          </label>

          <input
            id="passenger-email"
            type="email"
            autoComplete="email"
            placeholder="name@example.com"
            aria-invalid={Boolean(errors.email)}
            {...register("email")}
          />

          {errors.email && (
            <span
              className="form-field__error"
              role="alert"
            >
              {errors.email.message}
            </span>
          )}
        </div>

        <div className="form-field">
          <label htmlFor="passenger-phone">
            Phone number
          </label>

          <input
            id="passenger-phone"
            type="tel"
            autoComplete="tel"
            placeholder="+94 77 123 4567"
            aria-invalid={Boolean(errors.phone)}
            {...register("phone")}
          />

          {errors.phone && (
            <span
              className="form-field__error"
              role="alert"
            >
              {errors.phone.message}
            </span>
          )}
        </div>
      </div>

      <div className="passenger-form__actions">
        <button
          type="submit"
          className="button button--primary button--full"
          disabled={submitting || !isValid}
        >
          {submitting
            ? "Confirming booking..."
            : "Confirm and book seat"}
        </button>
      </div>
    </form>
  );
}