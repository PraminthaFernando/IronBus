import type {
  SeatType,
  TravelClass,
} from "../../types/domain";

export interface SeatFilterState {
  travelClass: TravelClass | "ALL";
  seatType: SeatType | "ALL";
}

interface SeatFiltersProps {
  value: SeatFilterState;
  onChange: (value: SeatFilterState) => void;
  disabled?: boolean;
}

export function SeatFilters({
  value,
  onChange,
  disabled = false,
}: SeatFiltersProps) {
  function handleTravelClassChange(
    nextValue: string,
  ) {
    onChange({
      ...value,
      travelClass:
        nextValue as SeatFilterState["travelClass"],
    });
  }

  function handleSeatTypeChange(
    nextValue: string,
  ) {
    onChange({
      ...value,
      seatType:
        nextValue as SeatFilterState["seatType"],
    });
  }

  function clearFilters() {
    onChange({
      travelClass: "ALL",
      seatType: "ALL",
    });
  }

  const hasActiveFilters =
    value.travelClass !== "ALL" ||
    value.seatType !== "ALL";

  return (
    <section
      className="seat-filters"
      aria-labelledby="seat-filter-title"
    >
      <div className="seat-filters__header">
        <div>
          <h2 id="seat-filter-title">
            Filter seats
          </h2>

          <p>
            Narrow the list by travel class or seat
            type.
          </p>
        </div>

        {hasActiveFilters && (
          <button
            type="button"
            className="button button--secondary"
            onClick={clearFilters}
            disabled={disabled}
          >
            Clear filters
          </button>
        )}
      </div>

      <div className="seat-filters__controls">
        <div className="form-field">
          <label htmlFor="travel-class-filter">
            Travel class
          </label>

          <select
            id="travel-class-filter"
            value={value.travelClass}
            onChange={event =>
              handleTravelClassChange(
                event.target.value,
              )
            }
            disabled={disabled}
          >
            <option value="ALL">
              All travel classes
            </option>

            <option value="FIRST_CLASS">
              First class
            </option>

            <option value="SECOND_CLASS">
              Second class
            </option>

            <option value="THIRD_CLASS">
              Third class
            </option>
          </select>
        </div>

        <div className="form-field">
          <label htmlFor="seat-type-filter">
            Seat type
          </label>

          <select
            id="seat-type-filter"
            value={value.seatType}
            onChange={event =>
              handleSeatTypeChange(
                event.target.value,
              )
            }
            disabled={disabled}
          >
            <option value="ALL">
              All seat types
            </option>

            <option value="WINDOW">
              Window
            </option>

            <option value="AISLE">
              Aisle
            </option>

            <option value="MIDDLE">
              Middle
            </option>

            <option value="OTHER">
              Other
            </option>
          </select>
        </div>
      </div>
    </section>
  );
}