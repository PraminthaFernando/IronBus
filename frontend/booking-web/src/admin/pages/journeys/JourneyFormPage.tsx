import { zodResolver } from "@hookform/resolvers/zod";
import { useEffect, useRef } from "react";
import { useForm } from "react-hook-form";
import { useNavigate, useParams } from "react-router-dom";

import {
  journeySchema,
  type JourneyFormValues,
} from "../../schemas/forms";
import {
  mutations,
  useJourney,
  useRoutes,
  useTrains,
} from "../../hooks/resource-hooks";
import {
  ErrorState,
  Loading,
  PageHeader,
} from "../../components/ui";
import type { ApiError } from "../../../api/api-error";

export function JourneyFormPage() {
  const { journeyId } = useParams();
  const isEditing = Boolean(journeyId);
  const navigate = useNavigate();

  /*
   * Only fetch the existing journey in edit mode.
   */
  const journeyQuery = useJourney(
    isEditing ? journeyId : undefined
  );

  /*
   * Routes and trains are required only while scheduling.
   * The hooks should accept an enabled flag.
   */
  const routesQuery = useRoutes({
    page: 0,
    size: 100,
  });

  const trainsQuery = useTrains({
    page: 0,
    size: 100,
  });

  const createMutation =
    mutations.journeyCreate();

  const updateMutation =
    mutations.journeyUpdate(
      journeyId ?? ""
    );

  const form =
    useForm<JourneyFormValues>({
      resolver: zodResolver(
        journeySchema
      ),
      defaultValues: {
        routeId: "",
        trainId: "",
        departureTime: "",
        status: "SCHEDULED",
      },
    });

  /*
   * Reset only once when the edit data first
   * arrives. Background refetches will not
   * overwrite unsaved form changes.
   */
  const initialResetDone =
    useRef(false);

  useEffect(() => {
    if (
      !isEditing ||
      !journeyQuery.data ||
      initialResetDone.current
    ) {
      return;
    }

    form.reset({
      /*
       * These IDs are retained in form state,
       * but they are not editable or sent in
       * the update request.
       */
      routeId:
        journeyQuery.data.routeId,
      trainId:
        journeyQuery.data.trainId,
      departureTime:
        toDateTimeLocalValue(
          journeyQuery.data
            .departureTime
        ),
      status:
        journeyQuery.data.status,
    });

    initialResetDone.current = true;
  }, [
    isEditing,
    journeyQuery.data,
    form,
  ]);

  if (
    isEditing &&
    journeyQuery.isPending
  ) {
    return <Loading />;
  }

  if (
    isEditing &&
    journeyQuery.isError
  ) {
    return (
      <ErrorState
        error={
          journeyQuery.error as ApiError
        }
        retry={() =>
          journeyQuery.refetch()
        }
      />
    );
  }

  if (
    !isEditing &&
    (routesQuery.isPending ||
      trainsQuery.isPending)
  ) {
    return <Loading />;
  }

  if (
    !isEditing &&
    (routesQuery.isError ||
      trainsQuery.isError)
  ) {
    const error =
      routesQuery.error ??
      trainsQuery.error;

    return (
      <ErrorState
        error={error as ApiError}
        retry={() => {
          void routesQuery.refetch();
          void trainsQuery.refetch();
        }}
      />
    );
  }

  const activeMutation =
    isEditing
      ? updateMutation
      : createMutation;

  const isPending =
    activeMutation.isPending;

  const handleSubmit =
    form.handleSubmit((values) => {
      const departureTime =
        toOffsetDateTime(
          values.departureTime
        );

      const onSuccess = () =>
        navigate("/admin/journeys");

      if (isEditing) {
        if (
          !journeyId ||
          !journeyQuery.data
        ) {
          return;
        }

        /*
         * Route and train cannot be changed
         * after the journey is scheduled.
         */
        updateMutation.mutate(
          {
            departureTime,
            status: values.status,
            expectedVersion:
              journeyQuery.data
                .version,
          },
          { onSuccess }
        );

        return;
      }

      /*
       * Scheduling requires selected route
       * and train IDs.
       */
      createMutation.mutate(
        {
          routeId: values.routeId,
          trainId: values.trainId,
          departureTime,
          status: "SUSPENDED"
        },
        { onSuccess }
      );
    });

  return (
    <>
      <PageHeader
        eyebrow="Operations"
        title={
          isEditing
            ? "Edit journey"
            : "Schedule journey"
        }
        description={
          isEditing
            ? "Update the departure time and operational status. The assigned route and train cannot be changed."
            : "Select a route, train, and departure time for the new service."
        }
      />

      <section className="panel form-panel">
        {activeMutation.isError && (
          <ErrorState
            error={
              activeMutation.error as ApiError
            }
          />
        )}

        <form
          className="admin-form"
          onSubmit={handleSubmit}
          noValidate
        >
          {isEditing ? (
            <EditJourneyResources
              routeCode={
                journeyQuery.data
                  ?.routeCode ?? ""
              }
              routeName={
                journeyQuery.data
                  ?.routeCode
              }
              trainCode={
                journeyQuery.data
                  ?.trainCode ?? ""
              }
              trainName={
                journeyQuery.data
                  ?.trainCode
              }
            />
          ) : (
            <>
              <div className="form-field">
                <label htmlFor="journey-route">
                  Route
                </label>

                <select
                  id="journey-route"
                  disabled={isPending}
                  aria-invalid={Boolean(
                    form.formState
                      .errors.routeId
                  )}
                  aria-describedby={
                    form.formState
                      .errors.routeId
                      ? "journey-route-error"
                      : undefined
                  }
                  {...form.register(
                    "routeId"
                  )}
                >
                  <option value="">
                    Select a route
                  </option>

                  {routesQuery.data
                    ?.content?.map(
                      (route) => (
                        <option
                          key={route.id}
                          value={route.id}
                        >
                          {route.code} —{" "}
                          {route.name}
                        </option>
                      )
                    )}
                </select>

                {form.formState.errors
                  .routeId && (
                  <p
                    id="journey-route-error"
                    className="field-error"
                    role="alert"
                  >
                    {
                      form.formState
                        .errors.routeId
                        .message
                    }
                  </p>
                )}
              </div>

              <div className="form-field">
                <label htmlFor="journey-train">
                  Train
                </label>

                <select
                  id="journey-train"
                  disabled={isPending}
                  aria-invalid={Boolean(
                    form.formState
                      .errors.trainId
                  )}
                  aria-describedby={
                    form.formState
                      .errors.trainId
                      ? "journey-train-error"
                      : undefined
                  }
                  {...form.register(
                    "trainId"
                  )}
                >
                  <option value="">
                    Select a train
                  </option>

                  {trainsQuery.data
                    ?.content?.map(
                      (train) => (
                        <option
                          key={train.id}
                          value={train.id}
                        >
                          {train.code} —{" "}
                          {train.name}
                        </option>
                      )
                    )}
                </select>

                {form.formState.errors
                  .trainId && (
                  <p
                    id="journey-train-error"
                    className="field-error"
                    role="alert"
                  >
                    {
                      form.formState
                        .errors.trainId
                        .message
                    }
                  </p>
                )}
              </div>
            </>
          )}

          <div className="form-field">
            <label htmlFor="journey-departure">
              Departure
            </label>

            <input
              id="journey-departure"
              type="datetime-local"
              disabled={isPending}
              aria-invalid={Boolean(
                form.formState.errors
                  .departureTime
              )}
              aria-describedby={
                form.formState.errors
                  .departureTime
                  ? "journey-departure-error"
                  : undefined
              }
              {...form.register(
                "departureTime"
              )}
            />

            {form.formState.errors
              .departureTime && (
              <p
                id="journey-departure-error"
                className="field-error"
                role="alert"
              >
                {
                  form.formState.errors
                    .departureTime
                    .message
                }
              </p>
            )}
          </div>

          {isEditing && (
            <div className="form-field">
              <label htmlFor="journey-status">
                Status
              </label>

              <select
                id="journey-status"
                disabled={isPending}
                aria-invalid={Boolean(
                  form.formState.errors
                    .status
                )}
                aria-describedby={
                  form.formState.errors
                    .status
                    ? "journey-status-error"
                    : undefined
                }
                {...form.register(
                  "status"
                )}
              >
                {getAllowedStatuses(
                  journeyQuery.data?.status
                ).map((status) => (
                  <option
                    key={status}
                    value={status}
                  >
                    {formatStatus(
                      status
                    )}
                  </option>
                ))}
              </select>

              {form.formState.errors
                .status && (
                <p
                  id="journey-status-error"
                  className="field-error"
                  role="alert"
                >
                  {
                    form.formState
                      .errors.status
                      .message
                  }
                </p>
              )}

              <p className="field-hint">
                Only valid status
                transitions are available.
              </p>
            </div>
          )}

          <div className="form-actions">
            <button
              type="button"
              disabled={isPending}
              onClick={() =>
                navigate(
                  "/admin/journeys"
                )
              }
            >
              Cancel
            </button>

            <button
              type="submit"
              disabled={isPending}
              aria-busy={isPending}
            >
              {isPending
                ? "Saving…"
                : isEditing
                  ? "Save changes"
                  : "Schedule journey"}
            </button>
          </div>
        </form>
      </section>
    </>
  );
}

