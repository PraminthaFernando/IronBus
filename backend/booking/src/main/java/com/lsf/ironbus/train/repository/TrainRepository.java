package com.lsf.ironbus.train.repository;

import com.lsf.ironbus.train.app.projection.TrainAdminProjection;
import com.lsf.ironbus.train.domain.Train;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TrainRepository
        extends JpaRepository<Train, UUID> {

    boolean existsByCodeIgnoreCase(String code);

    Optional<Train> findByIdAndActiveTrue(UUID id);

    List<Train> findAllByActiveTrueOrderByNameAsc();

    Optional<Train> findByCodeIgnoreCase(String code);

    long countByActiveTrue();

    Page<Train>
    findByCodeContainingIgnoreCaseOrNameContainingIgnoreCase(
            String code,
            String name,
            Pageable pageable
    );

    @Query("""
        select distinct t
        from Train t
        left join fetch t.coaches c
        left join fetch c.seats
        where t.id = :trainId
        """)
    Optional<Train> findByIdWithCoachesAndSeats(
            @Param("trainId") UUID trainId
    );

    @Query(
            value = """
                    select
                        t.id as id,
                        t.code as code,
                        t.name as name,
                        t.active as active,
                        count(distinct c.id) as coachCount,
                        count(
                            distinct case
                                when c.reservation_mode = 'RESERVED'
                                then c.id
                            end
                        ) as reservedCoachCount,
                        count(distinct s.id) as seatCount,
                        t.version as version
                    from ib_trains t
                    left join ib_coaches c
                        on c.train_id = t.id
                    left join ib_seats s
                        on s.coach_id = c.id
                    where (
                        :search = ''
                        or lower(t.code) like lower(
                            concat('%', :search, '%')
                        )
                        or lower(t.name) like lower(
                            concat('%', :search, '%')
                        )
                    )
                    group by
                        t.id,
                        t.code,
                        t.name,
                        t.active,
                        t.version
                    """,
            countQuery = """
                    select count(*)
                    from ib_trains t
                    where (
                        :search = ''
                        or lower(t.code) like lower(
                            concat('%', :search, '%')
                        )
                        or lower(t.name) like lower(
                            concat('%', :search, '%')
                        )
                    )
                    """,
            nativeQuery = true
    )
    Page<TrainAdminProjection> findAdminPage(
            @Param("search") String search,
            Pageable pageable
    );

    @Query(
            value = """
                select
                    t.id as id,
                    t.code as code,
                    t.name as name,
                    t.active as active,
                    count(distinct c.id) as coachCount,
                    count(
                        distinct case
                            when c.reservation_mode = 'RESERVED'
                            then c.id
                        end
                    ) as reservedCoachCount,
                    count(distinct s.id) as seatCount,
                    t.version as version
                from ib_trains t
                left join ib_coaches c
                    on c.train_id = t.id
                left join ib_seats s
                    on s.coach_id = c.id
                where t.id = :trainId
                group by
                    t.id,
                    t.code,
                    t.name,
                    t.active,
                    t.version
                """,
            nativeQuery = true
    )
    Optional<TrainAdminProjection> findAdminById(
            @Param("trainId") UUID trainId
    );

    @Query("""
        select distinct t
        from Train t
        left join fetch t.coaches
        where t.id = :trainId
        """)
    Optional<Train> findByIdWithCoaches(
            @Param("trainId") UUID trainId
    );
}