interface EditJourneyResourcesProps {
  routeCode: string;
  routeName?: string;
  trainCode: string;
  trainName?: string;
}

function EditJourneyResources({
  routeCode,
  routeName,
  trainCode,
  trainName,
}: EditJourneyResourcesProps) {
  return (
    <>
      <div className="form-field">
        <label htmlFor="journey-route">
          Route
        </label>

        <input
          id="journey-route"
          type="text"
          value={
            routeName
              ? `${routeCode} — ${routeName}`
              : routeCode
          }
          disabled
          readOnly
          aria-describedby="journey-route-hint"
        />

        <p
          id="journey-route-hint"
          className="field-hint"
        >
          The route cannot be changed
          after scheduling.
        </p>
      </div>

      <div className="form-field">
        <label htmlFor="journey-train">
          Train
        </label>

        <input
          id="journey-train"
          type="text"
          value={
            trainName
              ? `${trainCode} — ${trainName}`
              : trainCode
          }
          disabled
          readOnly
          aria-describedby="journey-train-hint"
        />

        <p
          id="journey-train-hint"
          className="field-hint"
        >
          The train cannot be changed
          after scheduling.
        </p>
      </div>
    </>
  );
}

type JourneyStatus =
  | "SUSPENDED"
  | "SCHEDULED"
  | "BOARDING"
  | "DEPARTED"
  | "COMPLETED"
  | "CANCELLED";

function getAllowedStatuses(
  currentStatus?: JourneyStatus
): JourneyStatus[] {
  if (!currentStatus) {
    return ["SCHEDULED"];
  }

  switch (currentStatus) {
    case "SCHEDULED":
      return [
        "SCHEDULED",
        "BOARDING",
        "CANCELLED",
      ];

    case "BOARDING":
      return [
        "BOARDING",
        "DEPARTED",
        "CANCELLED",
      ];

    case "DEPARTED":
      return [
        "DEPARTED",
        "COMPLETED",
      ];

    case "COMPLETED":
      return ["COMPLETED"];

    case "CANCELLED":
      return ["CANCELLED"];

    case "SUSPENDED":
      return ["SUSPENDED"];

    default:
      return ["SCHEDULED"];
  }
}

function formatStatus(
  status: JourneyStatus
): string {
  return status
    .toLowerCase()
    .split("_")
    .map(
      (word) =>
        word.charAt(0).toUpperCase() +
        word.slice(1)
    )
    .join(" ");
}

/**
 * Converts an ISO timestamp into the
 * format required by datetime-local.
 */
function toDateTimeLocalValue(
  value: string
): string {
  const date = new Date(value);

  if (Number.isNaN(date.getTime())) {
    return "";
  }

  const pad = (number: number) =>
    String(number).padStart(2, "0");

  return [
    date.getFullYear(),
    "-",
    pad(date.getMonth() + 1),
    "-",
    pad(date.getDate()),
    "T",
    pad(date.getHours()),
    ":",
    pad(date.getMinutes()),
  ].join("");
}

/**
 * Converts a datetime-local value into
 * the ISO timestamp expected by the backend.
 */
function toOffsetDateTime(
  value: string
): string {
  const date = new Date(value);

  if (Number.isNaN(date.getTime())) {
    throw new Error(
      "The departure time is invalid."
    );
  }

  return date.toISOString();
}